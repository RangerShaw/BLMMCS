package algorithm.differenceSet;

import java.util.Arrays;
import java.util.BitSet;

public class FastBitSet {

    boolean[] bits;

    int hashCode = 0;


    public FastBitSet(int nbits) {
        bits = new boolean[nbits];
    }

    public FastBitSet(int nbits, BitSet bs) {
        bits = new boolean[nbits];
        bs.stream().forEach(e -> bits[e] = true);
    }

    public FastBitSet(int nbits, BitSet bs, boolean inverse) {
        bits = new boolean[nbits];
        Arrays.fill(bits, true);
        bs.stream().forEach(e -> bits[e] = false);
    }


    public void set(int n) {
        bits[n] = true;
    }

    public void set(int stt, int end) {
        for (int i = stt; i < end; i++)
            bits[i] = true;
    }

    public void setAll() {
        Arrays.fill(bits, true);
    }

    public void clear(int n) {
        bits[n] = false;
    }

    @Override
    public int hashCode() {
        if (hashCode != 0) return hashCode;
        for (boolean bit : bits)
            hashCode = (hashCode << 1) + (bit ? 1 : 0);
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode == ((FastBitSet) obj).hashCode;
    }

//    @Override
//    public int hashCode() {
//        if (hashCode != -1) return hashCode;
//        hashCode = Arrays.hashCode(bits);
//        return hashCode;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        int a = 1<<1;
//        return Arrays.compare(bits, ((FastBitSet) obj).bits) == 0;
//    }

    public BitSet toBitSet() {
        BitSet bs = new BitSet(bits.length);
        for (int i = 0; i < bits.length; i++)
            if (bits[i]) bs.set(i);
        return bs;
    }
    public BitSet toInverseBitSet() {
        BitSet bs = new BitSet(bits.length);
        for (int i = 0; i < bits.length; i++)
            if (!bits[i]) bs.set(i);
        return bs;
    }
}
