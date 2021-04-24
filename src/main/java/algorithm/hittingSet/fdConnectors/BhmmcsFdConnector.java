package algorithm.hittingSet.fdConnectors;

import algorithm.hittingSet.BHMMCS.Bhmmcs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class BhmmcsFdConnector extends FdConnector {

    /**
     * Bhmmcs algorithms on different rhs
     */
    List<Bhmmcs> bhmmcsList = new ArrayList<>();


    /**
     * @param nElements number of attributes of input dataset
     */
    public BhmmcsFdConnector(int nElements) {
        super(nElements);
        for (int i = 0; i < nElements; i++)
            bhmmcsList.add(new Bhmmcs(nElements));
    }

    /**
     * @param toCover all subsets (different sets) to be covered
     */
    public void initiate(List<BitSet> toCover) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            //System.out.println("  [FdConnector] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            bhmmcsList.get(rhs).initiate(diffSets);
            minFDs.add(bhmmcsList.get(rhs).getMinCoverSets());
        }
    }

    public void insertSubsets(List<BitSet> addedSets) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            //System.out.println("  [FdConnector] adding on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(addedSets, rhs);
            bhmmcsList.get(rhs).insertSubsets(newDiffSets);
            minFDs.set(rhs, bhmmcsList.get(rhs).getMinCoverSets());
        }
    }

    public void removeSubsets(List<BitSet> remainedSets) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            // System.out.println(" [BLMMCS] removing on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(remainedSets, rhs);
            bhmmcsList.get(rhs).removeSubsets(newDiffSets);
            minFDs.set(rhs, bhmmcsList.get(rhs).getMinCoverSets());
        }
    }

}
