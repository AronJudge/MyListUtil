# 《性能优化》仿照ArrayList，自定义了一种List工具。

> 改动虽小，但在对项目的需求详细的分析后，感觉还是比较有用的，性能优化的一个点。

## 1.0 为什么

> 为什么要做这个呢，还请让我墨迹一下

​	目前在做一个适配VR语音的工作，功能很简单，枯燥，但是需要干的事情着实不少。简单来说就是VR语义给到我这里，具体要分发到那里，那个模块需要我这里来进行判断。

​	各种模块我在这里分析和VR的大量语义，根据特性把各种各样的模块分为了那么四大模块类。那四个模块类很少，VR语义过来，拿到关键的keyword，这里因为数量很少，就四个，那就一个Switch 或者遍历就行嘛，就可以分发到我四大模块中了，很简单。但四大模块下面又有很多子模块，20 30 个的样子，怎样根据Json中keyWord 将数据分发到对应的子模块呢？单纯的根据KeyWork遍历或者Switch 硬编码，我肯定是不能容忍的，所以这里我使用的是 类似 OKHTTP 中拦截器的设计模式，（但是经过了自己的改造和适配，不知道还能不能叫做拦截器）。

大体就是 ：

​	主模块维护所有子模块的列表（引入正题），对主模块提供一个抽象父类，抽象父亲提供了两个抽象方法：

- checkIsMain(KeyWord)

- doConverter() 

  子类自己维护自己的KeyWord，继承了父类，实现这两个抽象方法。

​	具体的工作流程就是，数据进来， 主类回去遍历包含子类的列表，子类会检查KeyWord 是否是我的， 是我的我会拦截下来， 封装数据，不再遍历。如果不是我的，就调用下一个元素。直到遍历完全部子类。大体的思路就是这样，那我们要优化的是什么呢？

​	思考一下：

场景一，假如你在开车，想通过语音选择想要的音乐播放，你会说：

1. 打开XX音乐APP
2. 播放xx音乐
3. 下一首
4. 下一首
5. 哎哎 上一首

场景二， 假设你想打开空调，调节温度， 你会说：

1. 打开空调
2. 把温度调节到23度
3. OMG 太冷了，升高一点
4. 再降低一点

类似的场景还有很多， 又没有发现一个规律，人在操作一个场景的时候，短时间内的语音都是和这个场景有关的，所以是不是我们可以在语音分发选择模块的时候 ，保留当前遍历的结果，然后再次遍历的时候，我们基于上次遍历结果的位置进行遍历，那有很大机率第一次就会命中模块。从而降低了时间复杂度O(n) - O(1)。好了这就是的为什么？

## 2.0 怎么做

​	说到这里，我的第一个想法是利用LRU 去实现，LRU是维护一个最近经常访问的链表，但是链表没办法用到计算机的局部行缓存原理，而且会新增动态维护链表带来的开销，对于这部分的性能优化恐怕是有点大材小用了，甚至我们可以根据使用的频率在创建数组的时候把经常会被使用的模块放在数组前面，来减少遍历时间。所以就放弃了LRU的实现方式。那再看一下我们需要优化的功能，我们需要保存上次遍历的位置，下次遍历的时候在上次遍历的索引处开始遍历， 很大记录当前会直接命中我们要选择的模块（和上次相同），确定好需求后，那就开始做吧。

## 3.0 结果

其实整个的实现不是很难，可以看到后面提供的源码，在实现的过程中主要需要控制怎么在上一次遍历的基础上在遍历，而不是上次遍历的基础上+1的位置，这个细节是一个我比较难解决的一个点，调试了一上午才解决这个问题。然后就看一下测试结果吧。

ListUtil 测试结果

```java
===============ListUtil Iterator==============
Son index = 0
Son index = 1
Son index = 2
Son index = 3
Son index = 4
===============ListUtil sleep==============
Son index = 4
Son index = 5
Son index = 6
Son index = 7
===============ListUtil sleep==============
Son index = 7
Son index = 8
Son index = 9
Son index = 0
Son index = 1
Son index = 2
Son index = 3
Son index = 4
===============ListUtil sleep==============
Son index = 4
Son index = 5
Son index = 6
Son index = 7
Son index = 8
Son index = 9
Son index = 0
Son index = 1
Son index = 2
Son index = 3

Process finished with exit code 0

```

ArrayList 测试结果

```
Son index = 1
Son index = 2
Son index = 3
Son index = 4
===============ArrayList sleep==============
Son index = 1
Son index = 2
Son index = 3
Son index = 4
Son index = 5
Son index = 6
Son index = 7
===============ArrayList sleep==============
Son index = 1
Son index = 2
Son index = 3
Son index = 4
===============ArrayList sleep==============
Son index = 1
Son index = 2
Son index = 3
Son index = 4
Son index = 5
Son index = 6
Son index = 7
Son index = 8
Son index = 9
Son index = 10
```



测试程序

```java
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Son> sonList = new ArrayList<>(10);
    private static ListUtil<Son> sonListUtil = new ListUtil<>();

    private static int index = 0;

    public static void main(String[] args) {
        System.out.println("===============ArrayList==============");
        sonList.add(new Son(1));
        sonList.add(new Son(2));
        sonList.add(new Son(3));
        sonList.add(new Son(4));
        sonList.add(new Son(5));
        sonList.add(new Son(6));
        sonList.add(new Son(7));
        sonList.add(new Son(8));
        sonList.add(new Son(9));
        sonList.add(new Son(10)); // size = 10

        System.out.println("==============ListUtil===============");
        sonListUtil.add(new Son(0));
        sonListUtil.add(new Son(1));
        sonListUtil.add(new Son(2));
        sonListUtil.add(new Son(3));
        sonListUtil.add(new Son(4));
        sonListUtil.add(new Son(5));
        sonListUtil.add(new Son(6));
        sonListUtil.add(new Son(7));
        sonListUtil.add(new Son(8));
        sonListUtil.add(new Son(9)); // size = 10

        System.out.println("===============ListUtil Iterator==============");

        for (Son s : sonListUtil) {
            if (s.getIndex() == 4) {
                break;
            }
        }

        System.out.println("===============ListUtil sleep==============");
        try {
            Thread.sleep(1000);

            for (Son s : sonListUtil) {
                if (s.getIndex() == 7) {
                    break;
                }
            }

            System.out.println("===============ListUtil sleep==============");
            Thread.sleep(1000);
            for (Son s : sonListUtil) {
                if (s.getIndex() == 4) {
                    break;
                }
            }
            System.out.println("===============ListUtil sleep==============");
            Thread.sleep(1000);
            for (Son s : sonListUtil) {
                s.getIndex();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

```

