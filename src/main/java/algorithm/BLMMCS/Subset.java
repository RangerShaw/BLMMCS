package algorithm.BLMMCS;

import java.util.BitSet;
import java.util.stream.IntStream;

public class Subset {

    BitSet elements;

    /**
     * which elements in S cover this subset
     */
    BitSet covers;


    public Subset(BitSet _elements) {
        elements = _elements;
        covers = new BitSet(elements.size());
    }

    public Subset(BitSet _elements, BitSet totalCover) {
        elements = (BitSet) _elements.clone();
        covers = (BitSet) _elements.clone();
        covers.and(totalCover);
    }

    public int getCoverCount() {
        return covers.cardinality();
    }

    public int getFirstCover() {
        return covers.nextSetBit(0);
    }

    public IntStream getAllCovers() {
        return covers.stream();
    }

    public void addCover(int e) {
        covers.set(e);
    }

    public void removeCover(int e) {
        covers.clear(e);
    }

    public boolean hasElement(int e) {
        return elements.get(e);
    }

    // TODO: rewrite hashCode and equals
}
