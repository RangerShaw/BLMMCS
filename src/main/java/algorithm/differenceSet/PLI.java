package algorithm.differenceSet;

import java.util.*;

public class PLI {
    int nAttributes;

    List<List<String>> data;

    /**
     * pliMap[i]["abc"] -> on attribute [i], which cluster "abc" belongs to
     */
    List<Map<String, Integer>> pliMap = new ArrayList<>();

    /**
     * pli[i]: clusters on attribute[i]
     * pli[i][j]: on attribute[i], what tuples belong to cluster[j]
     * pli[i][j][k]: tuple index
     */
    List<List<List<Integer>>> pli = new ArrayList<>();

    /**
     * inversePli[i][j]: on attribute [i], which cluster tuple[j] belongs to
     */
    List<List<Integer>> inversePli = new ArrayList<>();

    Set<BitSet> differenceSet = new HashSet<>();

    public PLI(List<List<String>> data) {
        this.data = data;
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        for (int i = 0; i < nAttributes; i++) {
            pli.add(new ArrayList<>());
            inversePli.add(new ArrayList<>());
            pliMap.add(new HashMap<>());
        }
    }

    public void initiatePLI() {
        for (int i = 0; i < nAttributes; i++) {
            Map<String, Integer> pliMap_i = pliMap.get(i);
            List<List<Integer>> pli_i = pli.get(i);
            List<Integer> inversePli_i = inversePli.get(i);

            for (int j = 0; j < data.size(); j++) {
                int cluster = pliMap_i.getOrDefault(data.get(j).get(i), pli_i.size());
                if (!pliMap_i.containsKey(data.get(j).get(i))) {
                    pliMap_i.put(data.get(j).get(i), cluster);
                    pli_i.add(new ArrayList<>());
                }

                pli_i.get(cluster).add(j);
                inversePli_i.add(cluster);
            }
        }
    }

    public void initiateDiffSet() {
        // TODO: change calculation method
        Set<Integer> addedDiffSet = new HashSet<>();
        for (int i = 0; i < inversePli.get(0).size() - 1; i++) {
            for (int j = i + 1; j < inversePli.get(0).size(); j++) {
                BitSet s = new BitSet(nAttributes);
                for (int k = 0; k < inversePli.size(); k++) {
                    if (!inversePli.get(k).get(i).equals(inversePli.get(k).get(j)))
                        s.set(k);
                }
                if(!s.isEmpty() && !addedDiffSet.contains(s.hashCode())) {
                    differenceSet.add(s);
                    addedDiffSet.add(s.hashCode());
                }
            }
        }

    }

}
