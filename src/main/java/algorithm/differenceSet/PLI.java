package algorithm.differenceSet;

import java.util.*;
import java.util.stream.Collectors;

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


    public PLI() {
    }

    List<List<List<Integer>>> getPli() {
        return pli;
    }

    public List<List<Integer>> getInversePli() {
        return inversePli;
    }

    void initiateDataStructure(List<List<String>> data) {
        nTuples = data.size();
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        for (int i = 0; i < nAttributes; i++) {
            pli.add(new ArrayList<>());
            pliMap.add(new HashMap<>());
            inversePli.add(new ArrayList<>());
        }
    }

    List<List<List<Integer>>> generatePLI(List<List<String>> data) {
        initiateDataStructure(data);

        for (int e = 0; e < nAttributes; e++) {
            Map<String, Integer> pliMapE = pliMap.get(e);
            List<List<Integer>> pliE = pli.get(e);
            int cluster;

            for (int t = 0; t < data.size(); t++) {
                if (pliMapE.containsKey(data.get(t).get(e)))
                    cluster = pliMapE.get(data.get(t).get(e));
                else {
                    cluster = pliE.size();
                    pliMapE.put(data.get(t).get(e), cluster);
                    pliE.add(new ArrayList<>());
                }

                pliE.get(cluster).add(t);
                inversePli.get(e).add(cluster);
            }
        }

        return pli;
    }

    List<Set<Integer>> insertData(List<List<String>> insertedData, List<Set<Integer>> updatedClusters, List<Integer> insertedClusters) {
        int offset = nTuples;
        nTuples += insertedData.size();
        pli.stream().map(List::size).forEach(insertedClusters::add);

        for (int e = 0; e < nAttributes; e++) {
            updatedClusters.add(new HashSet<>());

            Map<String, Integer> pliMapE = pliMap.get(e);
            List<List<Integer>> pliE = pli.get(e);
            int cluster;

            for (int t = 0; t < insertedData.size(); t++) {
                if (pliMapE.containsKey(insertedData.get(t).get(e))) {
                    cluster = pliMapE.get(insertedData.get(t).get(e));
                    updatedClusters.get(e).add(cluster);
                } else {
                    cluster = pliE.size();
                    pliMapE.put(insertedData.get(t).get(e), cluster);
                    pliE.add(new ArrayList<>());
                }
                pliE.get(cluster).add(t + offset);
            }
        }

        return updatedClusters;
    }


}
