package algorithm.MMCS;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class HittingSetAlgoNode {
    int nElements;

    BitSet elements;

    BitSet cand;

    /**
     * uncovered subsets
     */
    List<Subset> uncov;

    /**
     * crit[i]: subsets for which element i is crucial
     */
    ArrayList<ArrayList<Subset>> crit;

    HittingSetAlgoNode(int nEle) {
        nElements = nEle;
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HittingSetAlgoNode && ((HittingSetAlgoNode) obj).elements.equals(elements);
    }

    public BitSet getElements() {
        return (BitSet) elements.clone();
    }

    public boolean isCover() {
        return uncov.isEmpty();
    }

    public boolean isGlobalMinimal() {
        return elements.stream().noneMatch(e -> crit.get(e).isEmpty());
    }

    abstract public BitSet getAddCandidates();

}
