import java.util.*;

public class Test {

    public static void main(String[] args) {
        LinkedList<Integer> l = new LinkedList<>();
        List<Iterator<Integer>> li = new ArrayList<>();
        for(int i=0; i<10; i++) {
            l.add(1);
            li.add(l.descendingIterator());

        }
        if(li.get(5).hasNext())
            li.get(6).remove();
        System.out.println(l);
    }

}