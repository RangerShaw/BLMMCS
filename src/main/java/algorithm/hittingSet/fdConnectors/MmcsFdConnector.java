package algorithm.hittingSet.fdConnectors;

import algorithm.hittingSet.MMCS.Mmcs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MmcsFdConnector extends FdConnector {

    /**
     * Mmcs algorithms on different rhs
     */
    List<Mmcs> MmcsList = new ArrayList<>();

    /**
     * @param nElements number of attributes of input dataset
     */
    public MmcsFdConnector(int nElements) {
        super(nElements);
        for (int i = 0; i < nElements; i++)
            MmcsList.add(new Mmcs(nElements));
    }

    /**
     * @param toCover all subsets (different sets) to be covered
     */
    public void initiate(List<BitSet> toCover) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            System.out.println("  [FdConnector] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            MmcsList.get(rhs).initiate(diffSets);
            minFDs.add(MmcsList.get(rhs).getMinCoverSets());
        }
    }

}
