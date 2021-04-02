package algorithm.BLMMCS;

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
     * subsets to be covered
     */
    // TODO: remove this
    private List<Subset> subsetsToCover;

    /**
     * hitting sets that are minimal on its local branch
     */
    private List<BLMMCSNode> BLMMCSNodes;

    /**
     * record nodes that have been walked down in a batch, by the hashCode of its elements
     */
    private Set<Integer> walkedDown;

    /**
     * record nodes that have been walked up in a batch, by the hashCode of its elements
     */
    private Set<Integer> walkedUp;

    /**
     * coverMap[i]: subsets covered by element i
     */
    List<Set<Subset>> coverMap;

    public BLMMCS(int nEle, List<BitSet> toCover) {
        nElements = nEle;
        subsetsToCover = toCover.stream().map(Subset::new).collect(Collectors.toCollection(ArrayList::new));
        BLMMCSNodes = new ArrayList<>();
        walkedDown = new HashSet<>();
        walkedUp = new HashSet<>();
        coverMap = new ArrayList<>();

        for (int i = 0; i < nElements; i++) {
            coverMap.add(new HashSet<>());
        }
        for (Subset sb : subsetsToCover) {
            sb.getElements().forEach(e -> coverMap.get(e).add(sb));
        }
    }

    public void initiate() {
        BitSet S = new BitSet(nElements);
        LinkedList<Subset> uncov = new LinkedList<>(subsetsToCover);
        ArrayList<ArrayList<Subset>> crit = new ArrayList<>(nElements);

        for (int i = 0; i < nElements; i++) {
            crit.add(new ArrayList<>());
        }

        BLMMCSNode emptyCover = new BLMMCSNode(nElements, S, uncov, crit);
        walkDown(emptyCover, BLMMCSNodes);

    }

    /**
     * down from S, find all locally minimal cover sets that are minimal on its local branch
     */
    public void walkDown(BLMMCSNode S, List<BLMMCSNode> newNodes) {
        if (walkedDown.contains(S.hashCode())) return;
        walkedDown.add(S.hashCode());

        if (S.isCover()) {
            newNodes.add(S);
            return;
        }

        // TODO: prune: remove an ele from S and check whether it's been walked?
        S.getAddCandidates().forEach(e -> {
            BLMMCSNode childS = S.getChildNode(e);
            walkDown(childS, newNodes);
        });
    }


    public void processAddedSubsets(List<BitSet> addedSets) {
        walkedDown.clear();
        List<Subset> addedSubsets = addedSets.stream().map(Subset::new).collect(Collectors.toList());
        subsetsToCover.addAll(addedSubsets);

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.addSubsets(addedSubsets);
            walkDown(prevNode, newCoverSets);
        }

        BLMMCSNodes = newCoverSets;
    }

    /**
     * keep Node S if S
     * 1. is a cover, and
     * 2. is global minimal or has non-cover parents
     */
    // TODO: will walkUp prune some branches?
    public void walkUp(BLMMCSNode S, List<BLMMCSNode> newNodes) {
        if (!S.isCover() || walkedUp.contains(S.hashCode())) return;

        walkedUp.add(S.hashCode());

        if (S.isGlobalMinimal()) {
            newNodes.add(S);
            return;
        }

        // walk up and check whether all parents are covers
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
        long startTime1 = System.nanoTime();
        walkedUp.clear();
        List<Subset> removedSubsets = removedSets.stream().map(Subset::new).collect(Collectors.toList());

        for (Subset removedSb : removedSubsets) {
            removedSb.getElements().forEach(e -> coverMap.get(e).remove(removedSb));
        }

        List<BLMMCSNode> currNodes = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.removeSubsets(removedSubsets);
        }
        long endTime1 = System.nanoTime();
        System.out.println("runtime 1 for letter_remove : " + (endTime1 - startTime1) / 1000000 + "ms");

        long startTime2 = System.nanoTime();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            walkUp(prevNode, currNodes);
        }
        long endTime2 = System.nanoTime();
        System.out.println("runtime 2 for letter_remove : " + (endTime2 - startTime2) / 1000000 + "ms");

        BLMMCSNodes = currNodes;
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return BLMMCSNodes.stream()
                .filter(BLMMCSNode::isGlobalMinimal)
                .map(BLMMCSNode::getElements)
                .collect(Collectors.toList());
    }

    public List<BitSet> getAllCoverSets() {
        return BLMMCSNodes.stream()
                .map(BLMMCSNode::getElements)
                .collect(Collectors.toList());
    }




/*
    public static void main(String[] args) {
        List<Subset> subsets = new ArrayList<>();
        Subset ABC = new Subset(BitSet.valueOf(new long[]{0b000111}));
        Subset ABD = new Subset(BitSet.valueOf(new long[]{0b001011}));
        subsets.add(ABC);
        subsets.add(ABD);

        BLMMCS blmmcs = new BLMMCS(6, subsets);
        blmmcs.initiate();
        blmmcs.getGlobalMinHitSets().stream().map(s->s.elements).forEach(System.out::println);
        System.out.println();

        List<Subset> newSubsets = new ArrayList<>();
        Subset F = new Subset(BitSet.valueOf(new long[]{0b100000}));
        newSubsets.add(F);
        blmmcs.processAddedSubsets(newSubsets);
        blmmcs.getGlobalMinHitSets().stream().map(s->s.elements).forEach(System.out::println);
    }
*/

}
