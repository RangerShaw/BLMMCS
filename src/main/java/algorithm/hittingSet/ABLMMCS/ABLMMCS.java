package algorithm.hittingSet.ABLMMCS;

import algorithm.hittingSet.Subset;
import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class ABLMMCS {

    /**
     * number of total elements or attributes
     */
    private int nElements;

    /**
     * maximal allowed error rate
     */
    double threshold;

    /**
     * number of subsets to cover, excluding the empty subset
     */
    int nSubsets;

    /**
     * cover sets that are minimal on its local branch
     */
    private List<ABLMMCSNode> coverNodes = new ArrayList<>();

    /**
     * true iff there's an empty subset to cover (which could never be covered).
     * return no cover set if true but walk up or down without the empty subset
     */
    private boolean hasEmptySubset = false;

    /**
     * nodes that have been walked down during current iteration, by the hashCode of its elements
     */
    private Map<Integer, Boolean> walkedDown = new HashMap<>();

    /**
     * nodes that have been walked up during current iteration, by the hashCode of its elements
     */
    private Set<Integer> walkedUp = new HashSet<>();

    private boolean walkedUpLastTime = true;

    /**
     * coverMap[e]: subsets with element e
     */
    List<List<Subset>> coverMap = new ArrayList<>();


    public ABLMMCS(int nEle, double thre) {
        nElements = nEle;
        threshold = thre;
    }

    /**
     * @param toCover unique BitSets representing Subsets to be covered
     */
    public void initiate(List<BitSet> toCover) {
        hasEmptySubset = toCover.stream().anyMatch(BitSet::isEmpty);
        List<Subset> subsets = toCover.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());
        nSubsets = subsets.size();

        for (int i = 0; i < nElements; i++)
            coverMap.add(new ArrayList<>());
        for (Subset sb : subsets)
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));

        ABLMMCSNode initNode = new ABLMMCSNode(nElements, subsets);
        // TODO: what if initNode is an approx cover
        walkedDown.put(initNode.hashCode(), false);
        walkDown(initNode, coverNodes);

        System.out.println("# of Nodes walked: " + walkedDown.size());
    }

    /**
     * down from nd, find all locally minimal cover sets that are minimal on its local branch
     */
    boolean walkDown(ABLMMCSNode nd, List<ABLMMCSNode> newApproxCovers) {
        if (walkedDown.containsKey(nd.hashCode())) return walkedDown.get(nd.hashCode());

        boolean isApproxCover = nd.isApproxCover(threshold, nSubsets);
        walkedDown.put(nd.hashCode(), isApproxCover);

        if (isApproxCover) {
            newApproxCovers.add(nd);
            return isApproxCover;
        }

        nd.getAddCandidates(coverMap).forEach(e -> {
            ABLMMCSNode childNode = nd.getChildNode(e);
            walkDown(childNode, newApproxCovers);
        });
        return isApproxCover;
    }

    /**
     * @param addedSets unique BitSets representing new Subsets to be covered
     */
    public void processAddedSubsets(List<BitSet> addedSets) {
        walkedDown.clear();

        hasEmptySubset |= addedSets.stream().anyMatch(BitSet::isEmpty);
        List<Subset> addedSubsets = addedSets.stream().filter(bs -> !bs.isEmpty()).map(Subset::new).collect(Collectors.toList());

        for (Subset sb : addedSubsets)
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));

        List<ABLMMCSNode> newCoverSets = new ArrayList<>();
        for (ABLMMCSNode prevNode : coverNodes) {
            prevNode.addSubsets(addedSubsets);
            walkDown(prevNode, newCoverSets);
        }

        coverNodes = newCoverSets;

        System.out.println(" [ABLMMCS] # of Nodes walked down: " + walkedDown.size());
    }

    /**
     * keep node nd if (nd is a cover) and (nd is global minimal or has non-cover parents)
     */
    void walkUp(ABLMMCSNode nd, List<ABLMMCSNode> newNodes) {
        if (walkedUp.contains(nd.hashCode())) return;
        walkedUp.add(nd.hashCode());

        if (nd.isGlobalMinimal()) {
            newNodes.add(nd);
            return;
        }

        if (nd.hasNonCoverParent()) newNodes.add(nd);

        nd.getRemoveCandidates().forEach(e -> {
            ABLMMCSNode parentNode = nd.getParentNode(e, coverMap);
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

        List<ABLMMCSNode> newCoverSets = new ArrayList<>();
        for (ABLMMCSNode prevNode : coverNodes) {
            prevNode.removeSubsets(removed, coverMap);
        }

        long endTime1 = System.nanoTime();
        System.out.println(" [ABLMMCS] REMOVE runtime 1: " + (endTime1 - startTime1) / 1000000 + "ms");

        for (ABLMMCSNode prevNode : coverNodes) {
            walkUp(prevNode, newCoverSets);
        }

        long endTime2 = System.nanoTime();
        System.out.println(" [ABLMMCS] REMOVE runtime 2: " + (endTime2 - endTime1) / 1000000 + "ms");
        System.out.println(" [ABLMMCS] # of Nodes walked up: " + walkedUp.size());
        System.out.println(" [ABLMMCS] # of Nodes retained: " + coverNodes.size());

        coverNodes = newCoverSets;
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .filter(ABLMMCSNode::isGlobalMinimal)
                .map(ABLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

    public List<BitSet> getAllCoverSets() {
        return hasEmptySubset ? new ArrayList<>() : coverNodes.stream()
                .map(ABLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }
}
