package algorithm.differenceSet;

import java.util.*;

public class ApproxDifferenceSet {

    int nAttributes;

    int nTuples;

    Map<BitSet, Integer> diffSets = new HashMap<>();


    public ApproxDifferenceSet() {
    }

    public void initiateDataStructure(List<List<Integer>> inversePli) {
        nAttributes = inversePli.size();
        nTuples = inversePli.isEmpty() ? 0 : inversePli.get(0).size();
    }

    public Map<BitSet, Integer> generateDiffSets(List<List<Integer>> inversePli) {
        initiateDataStructure(inversePli);

        for (int t1 = 0; t1 < nTuples - 1; t1++) {
            for (int t2 = t1 + 1; t2 < nTuples; t2++) {
                BitSet diffSet = new BitSet(nAttributes);
                for (int e = 0; e < nAttributes; e++)
                    if (!inversePli.get(e).get(t1).equals(inversePli.get(e).get(t2)))
                        diffSet.set(e);
                diffSets.put(diffSet, diffSets.getOrDefault(diffSet, 0) + 1);
            }
        }
        return diffSets;
    }

    /**
     * Inout Diff Sets: derived from an existing tuple and an inserted tuple
     *
     * @param updatedClusters updatedClusters[e]: on attribute e, indexes of clusters updated
     */
    void generateInoutDiffSets(List<List<List<Integer>>> pli, int nInsertedTuples, List<Set<Integer>> updatedClusters, Map<BitSet, Integer> insertDiffSets) {
        // generate agree sets
        BitSet[][] inoutAgreeSets = new BitSet[nInsertedTuples][nTuples];
        for (int i = 0; i < nInsertedTuples; i++)
            for (int j = 0; j < nTuples; j++) {
                inoutAgreeSets[i][j] = new BitSet(nAttributes);
                inoutAgreeSets[i][j].set(0, nAttributes);
            }


        for (int e = 0; e < nAttributes; e++) {
            for (int c : updatedClusters.get(e)) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = 0; i < clst.size() && clst.get(i) < nTuples; i++)
                    for (int j = clst.size() - 1; j >= 0 && clst.get(j) >= nTuples; j--)
                        inoutAgreeSets[clst.get(j) - nTuples][clst.get(i)].clear(e);
            }
        }

        // generate difference sets
        for (int i = 0; i < nInsertedTuples; i++) {
            for (int j = 0; j < nTuples; j++) {
                BitSet diffSet = inoutAgreeSets[i][j];
                //diffSet.flip(0, nAttributes);
                if (diffSets.containsKey(diffSet)) diffSets.put(diffSet, diffSets.get(diffSet) + 1);
                else {
                    insertDiffSets.put(diffSet, 1);
                    diffSets.put(diffSet, 1);
                }
            }
        }
    }

    /**
     * Inner Diff Sets: derived from two inserted tuples
     *
     * @param insertedClusters insertedClusters[e]: on attribute e, index of the first new cluster
     */
    void generateInnerDiffSets(List<List<List<Integer>>> pli, int nInsertedTuples, List<Integer> insertedClusters, Map<BitSet, Integer> insertDiffSets) {
        // generate agree sets
        BitSet[][] innerAgreeSets = new BitSet[nInsertedTuples][nInsertedTuples];
        for (int i = 0; i < nInsertedTuples - 1; i++) {
            for (int j = i + 1; j < nInsertedTuples; j++) {
                innerAgreeSets[i][j] = new BitSet(nAttributes);
                innerAgreeSets[i][j].set(0, nAttributes);
            }
        }

        for (int e = 0; e < nAttributes; e++) {
            for (int c = insertedClusters.get(e); c < pli.get(e).size(); c++) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = 0; i < clst.size() - 1; i++) {
                    for (int j = i + 1; j < clst.size(); j++)
                        innerAgreeSets[clst.get(i) - nTuples][clst.get(j) - nTuples].clear(e);
                }
            }
        }

        // generate difference sets
        for (int i = 0; i < nInsertedTuples - 1; i++) {
            for (int j = i + 1; j < nInsertedTuples; j++) {
                BitSet diffSet = innerAgreeSets[i][j];
                //diffSet.flip(0, nAttributes);
                if (diffSets.containsKey(diffSet)) diffSets.put(diffSet, diffSets.get(diffSet) + 1);
                else {
                    insertDiffSets.put(diffSet, 1);
                    diffSets.put(diffSet, 1);
                }
            }
        }
    }

    public Map<BitSet, Integer> insertData(List<List<List<Integer>>> pli, int nInsertedTuples,
                                           List<Set<Integer>> updatedClusters, List<Integer> insertedClusters) {
        Map<BitSet, Integer> newDiffSets = new HashMap<>();

        generateInoutDiffSets(pli, nInsertedTuples, updatedClusters, newDiffSets);
        generateInnerDiffSets(pli, nInsertedTuples, insertedClusters, newDiffSets);

        nTuples += nInsertedTuples;

        return newDiffSets;
    }

    public Map<BitSet, Integer> getDiffSets() {
        return diffSets;
    }

}
