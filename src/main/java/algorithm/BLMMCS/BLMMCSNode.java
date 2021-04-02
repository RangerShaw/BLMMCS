package algorithm.BLMMCS;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BLMMCSNode {

    private static BLMMCSNode nonCoverNode;

    private int nElements;

    private BitSet elements;

    /**
     * lots of remove operation
     */
    private LinkedList<Subset> uncov;

    /**
     * crit[i]: subsets for which element i is crucial
     */
    private ArrayList<ArrayList<Subset>> crit;

    private BLMMCSNode(int nEle) {
        nElements = nEle;
    }

    public BLMMCSNode(int nEle, BitSet elements, LinkedList<Subset> uncov, ArrayList<ArrayList<Subset>> crit) {
        nElements = nEle;
        this.elements = elements;
        this.uncov = uncov;
        this.crit = crit;

        nonCoverNode = new BLMMCSNode(nElements);
        nonCoverNode.elements = new BitSet();
        nonCoverNode.uncov = new LinkedList<>();
        nonCoverNode.uncov.add(new Subset(new BitSet()));
    }

    public BitSet getElements() {
        return elements;
    }

    boolean isCover() {
        return uncov.isEmpty();
    }

    public boolean isGlobalMinimal() {
        return elements.stream().noneMatch(e -> crit.get(e).isEmpty());
    }

    public int hashCode() {
        return elements.hashCode();
    }

    /**
     * find an uncovered subset with the largest intersection with cand,
     * return its intersection with cand
     */
    public IntStream getAddCandidates() {
        BitSet cand = ((BitSet) elements.clone());
        cand.flip(0, cand.size());

        Comparator<Subset> cmp = Comparator.comparing(sb -> {
            BitSet t = ((BitSet) cand.clone());
            t.and(sb.elements);
            return t.cardinality();
        });

        cand.and(Collections.max(uncov, cmp).elements);
        return cand.stream();
    }

    public IntStream getRemoveCandidates() {
        return elements.stream();
    }

    public BLMMCSNode getChildNode(int e) {
        BLMMCSNode childNode = new BLMMCSNode(nElements);
        childNode.cloneContext(this);
        childNode.updateContextFromParent(e, this);
        return childNode;
    }

    public boolean parentIsCover(int e) {
        return crit.get(e).isEmpty();
    }

    public BLMMCSNode getParentNode(int e, List<Set<Subset>> coverMap) {
        // return nonCoverNode immediately if the parent node is NOT a cover
        if (!crit.get(e).isEmpty()) {
            nonCoverNode.elements = (BitSet) elements.clone();
            nonCoverNode.elements.clear(e);
            return nonCoverNode;
        }

        BLMMCSNode parentNode = new BLMMCSNode(nElements);
        parentNode.cloneContext(this);
        parentNode.updateContextFromChild(e, coverMap);
        return parentNode;
    }

    void cloneContext(BLMMCSNode parentNode) {
        elements = (BitSet) parentNode.elements.clone();
        crit = new ArrayList<>(parentNode.crit.size());
        //uncov = new LinkedList<>();     // different for child and parent

        for (int i = 0; i < nElements; i++) {
            crit.add((ArrayList<Subset>) parentNode.crit.get(i).clone());
        }
    }

    void updateContextFromChild(int e, List<Set<Subset>> coverMap) {
        elements.clear(e);

        uncov = new LinkedList<>();     // uncov is always empty

        for (Subset sb : coverMap.get(e)) {
            int critCover = sb.getCritCover(elements);
            if (critCover >= 0) crit.get(critCover).add(sb);
        }

        crit.get(e).clear();
    }

    void updateContextFromParent(int e, BLMMCSNode parentNode) {
        uncov = parentNode.uncov.stream()
                .filter(F -> {
                    if (!F.hasElement(e)) return true;
                    crit.get(e).add(F);
                    return false;
                })
                .collect(Collectors.toCollection(LinkedList::new));

        elements.stream().forEach(u -> {
            crit.get(u).removeIf(F -> F.hasElement(e));
        });

        elements.set(e);
    }

    public void addSubsets(List<Subset> addedSubsets) {
        // TODO: run some tests in advance
        for (Subset newSubset : addedSubsets) {
            BitSet intersec = (BitSet) elements.clone();
            intersec.and(newSubset.elements);

            if (intersec.isEmpty()) uncov.add(newSubset);
            if (intersec.cardinality() == 1) crit.get(intersec.nextSetBit(0)).add(newSubset);
        }
    }

    public void removeSubsets(List<Subset> removedSubsets) {
        // TODO: run some tests in advance
        Set<Integer> removedBitsets = removedSubsets.stream().map(Subset::hashCode).collect(Collectors.toSet());
        for (Subset removedSubset : removedSubsets) {
            BitSet t = (BitSet) elements.clone();
            t.and(removedSubset.elements);
            t.stream().forEach(e -> {
                crit.get(e).removeIf(sb -> removedBitsets.contains(sb.hashCode()));
            });
        }
    }

}
