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
    private List<Subset> subsetsToCover;

    /**
     * hitting sets that are minimal on its local branch
     */
    private List<CoverSet> coverSets = new ArrayList<>();

    /**
     * record CoverSets that have been walked, by the hashCode of its elements
     */
    private Set<Integer> walked;


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

    public BLMMCS(int nEle, List<BitSet> toCover) {
        nElements = nEle;
        subsetsToCover = toCover.stream().map(Subset::new).collect(Collectors.toCollection(ArrayList::new));
        coverSets = new ArrayList<>();
        walked = new HashSet<>();
    }

    public List<BitSet> getGlobalMinCoverSets() {
        return coverSets.stream()
                .filter(CoverSet::isGlobalMinimal)
                .map(CoverSet::getElements)
                .collect(Collectors.toList());
    }

    public List<CoverSet> getAllCoverSets() {
        return coverSets;
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

        CoverSet emptyCover = new CoverSet(nElements, S, uncov, crit, coverMap);
        walkDown(emptyCover, coverSets);
    }

    /**
     * down from S, find all locally minimal cover sets that are minimal on its local branch
     */
    public void walkDown(CoverSet S, List<CoverSet> res) {
        if (walked.contains(S.hashCode())) return;
        walked.add(S.hashCode());

        if (S.isCover()) {
            res.add(S);
            return;
        }

        // TODO: prune: remove an ele from S and check whether it's been walked?

        // TODO: optimize travel order from small to large?
        S.getCandidates().forEach(e -> {
            CoverSet childS = S.getChildS(e);
            walkDown(childS, res);
        });
    }


    public void processAddedSubsets(List<BitSet> addedSets) {
        walked.clear();
        List<Subset> addedSubsets = addedSets.stream().map(Subset::new).collect(Collectors.toList());
        subsetsToCover.addAll(addedSubsets);

        List<CoverSet> newCoverSets = new ArrayList<>();
        for (CoverSet prevCoverSet : coverSets) {
            prevCoverSet.addSubsets(addedSubsets);
            walkDown(prevCoverSet, newCoverSets);
        }

        coverSets = newCoverSets;
    }

    public void walkUp(CoverSet S, List<CoverSet> res) {
        if (walked.contains(S.hashCode())) return;
        walked.add(S.hashCode());

        // TODO: will walkUp prune some branches?
    }

    public void processRemovedSubsets(List<Subset> removedSubsets) {
        walked.clear();
        subsetsToCover.removeAll(removedSubsets);     // TODO: rewrite equals and hashCode?

        List<CoverSet> newCoverSets = new ArrayList<>();

        for (CoverSet S : coverSets) {
            S.removeSubSets(removedSubsets);
            walkUp(S, newCoverSets);
        }

        coverSets = newCoverSets;
    }
}
