package algorithm.hittingSet.fdConnectors;

import algorithm.hittingSet.BLMMCS.BLMMCS;

import java.util.*;

/**
 * BLMMCS Wrapper for FD discovery
 */
public class BlmmcsFdConnector extends FdConnector{

    List<BLMMCS> blmmcsList = new ArrayList<>();

    List<List<BitSet>> minimalCoverSets = new ArrayList<>();

    public BlmmcsFdConnector(int nEle) {
        super(nEle);
        for (int i = 0; i < nElements; i++)
            blmmcsList.add(new BLMMCS(nElements));
    }

    public void initiate(List<BitSet> toCover) {
        for (int rhs = 0; rhs < 1; rhs++) {
        //for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            blmmcsList.get(rhs).initiate(diffSets);
            minimalCoverSets.add(blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public void insertSubsets(List<BitSet> addedSets) {
        for (int rhs = 0; rhs < 1; rhs++) {
        //for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] adding on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(addedSets, rhs);
            blmmcsList.get(rhs).insertSubsets(newDiffSets);
            minimalCoverSets.set(rhs, blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public void removeSubsets(List<BitSet> removedSets) {
        for (int rhs = 0; rhs < 1; rhs++) {
        //for (int rhs = 0; rhs < 1; rhs++) {
            System.out.println(" [BLMMCS] removing on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(removedSets, rhs);
            blmmcsList.get(rhs).removeSubsets(newDiffSets);
            minimalCoverSets.set(rhs, blmmcsList.get(rhs).getGlobalMinCoverSets());
        }
    }

    public List<List<BitSet>> getMinimalCoverSets() {
        return minimalCoverSets;
    }

}
