package algorithm.differenceSet;

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
    List<List<Integer>> inversePli = new ArrayList<>();

    Map<BitSet, Integer> diffSets = new HashMap<>();

    List<BitSet> differenceSets = new ArrayList<>();


    public PLI(int nAttributes) {
        this.nAttributes = nAttributes;
    }

    public PLI(List<List<String>> data) {
        nTuples = data.size();
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        for (int i = 0; i < nAttributes; i++) {
            pli.add(new ArrayList<>());
            pliMap.add(new HashMap<>());
            inversePli.add(new ArrayList<>());
        }

        generatePLI(data);
    }

    List<List<List<Integer>>> getPli() {
        return pli;
    }

    public List<List<Integer>> getInversePli() {
        return inversePli;
    }

    List<List<List<Integer>>> generatePLI(List<List<String>> insertedData) {
        for (int e = 0; e < nAttributes; e++) {
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

                pliE.get(cluster).add(t);
                inversePli.get(e).add(cluster);
            }
        }

        // remove clusters whose size < 2
        for (List<List<Integer>> pliE : pli) {
            pliE.removeIf(clst -> clst.size() < 2);
            for (List<Integer> clst : pliE)
                clst.sort(Integer::compareTo);
        }

        return pli;
    }

    List<List<Integer>> insertData(List<List<String>> insertedData) {
        List<List<Integer>> updatedClusters = new ArrayList<>();
        int offset = nTuples;

        nTuples += insertedData.size();

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
                updatedClusters.get(e).add(cluster);
            }
        }

        // TODO: only check new clusters
        for (List<List<Integer>> pliE : pli) {
            pliE.removeIf(clst -> clst.size() < 2);
        }

        return updatedClusters;
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


}
