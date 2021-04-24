package algorithm.differenceSet;

import java.util.*;

public class PliDsConnector {

    public int nTuples;

    public int nAttributes;

    PLI Pli;

    DifferenceSet differenceSet;

    public PliDsConnector(List<List<String>> data) {
        nTuples = data.size();
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        System.out.println("generate PLI");
        Pli = new PLI();
        Pli.generatePLI(data);

        System.out.println("generate Diff Sets");
        differenceSet = new DifferenceSet();
        differenceSet.generateDiffSets(Pli.getInversePli());
    }

    public List<BitSet> getDiffSets() {
        return differenceSet.getDiffSets();
    }

    public List<BitSet> insertData(List<List<String>> insertedData) {
        List<Set<Integer>> updatedClusters = new ArrayList<>();
        List<Integer> insertedClusters = new ArrayList<>();

        //long startTime1 = System.nanoTime();
        Pli.insertData(insertedData, updatedClusters, insertedClusters);
        //long endTime1 = System.nanoTime();
        //System.out.println("update PLI runtime: " + (endTime1 - startTime1) / 1000000 + "ms");

        //long startTime2 = System.nanoTime();
        List<BitSet> newDiffSets = differenceSet.insertData(Pli.getPli(), insertedData.size(), updatedClusters, insertedClusters);
        //long endTime2 = System.nanoTime();
        //System.out.println("update DF runtime: " + (endTime2 - startTime2) / 1000000 + "ms");

        return newDiffSets;
    }
}
