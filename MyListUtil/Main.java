import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<Son> sonList = new ArrayList<>(10);
    static ListUtil<Son> sonListUtil = new ListUtil<>();

    static int index = 0;

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

        System.out.println("===============ArrayList Iterator==============");

        for (Son s : sonList) {

            if (s.getIndex() == 4) {
                break;
            }
        }

        System.out.println("===============ArrayList sleep==============");
        try {
            Thread.sleep(1000);

            for (Son s : sonList) {
                if (s.getIndex() == 7) {
                    break;
                }
            }

            System.out.println("===============ArrayList sleep==============");
            Thread.sleep(1000);
            for (Son s : sonList) {
                if (s.getIndex() == 4) {
                    break;
                }
            }
            System.out.println("===============ArrayList sleep==============");
            Thread.sleep(1000);
            for (Son s : sonList) {
                s.getIndex();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
