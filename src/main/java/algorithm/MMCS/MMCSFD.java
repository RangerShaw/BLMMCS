package algorithm.MMCS;

import algorithm.MMCS.MMCS;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * MMCS Wrapper for discovering FD
 */
public class MMCSFD {

    public int nElements;

    /**
     * MMCS algorithms on different attributes
     */
    List<MMCS> MMCSList = new ArrayList<>();

    /**
     * minimal cover sets on different attributes
     */
    List<List<BitSet>> minimalCoverSets = new ArrayList<>();

    /**
     * @param nEle number of attributes of input dataset
     */
    public MMCSFD(int nEle) {
        nElements = nEle;
        for (int i = 0; i < nElements; i++) {
            MMCSList.add(new MMCS(nElements));
        }
    }

    /**
     * @param toCover all subsets (different sets) to be covered
     */
    public void initiate(List<BitSet> toCover) {
        for (int rhs = 0; rhs < nElements; rhs++) {
            System.out.println(" [MMCS] initiating on rhs " + rhs + "...");
            List<BitSet> diffSets = generateDiffSetsOnRhs(toCover, rhs);
            MMCSList.get(rhs).initiate(diffSets);
            minimalCoverSets.add(MMCSList.get(rhs).getGlobalMinCoverSets());
            System.out.println(" # of FD on rhs " + rhs + ": " + minimalCoverSets.get(rhs).size());
        }
    }

    /**
     * @return minimal cover sets on different attribute
     */
    public List<List<BitSet>> getMinimalCoverSets() {
        return minimalCoverSets;
    }

    List<BitSet> generateDiffSetsOnRhs(List<BitSet> diffSets, int rhs) {
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
