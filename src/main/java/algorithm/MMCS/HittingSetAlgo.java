package algorithm.MMCS;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class HittingSetAlgo {
    public int nElement;

    public List<HittingSetAlgoNode> coverNodes = new ArrayList<>();

    boolean hasEmptySubset = false;

    abstract public void initiate(List<BitSet> toCover);

    abstract void walkDown(HittingSetAlgoNode nd, List<HittingSetAlgoNode> newNodes);

    abstract public List<BitSet> getGlobalMinCoverSets();
}
