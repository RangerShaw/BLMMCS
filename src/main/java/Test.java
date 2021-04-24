import java.util.*;

public class Test {

    public static void main(String[] args) {
        boolean[] bl = new boolean[]{true,false,true,false};
        System.out.println(Arrays.hashCode(bl));
        BitSet bs = new BitSet();
        System.out.println(bs.hashCode());
    }

}
