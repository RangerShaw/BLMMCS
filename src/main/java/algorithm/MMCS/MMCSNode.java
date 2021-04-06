package algorithm.MMCS;

import algorithm.BLMMCS.Subset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MMCSNode {

    private int nElements;

    private BitSet elements;

    private BitSet cand;

    /**
     * uncovered subsets
     */
    private List<Subset> uncov;

    /**
     * crit[i]: subsets for which element i is crucial
     */
    private ArrayList<ArrayList<Subset>> crit;

    private MMCSNode(int nEle) {
        nElements = nEle;
    }

    /**
     * for initiation only
     */
    public MMCSNode(int nEle, List<Subset> subsetsToCover) {
        nElements = nEle;
        elements = new BitSet(nElements);
        uncov = new ArrayList<>(subsetsToCover);
        crit = new ArrayList<>(nElements);

        cand = new BitSet(nElements);
        cand.set(0, nElements);

        for (int i = 0; i < nElements; i++) {
            crit.add(new ArrayList<>());
        }
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MMCSNode && ((MMCSNode) obj).elements.equals(elements);
    }

    public BitSet getElements() {
        return (BitSet) elements.clone();
    }

    public BitSet getCand() {
        return (BitSet) cand.clone();
    }

    public IntStream getEleStream() {
        return elements.stream();
    }

    public boolean hasElement(int e) {
        return elements.get(e);
    }

    boolean isCover() {
        return uncov.isEmpty();
    }

    public boolean isGlobalMinimal() {
        return elements.stream().noneMatch(e -> crit.get(e).isEmpty());
    }

    /**
     * find an uncovered subset with the optimal intersection with cand,
     * return its intersection with cand
     */
    public BitSet getAddCandidates() {
        Comparator<Subset> cmp = Comparator.comparing(sb -> {
            BitSet t = ((BitSet) cand.clone());
            t.and(sb.elements);
            return t.cardinality();
        });

        BitSet C = (BitSet) cand.clone();

        /* different strategies: min may be the fastest */
        C.and(Collections.min(uncov, cmp).elements);
        // C.and(Collections.max(uncov, cmp).elements);
        // C.and(uncov.get(0).elements);

        return C;
    }

    public MMCSNode getChildNode(int e, BitSet childCand) {
        MMCSNode childNode = new MMCSNode(nElements);
        childNode.cloneContext(childCand, this);
        childNode.updateContextFromParent(e, this);
        return childNode;
    }

    void cloneContext(BitSet outerCand, MMCSNode originalNode) {
        elements = (BitSet) originalNode.elements.clone();
        cand = (BitSet) outerCand.clone();

        crit = new ArrayList<>(nElements);
        for (int i = 0; i < nElements; i++) {
            crit.add((ArrayList<Subset>) originalNode.crit.get(i).clone());
        }
    }

    void updateContextFromParent(int currE, MMCSNode parentNode) {
        uncov = new ArrayList<>();

        for (Subset sb : parentNode.uncov) {
            if (sb.hasElement(currE)) crit.get(currE).add(sb);
            else uncov.add(sb);
        }

        elements.stream().forEach(u -> {
            crit.get(u).removeIf(F -> F.hasElement(currE));
        });

        elements.set(currE);
    }

}
