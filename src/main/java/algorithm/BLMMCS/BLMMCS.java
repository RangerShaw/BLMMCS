package algorithm.BLMMCS;

import util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bidirectional Local MMCS
 */
public class BLMMCS {

    /**
     * number of total elements or size of coverSet or size of subSet
     */
    private int nElements;

    /**
     * cover sets that are minimal on its local branch
     */
    private List<BLMMCSNode> BLMMCSNodes = new ArrayList<>();

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

    public void initiate(List<BitSet> toCover) {
        List<Subset> subsetsToCover = toCover.stream().map(Subset::new).collect(Collectors.toList());

        for (Subset sb : subsetsToCover) {
            sb.getEleStream().forEach(e -> coverMap.get(e).add(sb));
        }

        BLMMCSNode initNode = new BLMMCSNode(nElements, subsetsToCover);

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

        // TODO: prune: remove an ele from nd and check whether it's been walked?
        nd.getAddCandidates().forEach(e -> {
            BLMMCSNode childNd = nd.getChildNode(e);
            walkDown(childNd, newNodes);
        });
    }


    public void processAddedSubsets(List<BitSet> addedSets) {
        walkedDown.clear();

        List<Subset> addedSubsets = addedSets.stream().map(Subset::new).collect(Collectors.toList());
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
     * keep node S if (S is a cover) and (S is global minimal or has non-cover parents)
     */
    public void walkUp(BLMMCSNode S, List<BLMMCSNode> newNodes) {
        if (!S.isCover() || walkedUp.contains(S.hashCode())) return;

        walkedUp.add(S.hashCode());

        if (S.isGlobalMinimal()) {
            newNodes.add(S);
            return;
        }

        boolean hasNonCoverParent = false;

        PrimitiveIterator.OfInt it = S.getRemoveCandidates().iterator();
        while (it.hasNext()) {
            int e = it.nextInt();
            if (S.parentIsCover(e)) walkUp(S.getParentNode(e, coverMap), newNodes);
            else hasNonCoverParent = true;
        }

        if (hasNonCoverParent) newNodes.add(S);
    }

    public void processRemovedSubsets(List<BitSet> removedSets) {
        walkedUp.clear();

        Set<Subset> removedSubsets = removedSets.stream().map(Subset::new).collect(Collectors.toSet());
        for (Subset removedSb : removedSubsets) {
            removedSb.getEleStream().forEach(e -> coverMap.get(e).remove(removedSb));
        }

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.removeSubsets(removedSubsets);
            walkUp(prevNode, newCoverSets);
        }

        BLMMCSNodes = newCoverSets;
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return BLMMCSNodes.stream()
                .filter(BLMMCSNode::isGlobalMinimal)
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

    public List<BitSet> getAllCoverSets() {
        return BLMMCSNodes.stream()
                .map(BLMMCSNode::getElements)
                .sorted(Utils.BitsetComparator())
                .collect(Collectors.toList());
    }

}
