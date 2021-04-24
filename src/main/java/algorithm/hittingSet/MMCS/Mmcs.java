package algorithm.hittingSet.MMCS;

import algorithm.hittingSet.Subset;
import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * original MMCS algorithm for discovering minimal hitting sets
 */
public class Mmcs {

    /**
     * number of elements or attributes
     */
    private int nElements;

    /**
     * each node represents a minimal cover set
     */
    private List<MmcsNode> coverNodes = new ArrayList<>();

    /**
     * true iff there's an empty subset to cover (which could never be covered).
     * return no cover set if true but walk down without the empty subset
     */
    private boolean hasEmptySubset = false;

    /**
     * record nodes walked to avoid duplication
     */
    private Set<Integer> walked = new HashSet<>();


    public Mmcs(int nEle) {
        nElements = nEle;
    }

    /**
     * @param bitSetsToCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> bitSetsToCover) {
        hasEmptySubset = bitSetsToCover.stream().anyMatch(BitSet::isEmpty);

        List<Subset> subsets = bitSetsToCover.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        MmcsNode initNode = new MmcsNode(nElements, subsets);

        walkDown(initNode, coverNodes);
    }

    /**
     * down from nd on the search tree, find all minimal hitting sets
     */
    void walkDown(MmcsNode nd, List<MmcsNode> newNodes) {
        if (walked.contains(nd.hashCode())) return;
        walked.add(nd.hashCode());

        if (nd.isCover()) {
            newNodes.add(nd);
            return;
        }

        // configure cand for child nodes
        BitSet childCand = nd.getCand();
        BitSet addCandidates = nd.getAddCandidates();
        childCand.andNot(addCandidates);

        addCandidates.stream().forEach(e -> {
            MmcsNode childNode = nd.getChildNode(e, childCand);
            if (childNode.isGlobalMinimal()) {
                walkDown(childNode, newNodes);
                childCand.set(e);
            }
        });
    }


    public List<BitSet> getMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .map(MmcsNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }


}
