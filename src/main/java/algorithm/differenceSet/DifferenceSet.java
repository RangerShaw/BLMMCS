package algorithm.differenceSet;

import java.util.*;
import java.util.stream.Collectors;

public class DifferenceSet {

    int nAttributes;

    int nTuples;

    Set<BitSet> diffSets = new HashSet<>();

    Set<FastBitSet> fastDiffSets = new HashSet<>();

    Set<Integer> dfHash = new HashSet<>();

    public DifferenceSet() {
    }

    public void initiateDataStructure(List<List<Integer>> inversePli) {
        nAttributes = inversePli.size();
        nTuples = inversePli.isEmpty() ? 0 : inversePli.get(0).size();
    }

    public Set<BitSet> generateDiffSets(List<List<Integer>> inversePli) {
        initiateDataStructure(inversePli);

        for (int t1 = 0; t1 < nTuples - 1; t1++) {
            for (int t2 = t1 + 1; t2 < nTuples; t2++) {
                BitSet diffSet = new BitSet(nAttributes);
                for (int e = 0; e < nAttributes; e++)
                    //if (inversePli.get(e).get(t1).equals(inversePli.get(e).get(t2)))
                    if (!inversePli.get(e).get(t1).equals(inversePli.get(e).get(t2)))
                        diffSet.set(e);
                diffSets.add(diffSet);
            }
        }

        fastDiffSets = diffSets.stream().map(df -> new FastBitSet(nAttributes, df, true)).collect(Collectors.toSet());
        dfHash = diffSets.stream().map(ds -> DifferenceSet.bitsetToInverseInt(nAttributes, ds)).collect(Collectors.toSet());

        return diffSets;
    }


    /**
     * @param updatedClusters  On each attribute, indexes of clusters updated.
     *                         Corresponding to Inout Diff Sets derived from an existing tuple and an inserted tuple
     * @param insertedClusters On each attribute, index of the first new cluster.
     *                         Corresponding to Inner Diff Sets derived from two inserted tuples
     */
    void updateDiffSets(List<List<List<Integer>>> pli, int nInsertedTuples,
                        List<Set<Integer>> updatedClusters, List<Integer> insertedClusters, List<BitSet> newDiffSets) {

        // agreeSets[0..nInsertedTuples-1][0..nTuples-1]: Inout Diff Sets
        // agreeSets[0..nInsertedTuples-1][nTuples..nTuples+nInsertedTuples-1]: Inner Diff Sets
        BitSet[][] agreeSets = new BitSet[nInsertedTuples][nTuples + nInsertedTuples];
        for (int i = 0; i < nInsertedTuples; i++) {
            for (int j = 0; j < nTuples + i; j++) {
                agreeSets[i][j] = new BitSet(nAttributes);
                agreeSets[i][j].set(0, nAttributes);
            }
        }

        for (int e = 0; e < nAttributes; e++) {
            for (int c : updatedClusters.get(e)) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = clst.size() - 1; clst.get(i) >= nTuples; i--)
                    for (int j = 0; clst.get(j) < nTuples; j++)
                        agreeSets[clst.get(i) - nTuples][clst.get(j)].clear(e);
            }
            for (int c = insertedClusters.get(e); c < pli.get(e).size(); c++) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = 1; i < clst.size() - 1; i++)
                    for (int j = 0; j < i; j++)
                        agreeSets[clst.get(i) - nTuples][clst.get(j) - nTuples].clear(e);
            }
        }

        for (int i = 0; i < nInsertedTuples; i++) {
            for (int j = 0; j < nTuples + i; j++) {
                if (diffSets.add(agreeSets[i][j])) newDiffSets.add(agreeSets[i][j]);
            }
        }
    }

    void updateDiffSets3(List<List<List<Integer>>> pli, int nInsertedTuples,
                         List<Set<Integer>> updatedClusters, List<Integer> insertedClusters, List<BitSet> newDiffSets) {

        long startTime1 = System.nanoTime();
        boolean[][][] agreeSetsMap = new boolean[nInsertedTuples][nTuples + nInsertedTuples][nAttributes];

        for (int e = 0; e < nAttributes; e++) {
            for (int c : updatedClusters.get(e)) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = clst.size() - 1; clst.get(i) >= nTuples; i--)
                    for (int j = 0; clst.get(j) < nTuples; j++)
                        agreeSetsMap[clst.get(i) - nTuples][clst.get(j)][e] = true;
            }
            for (int c = insertedClusters.get(e); c < pli.get(e).size(); c++) {
                List<Integer> clst = pli.get(e).get(c);
                for (int i = 1; i < clst.size() - 1; i++)
                    for (int j = 0; j < i; j++)
                        agreeSetsMap[clst.get(i) - nTuples][clst.get(j) - nTuples][e] = true;
            }
        }
        long endTime1 = System.nanoTime();
        System.out.println("updateDiffSets3 runtime 1: " + (endTime1 - startTime1) / 1000000 + "ms");

        long startTime2 = System.nanoTime();
        for (int i = 0; i < nInsertedTuples; i++) {
            for (int j = 0; j < nTuples + i; j++) {
                if (dfHash.add(binaryToInt(agreeSetsMap[i][j])))
                    newDiffSets.add(binaryToInverseBitSet(agreeSetsMap[i][j]));
            }
        }
        long endTime2 = System.nanoTime();
        System.out.println("updateDiffSets3 runtime 2: " + (endTime2 - startTime2) / 1000000 + "ms");
    }

    public List<BitSet> insertData(List<List<List<Integer>>> pli, int nInsertedTuples,
                                   List<Set<Integer>> updatedClusters, List<Integer> insertedClusters) {

        List<BitSet> newDiffSets = new ArrayList<>();
        updateDiffSets3(pli, nInsertedTuples, updatedClusters, insertedClusters, newDiffSets);

        nTuples += nInsertedTuples;

        return newDiffSets;
    }

    public List<BitSet> getDiffSets() {
        List<BitSet> diffSetList = new ArrayList<>(diffSets);
//        for (BitSet ds : diffSetList)
//            ds.flip(0, nAttributes);
        return diffSetList;
    }


//    /**
//     * Inout Diff Sets: derived from an existing tuple and an inserted tuple
//     *
//     * @param updatedClusters updatedClusters[e]: on attribute e, indexes of clusters updated
//     */
//    void generateInoutDiffSets(List<List<List<Integer>>> pli, int nInsertedTuples, List<Set<Integer>> updatedClusters, List<BitSet> insertDiffSets) {
//        // generate agree sets
//        BitSet[][] inoutAgreeSets = new BitSet[nInsertedTuples][nTuples];
//        for (int i = 0; i < nInsertedTuples; i++)
//            for (int j = 0; j < nTuples; j++) {
//                inoutAgreeSets[i][j] = new BitSet(nAttributes);
//                inoutAgreeSets[i][j].set(0, nAttributes);
//            }
//
//
//        for (int e = 0; e < nAttributes; e++) {
//            for (int c : updatedClusters.get(e)) {
//                List<Integer> clst = pli.get(e).get(c);
//                for (int i = 0; i < clst.size() && clst.get(i) < nTuples; i++)
//                    for (int j = clst.size() - 1; j >= 0 && clst.get(j) >= nTuples; j--)
//                        inoutAgreeSets[clst.get(j) - nTuples][clst.get(i)].clear(e);
//            }
//        }
//
//        // generate difference sets
//        for (int i = 0; i < nInsertedTuples; i++) {
//            for (int j = 0; j < nTuples; j++) {
//                BitSet diffSet = inoutAgreeSets[i][j];
//                //diffSet.flip(0, nAttributes);
//                if (diffSets.add(diffSet))
//                    insertDiffSets.add(diffSet);
//            }
//        }
//    }
//
//    /**
//     * Inner Diff Sets: derived from two inserted tuples
//     *
//     * @param insertedClusters insertedClusters[e]: on attribute e, index of the first new cluster
//     */
//    void generateInnerDiffSets(List<List<List<Integer>>> pli, int nInsertedTuples, List<Integer> insertedClusters, List<BitSet> insertDiffSets) {
//        // generate agree sets
//        BitSet[][] innerAgreeSets = new BitSet[nInsertedTuples][nInsertedTuples];
//        for (int i = 0; i < nInsertedTuples - 1; i++) {
//            for (int j = i + 1; j < nInsertedTuples; j++) {
//                innerAgreeSets[i][j] = new BitSet(nAttributes);
//                innerAgreeSets[i][j].set(0, nAttributes);
//            }
//        }
//
//        for (int e = 0; e < nAttributes; e++) {
//            for (int c = insertedClusters.get(e); c < pli.get(e).size(); c++) {
//                List<Integer> clst = pli.get(e).get(c);
//                for (int i = 0; i < clst.size() - 1; i++) {
//                    for (int j = i + 1; j < clst.size(); j++)
//                        innerAgreeSets[clst.get(i) - nTuples][clst.get(j) - nTuples].clear(e);
//                }
//            }
//        }
//
//        // generate difference sets
//        for (int i = 0; i < nInsertedTuples - 1; i++) {
//            for (int j = i + 1; j < nInsertedTuples; j++) {
//                BitSet diffSet = innerAgreeSets[i][j];
//                //diffSet.flip(0, nAttributes);
//                if (diffSets.add(diffSet))
//                    insertDiffSets.add(diffSet);
//            }
//        }
//    }

    public static int bitsetToInverseInt(int nAttributes, BitSet bs) {
        int x = 0;
        for (int i = 0; i < nAttributes; i++) {
            x <<= 1;
            x |= bs.get(i) ? 0 : 1;
        }
        return x;
    }

    public static int binaryToInt(boolean[] bools) {
        int x = 0;
        for (boolean b : bools) {
            x <<= 1;
            x |= b ? 1 : 0;
        }
        return x;
    }

    public static BitSet binaryToInverseBitSet(boolean[] bools) {
        BitSet bs = new BitSet(bools.length);
        for (int i = 0; i < bools.length; i++)
            if (!bools[i]) bs.set(i);
        return bs;
    }
}
