package algorithm.MMCS.BLMMCS;

import algorithm.MMCS.Subset;
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
    private List<BLMMCSNode> coverNodes = new ArrayList<>();

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

    private boolean walkedUpLastTime = true;

    /**
     * coverMap[e]: subsets with element e
     */
    List<List<Subset>> coverMap = new ArrayList<>();


    public BLMMCS(int nEle) {
        nElements = nEle;
    }

    /**
     * @param toCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> toCover) {
        for (int i = 0; i < nElements; i++) {
            coverMap.add(new ArrayList<>());
        }

        List<Subset> subsets = new ArrayList<>(toCover.size());
        for (BitSet bs : toCover) {
            if (bs.isEmpty()) hasEmptySubset = true;
            else subsets.add(new Subset(bs));
        }

        for (Subset sb : subsets) {
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));
        }

        BLMMCSNode initNode = new BLMMCSNode(nElements, subsets);

        walkDown(initNode, coverNodes);

        System.out.println("# of Nodes walked: " + walkedDown.size());
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
        for (BLMMCSNode prevNode : coverNodes) {
            prevNode.addSubsets(addedSubsets);
            walkDown(prevNode, newCoverSets);
        }

        System.out.println(" [BLMMCS] # of Nodes walked down: " + walkedDown.size());

        coverNodes = newCoverSets;
    }

    /**
     * keep node nd if (nd is a cover) and (nd is global minimal or has non-cover parents)
     */
    void walkUp(BLMMCSNode nd, List<BLMMCSNode> newNodes) {
        if (walkedUp.contains(nd.hashCode())) return;
        walkedUp.add(nd.hashCode());

        if (nd.isGlobalMinimal()) {
            newNodes.add(nd);
            return;
        }

        if (nd.hasNonCoverParent()) newNodes.add(nd);

        nd.getRemoveCandidates().forEach(e -> {
            BLMMCSNode parentNode = nd.getParentNode(e, coverMap);
            walkUp(parentNode, newNodes);
        });
    }

    /**
     * @param removedSets unique BitSets representing existing Subsets to be removed
     */
    public void processRemovedSubsets(List<BitSet> removedSets) {
        long startTime1 = System.nanoTime();
        walkedUp.clear();

        hasEmptySubset &= removedSets.stream().noneMatch(BitSet::isEmpty);

        Set<Subset> removed = removedSets.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toSet());

        coverMap.forEach(subsets -> subsets.removeIf(removed::contains));

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : coverNodes) {
            prevNode.removeSubsets(removed, coverMap);
        }

        long endTime1 = System.nanoTime();
        System.out.println(" [BLMMCS] REMOVE runtime 1: " + (endTime1 - startTime1) / 1000000 + "ms");

        for (BLMMCSNode prevNode : coverNodes) {
            walkUp(prevNode, newCoverSets);
        }

        long endTime2 = System.nanoTime();
        System.out.println(" [BLMMCS] REMOVE runtime 2: " + (endTime2 - endTime1) / 1000000 + "ms");
        System.out.println(" [BLMMCS] # of Nodes walked up: " + walkedUp.size());
        System.out.println(" [BLMMCS] # of Nodes retained: " + coverNodes.size());

        coverNodes = newCoverSets;
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .filter(BLMMCSNode::isGlobalMinimal)
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

    public List<BitSet> getAllCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

}
