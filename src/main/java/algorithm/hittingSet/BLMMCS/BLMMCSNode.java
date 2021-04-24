package algorithm.hittingSet.BLMMCS;

import algorithm.hittingSet.Subset;

import java.util.*;
import java.util.stream.IntStream;

/**
 * each BLMMCSNode corresponds uniquely with a set of elements
 * which is an intermediate (potentially) cover set
 */
public class BLMMCSNode {

    private int nElements;

    private BitSet elements;

    /**
     * uncovered subsets
     */
    private List<Subset> uncov;

    /**
     * crit[i]: subsets for which element i is crucial
     */
    private ArrayList<ArrayList<Subset>> crit;

    private BLMMCSNode(int nEle) {
        nElements = nEle;
    }

    /**
     * for initiation only
     */
    public BLMMCSNode(int nEle, List<Subset> subsetsToCover) {
        nElements = nEle;
        elements = new BitSet(nElements);
        uncov = new ArrayList<>(subsetsToCover);
        crit = new ArrayList<>(nElements);

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
        return obj instanceof BLMMCSNode && ((BLMMCSNode) obj).elements.equals(elements);
    }

    public BitSet getElements() {
        return (BitSet) elements.clone();
    }

    public int getElementsCount() {
        return elements.cardinality();
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

    public IntStream getAddCandidates(List<List<Subset>> coverMap) {
        BitSet cand = ((BitSet) elements.clone());
        cand.flip(0, nElements);
        return cand.stream().filter(e -> !coverMap.get(e).isEmpty());
    }

    public IntStream getRemoveCandidates() {
        return elements.stream().filter(e -> crit.get(e).isEmpty());
    }

    public BLMMCSNode getChildNode(int e) {
        BLMMCSNode childNode = new BLMMCSNode(nElements);
        childNode.cloneContext(this);
        childNode.updateContextFromParent(e, this);
        return childNode;
    }

    public boolean hasNoCrit() {
        return elements.stream().allMatch(e -> crit.get(e).isEmpty());
    }

    /**
     * true iff the parent without e is still a cover
     */
    public boolean hasNoCritOn(int e) {
        return crit.get(e).isEmpty();
    }

    public boolean hasNonCoverParent() {
        return elements.stream().anyMatch(e -> !crit.get(e).isEmpty());
    }

    void cloneContext(BLMMCSNode originalNode) {
        elements = (BitSet) originalNode.elements.clone();

        crit = new ArrayList<>(nElements);
        for (int i = 0; i < nElements; i++) {
            crit.add(new ArrayList<>(originalNode.crit.get(i)));
        }
    }

    /**
     * general version of BLMMCS for simply discovering cover sets
     */
    void updateContextFromChild(int e, List<List<Subset>> coverMap, BLMMCSNode childNode) {
        elements.clear(e);

        uncov = new ArrayList<>();      // always empty

        for (Subset sb : coverMap.get(e)) {
            int critCover = sb.getCritCover(elements);
            if (critCover >= 0) crit.get(critCover).add(sb);
        }

        crit.get(e).clear();
    }

    public BLMMCSNode getParentNode(int e, List<List<Subset>> coverMap) {
        BLMMCSNode parentNode = new BLMMCSNode(nElements);
        parentNode.cloneContext(this);
        parentNode.updateContextFromChild(e, coverMap, this);
        return parentNode;
    }


    void updateContextFromParent(int e, BLMMCSNode parentNode) {
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

    public void addSubsets(List<Subset> newSubsets) {
        for (Subset newSb : newSubsets) {
            BitSet intersec = (BitSet) elements.clone();
            intersec.and(newSb.elements);

            if (intersec.isEmpty()) uncov.add(newSb);
            if (intersec.cardinality() == 1) crit.get(intersec.nextSetBit(0)).add(newSb);
        }
    }

    public void removeSubsets(Set<Subset> removedBitSets, List<List<Subset>> coverMap) {
        for (ArrayList<Subset> critSubsets : crit) {
            critSubsets.removeIf(removedBitSets::contains);
        }
    }

}
