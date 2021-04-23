package algorithm.differenceSet;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class PliDsConnector {

    public int nTuples;

    public int nAttributes;

    PLI Pli;

    DifferenceSet differenceSet;

    public PliDsConnector(List<List<String>> data) {
        nTuples = data.size();
        nAttributes = data.isEmpty() ? 0 : data.get(0).size();

        Pli = new PLI(nAttributes);
        Pli.generatePLI(data);

        differenceSet = new DifferenceSet(nAttributes);
        differenceSet.generateDiffSets(Pli.getInversePli());
    }

    public Map<BitSet, Integer> getDiffSets() {
        return differenceSet.getDiffSets();
    }

    public void insertData(List<List<String>> insertedData) {
        List<List<Integer>> updatedClusters = Pli.insertData(insertedData);
        differenceSet.insertData(insertedData.size(), updatedClusters);
    }

}
