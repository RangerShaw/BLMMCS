package algorithm.hittingSet.EMMCS;

import algorithm.hittingSet.Subset;
import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Extendable MMCS algorithm supporting inserting subsets
 */
public class Emmcs{

    /**
     * number of elements or attributes
     */
    private int nElements;

    /**
     * each node represents a minimal cover set
     */
    private List<EmmcsNode> coverNodes = new ArrayList<>();

    /**
     * true iff there's an empty subset to cover (which could never be covered).
     * return no cover set if true but walk down without the empty subset
     */
    private boolean hasEmptySubset = false;

    /**
     * record nodes walked to avoid duplication
     */
    private Set<Integer> walked = new HashSet<>();


    public Emmcs(int nEle) {
        nElements = nEle;
    }

    /**
     * @param bitSetsToCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> bitSetsToCover) {
        hasEmptySubset = bitSetsToCover.stream().anyMatch(BitSet::isEmpty);

        List<Subset> subsets = bitSetsToCover.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        EmmcsNode initNode = new EmmcsNode(nElements, subsets);

        walkDown(initNode, coverNodes);
    }

    /**
     * @param insertedBitSets unique BitSets representing newly inserted Subsets to be covered
     */
    public void insertSubsets(List<BitSet> insertedBitSets) {
        walked.clear();

        hasEmptySubset |= insertedBitSets.stream().anyMatch(BitSet::isEmpty);

        List<Subset> insertedSubsets = insertedBitSets.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        for (EmmcsNode prevNode : coverNodes)
            prevNode.insertSubsets(insertedSubsets);

        List<EmmcsNode> newCoverSets = new ArrayList<>();
        for (EmmcsNode prevNode : coverNodes)
            walkDown(prevNode, newCoverSets);

        coverNodes = newCoverSets;
    }

    /**
     * down from nd on the search tree, find all minimal hitting sets
     */
    void walkDown(EmmcsNode nd, List<EmmcsNode> newNodes) {
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
            EmmcsNode childNode = nd.getChildNode(e, childCand);
            if (childNode.isGlobalMinimal()) {
                walkDown(childNode, newNodes);
                childCand.set(e);
            }
        });
    }


    public List<BitSet> getMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .map(EmmcsNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

}
