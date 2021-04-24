package algorithm.hittingSet.fdConnectors;

import algorithm.hittingSet.EMMCS.Emmcs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class EmmcsFdConnector extends FdConnector {

    /**
     * Emmcs algorithms on different rhs
     */
    List<Emmcs> emmcsList = new ArrayList<>();


    /**
     * @param nElements number of attributes of input dataset
     */
    public EmmcsFdConnector(int nElements) {
        super(nElements);
        for (int i = 0; i < nElements; i++)
            emmcsList.add(new Emmcs(nElements));
    }

    /**
     * @param toCover all subsets (different sets) to be covered
     */
    public void initiate(List<BitSet> toCover) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            System.out.println("  [FdConnector] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            emmcsList.get(rhs).initiate(diffSets);
            minFDs.add(emmcsList.get(rhs).getMinCoverSets());
        }
    }

    public void insertSubsets(List<BitSet> addedSets) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            System.out.println("  [FdConnector] adding on rhs " + rhs + "...");
            List<BitSet> newDiffSets = generateDiffSetsOnRhs(addedSets, rhs);
            emmcsList.get(rhs).insertSubsets(newDiffSets);
            minFDs.set(rhs, emmcsList.get(rhs).getMinCoverSets());
        }
    }

}
