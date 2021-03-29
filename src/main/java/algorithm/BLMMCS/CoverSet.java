package algorithm.BLMMCS;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CoverSet {

    private int nElements;

    public BitSet getElements() {
        return elements;
    }

    public BitSet elements;

    private LinkedList<Subset> uncov;

    /**
     * crit[i]: subsets for which element i is crucial
     */
    private ArrayList<ArrayList<Subset>> crit;

    /**
     * coverMap[i]: subsets covered by element i
     */
    private ArrayList<ArrayList<Subset>> coverMap;

    public CoverSet(int nEle) {
        nElements = nEle;
    }

    public CoverSet(int nEle, BitSet elements, LinkedList<Subset> uncov, ArrayList<ArrayList<Subset>> crit, ArrayList<ArrayList<Subset>> coverMap) {
        nElements = nEle;
        this.elements = elements;
        this.uncov = uncov;
        this.crit = crit;
        this.coverMap = coverMap;
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
    public IntStream getCandidates() {
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

    public CoverSet getChildS(int e) {
        CoverSet childS = new CoverSet(nElements);
        childS.cloneContext(e, this);
        childS.updateContext(e, this);
        return childS;
    }

    void cloneContext(int e, CoverSet parentS) {
        elements = (BitSet) parentS.elements.clone();
        uncov = new LinkedList<>();     // no need to clone here
        crit = new ArrayList<>(parentS.crit.size());
        coverMap = new ArrayList<>(parentS.coverMap.size());

        for (int i = 0; i < nElements; i++) {
            crit.add((ArrayList<Subset>) parentS.crit.get(i).clone());
            coverMap.add((ArrayList<Subset>) parentS.coverMap.get(i).clone());
        }
    }

    void updateContext(int e, CoverSet parentCoverSet) {
        uncov = parentCoverSet.uncov.stream()
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
            else intersec.stream().forEach(i -> coverMap.get(i).add(newSubset));
            if (intersec.cardinality() == 1) crit.get(intersec.nextSetBit(0)).add(newSubset);
        }
    }

    public void removeSubSets(List<Subset> removedSubsets) {
        // TODO: run some tests in advance
        for (Subset newSubset : removedSubsets) {
            BitSet t = (BitSet) elements.clone();
            t.or(newSubset.elements);
            if (!t.equals(elements)) uncov.add(newSubset);
        }
    }

    // TODO: rewrite hashCode and equals
}
