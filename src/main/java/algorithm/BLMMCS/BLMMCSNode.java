package algorithm.BLMMCS;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BLMMCSNode {

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

    /**
     * coverMap[i]: subsets covered by element i
     */
    private ArrayList<ArrayList<Subset>> coverMap;

    public BLMMCSNode(int nEle) {
        nElements = nEle;
    }

    public BLMMCSNode(int nEle, BitSet elements, LinkedList<Subset> uncov, ArrayList<ArrayList<Subset>> crit, ArrayList<ArrayList<Subset>> coverMap) {
        nElements = nEle;
        this.elements = elements;
        this.uncov = uncov;
        this.crit = crit;
        this.coverMap = coverMap;
    }

    public BitSet getElements() {
        return elements;
    }

    boolean isCover() {
        return uncov.isEmpty();
    }

    boolean isGlobalMinimal() {
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

    public BLMMCSNode getParentNode(int e) {
        BLMMCSNode parentNode = new BLMMCSNode(nElements);
        parentNode.cloneContext(this);
        parentNode.updateContextFromChild(e, this);
        return parentNode;
    }

    void cloneContext(BLMMCSNode parentNode) {
        elements = (BitSet) parentNode.elements.clone();
        crit = new ArrayList<>(parentNode.crit.size());
        coverMap = new ArrayList<>(parentNode.coverMap.size());
        //uncov = new LinkedList<>();     // different for child and parent

        for (int i = 0; i < nElements; i++) {
            crit.add((ArrayList<Subset>) parentNode.crit.get(i).clone());
            coverMap.add((ArrayList<Subset>) parentNode.coverMap.get(i).clone());
        }
    }

    void updateContextFromChild(int e, BLMMCSNode childNode) {
        elements.clear(e);

        uncov = new LinkedList<>(childNode.uncov);
        uncov.addAll(crit.get(e));

        for (Subset sb : coverMap.get(e)) {
            int critCover = sb.getCritCover(elements);
            if (critCover >= 0) crit.get(critCover).add(sb);
        }

        crit.get(e).clear();

        coverMap.get(e).clear();
    }

    void updateContextFromParent(int e, BLMMCSNode parentNode) {
        uncov = parentNode.uncov.stream()
                .filter(F -> {
                    if (!F.hasElement(e)) return true;
                    crit.get(e).add(F);
                    coverMap.get(e).add(F);
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
            else intersec.stream().forEach(e -> coverMap.get(e).add(newSubset));
            if (intersec.cardinality() == 1) crit.get(intersec.nextSetBit(0)).add(newSubset);
        }
    }

    public void removeSubsets(List<Subset> removedSubsets) {
        // TODO: run some tests in advance
        for (Subset removedSubset : removedSubsets) {
            BitSet t = (BitSet) elements.clone();
            t.and(removedSubset.elements);
            t.stream().forEach(e -> {
                crit.get(e).remove(removedSubset);
                coverMap.get(e).remove(removedSubset);
            });
        }
    }

    // TODO: rewrite hashCode and equals
}
