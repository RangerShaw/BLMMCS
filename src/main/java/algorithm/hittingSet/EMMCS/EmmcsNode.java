package algorithm.hittingSet.EMMCS;

import algorithm.hittingSet.Subset;

import java.util.*;

public class EmmcsNode {

    private int nElements;

    /**
     * elements of current node
     */
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


    private EmmcsNode(int nEle) {
        nElements = nEle;
    }

    /**
     * for initiation only
     */
    EmmcsNode(int nEle, List<Subset> subsetsToCover) {
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
        return obj instanceof EmmcsNode && ((EmmcsNode) obj).elements.equals(elements);
    }

    BitSet getElements() {
        return (BitSet) elements.clone();
    }

    BitSet getCand() {
        return (BitSet) cand.clone();
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
    BitSet getAddCandidates() {
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

    EmmcsNode getChildNode(int e, BitSet childCand) {
        EmmcsNode childNode = new EmmcsNode(nElements);
        childNode.cloneContext(childCand, this);
        childNode.updateContextFromParent(e, this);
        return childNode;
    }

    void cloneContext(BitSet outerCand, EmmcsNode originalNode) {
        elements = (BitSet) originalNode.elements.clone();
        cand = (BitSet) outerCand.clone();

        crit = new ArrayList<>(nElements);
        for (int i = 0; i < nElements; i++) {
            crit.add(new ArrayList<>(originalNode.crit.get(i)));
        }
    }

    void updateContextFromParent(int e, EmmcsNode parentNode) {
        uncov = new ArrayList<>();

        for (Subset sb : parentNode.uncov) {
            if (sb.hasElement(e)) crit.get(e).add(sb);
            else uncov.add(sb);
        }

        elements.stream().forEach(u -> {
            crit.get(u).removeIf(F -> F.hasElement(e));
        });

        elements.set(e);
    }

    void insertSubsets(List<Subset> newSubsets) {
        cand = (BitSet) elements.clone();
        cand.flip(0, nElements);

        for (Subset newSb : newSubsets) {
            BitSet intersec = (BitSet) elements.clone();
            intersec.and(newSb.elements);

            if (intersec.isEmpty()) uncov.add(newSb);
            if (intersec.cardinality() == 1) crit.get(intersec.nextSetBit(0)).add(newSb);
        }
    }

}
