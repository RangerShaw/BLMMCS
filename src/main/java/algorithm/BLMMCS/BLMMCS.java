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
    private List<BLMMCSNode> BLMMCSNodes = new ArrayList<>();

    /**
     * record nodes that have been walked down in a batch, by the hashCode of its elements
     */
    private Set<Integer> walkedDown;

    /**
     * record nodes that have been walked up in a batch, by the hashCode of its elements
     */
    private Map<Integer, Boolean> walkedUp;

    private boolean walkDownLastTime = true;

    public BLMMCS(int nEle, List<BitSet> toCover) {
        nElements = nEle;
        subsetsToCover = toCover.stream().map(Subset::new).collect(Collectors.toCollection(ArrayList::new));
        BLMMCSNodes = new ArrayList<>();
        walkedDown = new HashSet<>();
        walkedUp = new HashMap<>();
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return BLMMCSNodes.stream()
                .filter(BLMMCSNode::isGlobalMinimal)
                .map(BLMMCSNode::getElements)
                .collect(Collectors.toList());
    }

    public List<BLMMCSNode> getAllCoverSets() {
        return BLMMCSNodes;
    }

    public void initiate() {
        BitSet S = new BitSet(nElements);
        LinkedList<Subset> uncov = new LinkedList<>(subsetsToCover);
        ArrayList<ArrayList<Subset>> crit = new ArrayList<>(nElements);
        ArrayList<ArrayList<Subset>> coverMap = new ArrayList<>(nElements);

        for (int i = 0; i < nElements; i++) {
            crit.add(new ArrayList<>());
            coverMap.add(new ArrayList<>());
        }

        BLMMCSNode emptyCover = new BLMMCSNode(nElements, S, uncov, crit, coverMap);
        walkDown(emptyCover, BLMMCSNodes);

        walkDownLastTime = true;
    }

    /**
     * down from S, find all locally minimal cover sets that are minimal on its local branch
     */
    public void walkDown(BLMMCSNode S, List<BLMMCSNode> res) {
        if (walkedDown.contains(S.hashCode())) return;
        walkedDown.add(S.hashCode());

        if (S.isCover()) {
            res.add(S);
            return;
        }

        // TODO: prune: remove an ele from S and check whether it's been walked?
        S.getAddCandidates().forEach(e -> {
            BLMMCSNode childS = S.getChildNode(e);
            walkDown(childS, res);
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
        walkDownLastTime = true;
    }

    /**
     * keep Node S IF
     * 1. S is a cover, and
     * 2. S is global minimal or S has non-cover parents
     *
     * @return true IFF S is a cover
     */
    // TODO: will walkUp prune some branches?
    public boolean walkUp(BLMMCSNode S, List<BLMMCSNode> res) {
        if (walkedUp.containsKey(S.hashCode())) return walkedUp.get(S.hashCode());
        walkedUp.put(S.hashCode(), S.isCover());

        if (!S.isCover()) return false;

        if (S.isGlobalMinimal()) {
            res.add(S);
            return true;
        }

        boolean hasNonCoverParen = S.getRemoveCandidates()
                .mapToObj(e -> walkUp(S.getParentNode(e), res))
                .reduce(false, Boolean::logicalOr);

        if (hasNonCoverParen) res.add(S);

        return true;
    }

    public void processRemovedSubsets(List<BitSet> removedSets) {
        walkedUp.clear();
        List<Subset> removedSubsets = removedSets.stream().map(Subset::new).collect(Collectors.toList());
        //subsetsToCover.removeAll(removedSets);     // TODO: rewrite equals and hashCode?

        List<BLMMCSNode> newCoverSets = new ArrayList<>();
        for (BLMMCSNode prevNode : BLMMCSNodes) {
            prevNode.removeSubsets(removedSubsets);
            walkUp(prevNode, newCoverSets);
        }

        BLMMCSNodes = newCoverSets;
        walkDownLastTime = false;
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
