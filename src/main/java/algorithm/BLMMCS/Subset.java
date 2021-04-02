package algorithm.BLMMCS;

import java.util.BitSet;
import java.util.stream.IntStream;

public class Subset {

    BitSet elements;

    public Subset(BitSet _elements) {
        elements = (BitSet) _elements.clone();
    }

    public boolean hasElement(int e) {
        return elements.get(e);
    }

    public int getCoverCount(BitSet coverElements) {
        BitSet intersection = (BitSet) coverElements.clone();
        intersection.and(elements);
        return intersection.cardinality();
    }

    /**
     * @return -1 if this subset has no critical cover w.r.t coverEle
     */
    public int getCritCover(BitSet coverEle) {
        BitSet intersec = (BitSet) coverEle.clone();
        intersec.and(elements);
        return intersec.cardinality() == 1 ? intersec.nextSetBit(0) : -1;
    }

    public IntStream getElements() {
        return elements.stream();
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Subset && ((Subset) obj).elements.equals(elements);
    }


//    BitSet elements;
//    /**
//     * which elements in S cover this subset
//     */
//    BitSet covers;
//
//
//    public Subset(BitSet _elements) {
//        elements = _elements;
//        covers = new BitSet(elements.size());
//    }
//
//    public Subset(BitSet _elements, BitSet totalCover) {
//        elements = (BitSet) _elements.clone();
//        covers = (BitSet) _elements.clone();
//        covers.and(totalCover);
//    }
//
//    public int getCoverCount() {
//        return covers.cardinality();
//    }
//
//    public int getFirstCover() {
//        return covers.nextSetBit(0);
//    }
//
//    public IntStream getAllCovers() {
//        return covers.stream();
//    }
//
//    public void addCover(int e) {
//        covers.set(e);
//    }
//
//    public void removeCover(int e) {
//        covers.clear(e);
//    }
//
//    public boolean hasElement(int e) {
//        return elements.get(e);
//    }

}
