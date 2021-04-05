package algorithm.BLMMCS;

import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bidirectional Local MMCS
 */
public class BLMMCS {

    /**
     * number of total elements or attributes
     */
    private int nElements;

    /**
     * cover sets that are minimal on its local branch
     */
    private List<BLMMCSNode> BLMMCSNodes = new ArrayList<>();

    /**
     * true iff there's an empty subset to cover (which could never be covered).
     * return no cover set if true but walk up or down without the empty subset
     */
    private boolean hasEmptySubset = false;

    /**
     * nodes that have been walked down during current iteration, by the hashCode of its elements
     */
    private Set<Integer> walkedDown = new HashSet<>();

    /**
     * nodes that have been walked up during current iteration, by the hashCode of its elements
     */
    private Set<Integer> walkedUp = new HashSet<>();

    /**
     * coverMap[e]: subsets with element e
     */
    List<Set<Subset>> coverMap = new ArrayList<>();


    public BLMMCS(int nEle) {
        nElements = nEle;

        for (int i = 0; i < nElements; i++) {
            coverMap.add(new HashSet<>());
        }
    }

    /**
     * @param toCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> toCover) {
        hasEmptySubset = toCover.stream().anyMatch(BitSet::isEmpty);
        List<Subset> subsets = toCover.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        for (Subset sb : subsets) {
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));
        }

        BLMMCSNode initNode = new BLMMCSNode(nElements, subsets);

        walkDown(initNode, BLMMCSNodes);
    }

    /**
     * down from nd, find all locally minimal cover sets that are minimal on its local branch
     */
    void walkDown(BLMMCSNode nd, List<BLMMCSNode> newNodes) {
        if (walkedDown.contains(nd.hashCode())) return;
        walkedDown.add(nd.hashCode());

        if (nd.isCover()) {
            newNodes.add(nd);
            return;
        }

        nd.getAddCandidates(coverMap).forEach(e -> {
            BLMMCSNode childNode = nd.getChildNode(e);
            walkDown(childNode, newNodes);
        });
    }

    /**
     * @param addedSets unique BitSets representing new Subsets to be covered
     */
    public void processAddedSubsets(List<BitSet> addedSets) {
        walkedDown.clear();

        hasEmptySubset |= addedSets.stream().anyMatch(BitSet::isEmpty);
        List<Subset> addedSubsets = addedSets.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        for (Subset sb : addedSubsets) {
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));
        }

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.addSubsets(addedSubsets);
            walkDown(prevNode, newCoverSets);
        }

        BLMMCSNodes = newCoverSets;
    }

    /**
     * keep node nd if (nd is a cover) and (nd is global minimal or has non-cover parents)
     */
    void walkUp(BLMMCSNode nd, List<BLMMCSNode> newNodes) {
        if (!nd.isCover() || walkedUp.contains(nd.hashCode())) return;

        walkedUp.add(nd.hashCode());

        if (nd.isGlobalMinimal()) {
            newNodes.add(nd);
            return;
        }

        boolean hasNonCoverParent = false;

        for (int e : nd.getRemoveCandidates().toArray()) {
            if (nd.parentIsCover(e)) walkUp(nd.getParentNode(e, coverMap), newNodes);
            else hasNonCoverParent = true;
        }

        if (hasNonCoverParent) newNodes.add(nd);
    }

    /**
     * @param removedSets unique BitSets representing existing Subsets to be removed
     */
    public void processRemovedSubsets(List<BitSet> removedSets) {
        walkedUp.clear();

        hasEmptySubset &= removedSets.stream().noneMatch(BitSet::isEmpty);

        Set<Subset> removed = removedSets.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toSet());
        for (Subset sb : removed) {
            sb.getEleStream().forEach(e -> coverMap.get(e).remove(sb));
        }

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.removeSubsets(removed);
            walkUp(prevNode, newCoverSets);
        }

        BLMMCSNodes = newCoverSets;
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : BLMMCSNodes.stream()
                .filter(BLMMCSNode::isGlobalMinimal)
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

    public List<BitSet> getAllCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : BLMMCSNodes.stream()
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

}
