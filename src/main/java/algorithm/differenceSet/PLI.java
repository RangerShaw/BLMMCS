package algorithm.differenceSet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class PLI {
    public int nAttributes;

    List<List<String>> data;

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

    List<BitSet> differenceSets = new ArrayList<>();

    public PLI(List<List<String>> data) {
        this.data = data;
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        for (int i = 0; i < nAttributes; i++) {
            pli.add(new ArrayList<>());
            inversePli.add(new ArrayList<>());
            pliMap.add(new HashMap<>());
        }
    }

    public void generatePLI() {
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

        data.clear();
    }

    public List<BitSet> generateDiffSets() {
        List<List<Integer>> strips = new ArrayList<>();
        pli.stream()
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.size() > 1)
                .forEach(strips::add);

        System.out.println("Computing agree sets...");
        Set<BitSet> agreeSets = new HashSet<>();
        for (List<Integer> strip : strips) {
            for (int i = 0; i < strip.size(); i++) {
                for (int j = i + 1; j < strip.size(); j++) {
                    BitSet agreeSet_ij = new BitSet(nAttributes);
                    for (int k = 0; k < nAttributes; k++)
                        if (inversePli.get(k).get(i).equals(inversePli.get(k).get(j)))
                            agreeSet_ij.set(k);
                    agreeSets.add(agreeSet_ij);
                }
            }
        }

        System.out.println("Computing diff sets...");
        try {
            PrintWriter out = new PrintWriter("letterDS.txt");

            for (BitSet agreeSet : agreeSets) {
                BitSet diffSet = (BitSet) agreeSet.clone();
                diffSet.flip(0, nAttributes);
                differenceSets.add(diffSet);

                out.println(diffSet);
                if(diffSet.equals(BitSet.valueOf(new long[]{0b10011010001})))
                    System.out.println("0467 10");
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return differenceSets;
    }

}
