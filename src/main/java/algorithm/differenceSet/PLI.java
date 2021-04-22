package algorithm.differenceSet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class PLI {

    public int nTuples;

    public int nAttributes;

    /**
     * pliMap[i]["abc"] -> on attribute [i], which cluster "abc" belongs to
     */
    List<Map<String, Integer>> pliMap = new ArrayList<>();

    /**
     * pli[i]: clusters on attribute[i];
     * pli[i][j]: on attribute[i], what tuples belong to cluster[j];
     * pli[i][j][k]: tuple index
     */
    List<List<List<Integer>>> pli = new ArrayList<>();

    /**
     * inversePli[i][j]: on attribute [i], which cluster tuple[j] belongs to
     */
    //List<List<Integer>> inversePli = new ArrayList<>();

    Map<BitSet, Integer> diffSets = new HashMap<>();

    List<BitSet> differenceSets = new ArrayList<>();

    public PLI() {
    }

    public void initiate(List<List<String>> data) {
        nTuples = data.size();
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        for (int i = 0; i < nAttributes; i++) {
            pli.add(new ArrayList<>());
            pliMap.add(new HashMap<>());
            //inversePli.add(new ArrayList<>());
        }

        generatePLI(data, 0);
    }


    /**
     * @param offset for the number of inserted tuples
     */
    void generatePLI(List<List<String>> insertedData, int offset) {
        List<List<Integer>> updatedClusters = new ArrayList<>();

        for (int e = 0; e < nAttributes; e++) {
            updatedClusters.add(new ArrayList<>());

            Map<String, Integer> pliMapE = pliMap.get(e);
            List<List<Integer>> pliE = pli.get(e);

            for (int t = 0; t < insertedData.size(); t++) {
                int cluster;
                String str = insertedData.get(t).get(e);

                if (pliMapE.containsKey(str))
                    cluster = pliMapE.get(str);
                else {
                    cluster = pliE.size();
                    pliMapE.put(str, cluster);
                    pliE.add(new ArrayList<>());
                }

                pliE.get(cluster).add(t + offset);
                if (offset != 0) updatedClusters.get(e).add(cluster);
            }
        }

        // remove clusters whose size < 2
        for (List<List<Integer>> pliE : pli) {
            pliE.removeIf(clst -> clst.size() < 2);
            for (List<Integer> clst : pliE)
                clst.sort(Integer::compareTo);
        }

        insertedData.clear();
    }

    public void insertData(List<List<String>> insertedData) {
        int nInsertedTuples = insertedData.size();
        int offset = nTuples;

        List<List<Integer>> updatedClusters = generatePLI(insertedData, offset);

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
    }

    public Map<BitSet, Integer> genDiffSets() {
        BitSet[][] agreeSets = new BitSet[nTuples][nTuples];
        for (int i = 0; i < nTuples - 1; i++)
            for (int j = i; j < nTuples; j++)
                agreeSets[i][j] = new BitSet(nAttributes);

        // generate agree sets
        for (int e = 0; e < nAttributes; e++) {
            for (List<Integer> clst : pli.get(e))
                for (int i = 0; i < clst.size() - 1; i++)
                    for (int j = i + 1; j < clst.size(); j++) {
                        agreeSets[clst.get(i)][clst.get(j)].set(e); //clst.get(i) < clst.get(j)
                    }
        }

        // generate difference sets
        for (int i = 0; i < nTuples - 1; i++) {
            for (int j = i + 1; j < nTuples; j++) {
                BitSet diffSet = agreeSets[i][j];
                diffSet.flip(0, nAttributes);
                diffSets.put(diffSet, diffSets.getOrDefault(diffSet, 0) + 1);
            }
        }

        return diffSets;
    }


//    public List<BitSet> generateDiffSets() {
//        List<List<Integer>> strips = new ArrayList<>();
//        pli.stream()
//                .flatMap(Collection::stream)
//                .filter(cluster -> cluster.size() > 1)
//                .forEach(strips::add);
//
//        System.out.println("Computing agree sets...");
//        Set<BitSet> agreeSets = new HashSet<>();
//        for (List<Integer> strip : strips) {
//            for (int i = 0; i < strip.size(); i++) {
//                for (int j = i + 1; j < strip.size(); j++) {
//                    BitSet agreeSet_ij = new BitSet(nAttributes);
//                    for (int k = 0; k < nAttributes; k++)
//                        if (inversePli.get(k).get(i).equals(inversePli.get(k).get(j)))
//                            agreeSet_ij.set(k);
//                    agreeSets.add(agreeSet_ij);
//                }
//            }
//        }
//
//        System.out.println("Computing diff sets...");
//        try {
//            PrintWriter out = new PrintWriter("letterDS.txt");
//
//            for (BitSet agreeSet : agreeSets) {
//                BitSet diffSet = (BitSet) agreeSet.clone();
//                diffSet.flip(0, nAttributes);
//                differenceSets.add(diffSet);
//
//                out.println(diffSet);
//                if (diffSet.equals(BitSet.valueOf(new long[]{0b10011010001})))
//                    System.out.println("0467 10");
//            }
//            out.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return differenceSets;
//    }

}
