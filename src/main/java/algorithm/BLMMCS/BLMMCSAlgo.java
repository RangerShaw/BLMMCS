package algorithm.BLMMCS;

import java.util.*;
import java.util.stream.Collectors;

public class BLMMCSAlgo {

    public int nElements;

    List<BLMMCS> blmmcsList = new ArrayList<>();

    List<List<BitSet>> minimalCoverSets = new ArrayList<>();

    public BLMMCSAlgo(int nEle) {
        nElements = nEle;
        for (int i = 0; i < nElements; i++) {
            blmmcsList.add(new BLMMCS(nElements));
        }
    }

    public void initiate(List<BitSet> toCover) {
        //for (int rhs = 0; rhs < nElements; rhs++) {
        for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            blmmcsList.get(rhs).initiate(diffSets);
            minimalCoverSets.add(blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public void processAddedSubsets(List<BitSet> addedSets) {
        //for (int rhs = 0; rhs < nElements; rhs++) {
        for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] adding on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(addedSets, rhs);
            blmmcsList.get(rhs).processAddedSubsets(newDiffSets);
            minimalCoverSets.set(rhs, blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public void processRemovedSubsets(List<BitSet> removedSets) {
        //for (int rhs = 0; rhs < nElements; rhs++) {
        for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] removing on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(removedSets, rhs);
            blmmcsList.get(rhs).processRemovedSubsets(newDiffSets);
            minimalCoverSets.set(rhs, blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public List<List<BitSet>> getMinimalCoverSets() {
        return minimalCoverSets;
    }

    List<BitSet> generateDiffSetsOnRhs(List<BitSet> diffSets, int rhs) {
        // TODO: what if a new dsI is empty or an existing empty dsI is removed:
        //  return empty coverSets, but run blmmcs without the empty dsI
        List<BitSet> diffSetsOnRhs = new ArrayList<>();

        for (BitSet diffSet : diffSets) {
            if (diffSet.get(rhs)) {
                BitSet diffSetRhs = (BitSet) diffSet.clone();
                diffSetRhs.clear(rhs);
                diffSetsOnRhs.add(diffSetRhs);
            }
        }

        return diffSetsOnRhs;
    }

}
