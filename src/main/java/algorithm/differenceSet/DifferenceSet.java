package algorithm.differenceSet;

import java.util.*;

public class DifferenceSet {

    int nAttributes;

    int nTuples;

    List<List<List<Integer>>> pli;

    Map<BitSet, Integer> diffSets = new HashMap<>();

    public DifferenceSet(int nAttributes) {
        this.nAttributes = nAttributes;
    }

    public DifferenceSet(PLI Pli) {
        nAttributes = Pli.nAttributes;
        nTuples = Pli.nTuples;
        pli = Pli.pli;
    }

    public Map<BitSet, Integer> getDiffSets() {
        return diffSets;
    }

    public Map<BitSet, Integer> generateDiffSets(List<List<Integer>> inversePli) {
        nTuples = inversePli.size();
        for (int t1 = 0; t1 < nTuples - 1; t1++) {
            for (int t2 = t1 + 1; t2 < nTuples; t2++) {
                BitSet diffSet = new BitSet(nAttributes);
                for (int e = 0; e < nAttributes; e++)
                    if (!inversePli.get(t1).get(e).equals(inversePli.get(t2).get(e)))
                        diffSet.set(e);
                diffSets.put(diffSet, diffSets.getOrDefault(diffSet, 0) + 1);
            }
        }
        return diffSets;
    }


    public Map<BitSet, Integer> insertData(int nInsertedTuples, List<List<Integer>> updatedClusters) {
        int offset = nTuples;

        // generate agree sets
        BitSet[][] inoutAgreeSets = new BitSet[nTuples][nInsertedTuples];           // from an existing tuple and an inserted tuple
        BitSet[][] innerAgreeSets = new BitSet[nInsertedTuples][nInsertedTuples];   // from two inserted tuples

        for (int i = 0; i < nTuples - 1; i++)
            for (int j = i + 1; j < nInsertedTuples; j++)
                inoutAgreeSets[i][j] = new BitSet(nAttributes);
        for (int i = 0; i < nInsertedTuples - 1; i++)
            for (int j = i + 1; j < nInsertedTuples; j++)
                innerAgreeSets[i][j] = new BitSet(nAttributes);

        for (int e = 0; e < nAttributes; e++) {
            for (int c : updatedClusters.get(e)) {
                List<Integer> clst = pli.get(e).get(c);

                // inout agree sets
                for (int i = 0; i < clst.size() && clst.get(i) < nTuples; i++)
                    for (int j = clst.size() - 1; j >= 0 && clst.get(j) >= nTuples; j--)
                        inoutAgreeSets[clst.get(i)][clst.get(j) - offset].set(e);  //clst.get(i) < clst.get(j)

                // inner agree sets
                for (int j = clst.size() - 1; j >= 0 && clst.get(j) >= nTuples; j--)
                    for (int i = j - 1; i >= 0 && clst.get(i) >= nTuples; i--)
                        innerAgreeSets[clst.get(i) - offset][clst.get(j) - offset].set(e);
            }
        }

        // generate difference sets
        for (int i = 0; i < nTuples - 1; i++) {
            for (int j = i + 1; j < nInsertedTuples; j++) {
                BitSet diffSet = inoutAgreeSets[i][j];
                diffSet.flip(0, nAttributes);
                diffSets.put(diffSet, diffSets.getOrDefault(diffSet, 0) + 1);
            }
        }

        for (int i = 0; i < nInsertedTuples - 1; i++) {
            for (int j = i + 1; j < nInsertedTuples; j++) {
                BitSet diffSet = inoutAgreeSets[i][j];
                diffSet.flip(0, nAttributes);
                diffSets.put(diffSet, diffSets.getOrDefault(diffSet, 0) + 1);
            }
        }

        return diffSets;
    }
}
