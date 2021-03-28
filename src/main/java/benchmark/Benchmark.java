package benchmark;

import algorithm.differenceSet.PLI;
import util.DataLoader;

import java.util.List;

public class Benchmark {

    public static void main(String[] args) {
        String DATA_FILE_PATH = "dataFiles\\letter.csv";
        List<List<String>> csvData = DataLoader.readCsvFile(DATA_FILE_PATH);

        PLI pli = new PLI(csvData);
        pli.initiatePLI();
        pli.initiateDiffSet();
        System.out.println();
    }
}
