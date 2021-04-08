package algorithm.MMCS;

import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class MMCS {

    /**
     * number of elements or attributes
     */
    int nElements;

    /**
     * each node represents a minimal cover set
     */
    List<MMCSNode> coverNodes = new ArrayList<>();

    /**
     * true iff there's an empty subset to cover (which could never be covered).
     * return no cover set if true but walk down without the empty subset
     */
    boolean hasEmptySubset = false;


    public MMCS(int nEle) {
        nElements = nEle;
    }

    /**
     * @param toCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> toCover) {
        hasEmptySubset = toCover.stream().anyMatch(BitSet::isEmpty);

        List<Subset> subsets = toCover.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        MMCSNode initNode = new MMCSNode(nElements, subsets);

        walkDown(initNode, coverNodes);
    }

    /**
     * down from nd, find all minimal cover sets
     */
    void walkDown(MMCSNode nd, List<MMCSNode> newNodes) {
        if (nd.isCover()) {
            newNodes.add(nd);
            return;
        }

        BitSet childCand = nd.getCand();
        BitSet C = nd.getAddCandidates();
        childCand.andNot(C);

        for (int e : C.stream().toArray()) {
            MMCSNode childNode = nd.getChildNode(e, childCand);
            if (childNode.isGlobalMinimal()) {
                walkDown(childNode, newNodes);
            }
            childCand.set(e);
        }
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .map(MMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

}


