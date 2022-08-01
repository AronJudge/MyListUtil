
import java.util.Arrays;
import java.util.Iterator;

/**
 * ClassName ListUtil
 * Description TODO
 *
 * @Author liuwei
 * @Date 2022/7/29 下午12:02
 * @Version 1.0
 **/
public class ListUtil<T> implements Iterable<T> {
    private Object[] elementData;
    private int size;
    private int index;     // 记录遍历的位置
    private int curIndex;  // 记录当前遍历的位置
    private int lastIndex;  // 记录上次遍历的位置

    public ListUtil() {
        this(5);
    }

    public ListUtil(int size) {
        this.elementData = new Object[size];
        this.size = 0;
        this.index = -1;
    }

    /**
     * 添加元素, 主要用的就是add方法
     */
    public boolean add(T item) {
        if (size == elementData.length) {
            elementData = Arrays.copyOf(elementData, elementData.length * 2);
        }
        elementData[size] = item;
        size++;
        return true;
    }

    /**
     * 获取元素  用不到
     */
    public T get(int index) {
        T data = (T) new Object();
        if (index >= size || index < 0) {
            throw new RuntimeException("参数不合法");
        } else {
            data = (T) elementData[index];
        }
        return data;
    }

    /**
     * 获取元素个数 用不到
     */
    public int size() {
        return size;
    }

    /**
     * 删除元素  用不到
     */
    public boolean remove(int index) {
        if (index >= size || index < 0) {
            throw new RuntimeException("参数不合法");
        } else {
            for (int i = index; i < size; i++) {
                //元素的移动
                elementData[i] = elementData[i + 1];
            }
            elementData[size - 1] = null;//便于GC的回收
            size--;
        }

        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new str();
    }

    class str implements Iterator<T> {
        private str() {
            curIndex = index;
            lastIndex = curIndex;
        }

        // 第二/三次/四次遍历 接着上一次的结果继续遍历， 而不是从上一次的结果+1开始
        @Override
        public boolean hasNext() {
            if (index + 1 == curIndex) {
                return false;
            } else {
                if (lastIndex != -1) {
                    index = lastIndex;
                    lastIndex = -1;
                } else {
                    index = (index + 1) % size;
                }
                return true;
            }
        }

        @Override
        public T next() {
            int i = index;
            return (T) elementData[i];
        }
    }
}
