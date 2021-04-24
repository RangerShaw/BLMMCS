package benchmark;

import algorithm.differenceSet.PliDsConnector;
import algorithm.hittingSet.fdConnectors.BhmmcsFdConnector;
import algorithm.hittingSet.fdConnectors.FdConnector;
import util.DataLoader;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class Benchmark {

    static String[] CSV_IN_FULL = new String[]{
            "dataFiles\\letter\\letter.csv",
            "dataFiles\\balance\\balance-scale.csv"
    };
    static String[] DS_IN_FULL = new String[]{
            "dataFiles\\letter\\letterDS_yya.txt",
            "dataFiles\\balance\\balance-scale.txt"
    };
    static String[] DS_IN_PART = new String[]{
            "dataFiles\\letter\\letter_15000_DS_yya.txt",
            "dataFiles\\balance\\balance-scale100.txt"
    };
    static String[] FD_OUT_FULL = new String[]{
            "dataFiles\\letter\\letterFD.txt",
            "dataFiles\\balance\\balance-scale_FD.txt"
    };
    static String[] FD_OUT_PART = new String[]{
            "dataFiles\\letter\\letter_15000_FD.txt",
            "dataFiles\\balance\\balance-scale100_FD.txt"
    };

//    static String[] MUL_DS_IN = new String[]{
//            "dataFiles\\letter\\multi\\letter_DS_15000.txt",
//            "dataFiles\\letter\\multi\\letter_DS_16000.txt",
//            "dataFiles\\letter\\multi\\letter_DS_17000.txt",
//            "dataFiles\\letter\\multi\\letter_DS_18000.txt",
//            "dataFiles\\letter\\multi\\letter_DS_19000.txt",
//            "dataFiles\\letter\\multi\\letter_DS_20000.txt"
//    };
//    static String[] MUL_FD_OUT = new String[]{
//            "dataFiles\\letter\\multi\\letter_FD_15000.txt",
//            "dataFiles\\letter\\multi\\letter_FD_16000.txt",
//            "dataFiles\\letter\\multi\\letter_FD_17000.txt",
//            "dataFiles\\letter\\multi\\letter_FD_18000.txt",
//            "dataFiles\\letter\\multi\\letter_FD_19000.txt",
//            "dataFiles\\letter\\multi\\letter_FD_20000.txt"
//    };

    static String[] MULTI_CSV_IN = new String[]{
            "dataFiles\\letter\\total\\letter_15000.csv",
            "dataFiles\\letter\\total\\letter_15000-16000.csv",
            "dataFiles\\letter\\total\\letter_16000-17000.csv",
            "dataFiles\\letter\\total\\letter_17000-18000.csv",
            "dataFiles\\letter\\total\\letter_18000-19000.csv",
            "dataFiles\\letter\\total\\letter_19000-20000.csv"
    };
    static String[] MULTI_FD_OUT = new String[]{
            "dataFiles\\letter\\total\\letter_FD_15000.txt",
            "dataFiles\\letter\\total\\letter_FD_15000-16000.txt",
            "dataFiles\\letter\\total\\letter_FD_16000-17000.txt",
            "dataFiles\\letter\\total\\letter_FD_17000-18000.txt",
            "dataFiles\\letter\\total\\letter_FD_18000-19000.txt",
            "dataFiles\\letter\\total\\letter_FD_19000-20000.txt"
    };


    public static void main(String[] args) {
        // load data
        // List<List<String>> csvData = DataLoader.readCsvFile(CSV_IN_FULL[dataset]);

        // initiate pli and differenceSet
        // List<BitSet> diffSetsAll = pli.generateDiffSets();


        //testMultiRemove(blmmcsfd);
        //testMultiAdd(blmmcsfd);
        //testRemove(blmmcsfd);
        //testAdd(blmmcsfd);
        testMMCS();
    }


    public static void testMMCS() {
        // load data
        List<List<String>> csvData = DataLoader.readCsvFile(MULTI_CSV_IN[0]);

        // initiate pli and differenceSet
        PliDsConnector dsConnector = new PliDsConnector(csvData);
        List<BitSet> initDiffSets = dsConnector.getDiffSets();
        System.out.println(initDiffSets.size());

        BhmmcsFdConnector fdConnector = new BhmmcsFdConnector(17);

        fdConnector.initiate(initDiffSets);
        System.out.println(fdConnector.getMinFDs().get(0).size());

        List<List<List<String>>> insertDatas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            insertDatas.add(DataLoader.readCsvFile(MULTI_CSV_IN[i + 1]));
        }

        List<List<BitSet>> insertDiffSets = new ArrayList<>();
        long startTime1 = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            System.out.println("insert data: " + (i + 1));
            insertDiffSets.add(dsConnector.insertData(insertDatas.get(i)));
            System.out.println("new diff sets: " + insertDiffSets.get(i).size());
        }
        long endTime1 = System.nanoTime();
        System.out.println("update PLI and DF runtime: " + (endTime1 - startTime1) / 1000000 + "ms");

        long startTime2 = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            System.out.println("insert fd: " + i);
            fdConnector.insertSubsets(insertDiffSets.get(i));
            System.out.println(fdConnector.getMinFDs().get(0).size());
        }
        long endTime2 = System.nanoTime();
        System.out.println("update FD runtime: " + (endTime2 - startTime2) / 1000000 + "ms");

//        MMCSFD mmcsfd = new MMCSFD(pli.nAttributes);
//
//        Map<BitSet, Integer> dsMap = DataLoader.readYyaDiffSets(MULTI_DS_IN[0]);
//        List<BitSet> baseDiffSets = new ArrayList<>(dsMap.keySet());
//        System.out.println("initiating MMCS...");
//        long startTime1 = System.nanoTime();
//        mmcsfd.initiate(baseDiffSets);
//        long endTime1 = System.nanoTime();
//        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");
//        printFDs(mmcsfd, MULTI_FD_OUT[0]);
//        System.out.println();
    }

//    public static void testMultiAdd(BlmmcsFdConnector blmmcsfd) {
//        // initiate on base 20000
//        Map<BitSet, Integer> baseDsMap = DataLoader.readYyaDiffSets(MULTI_DS_IN[0]);
//        List<BitSet> baseDiffSets = new ArrayList<>(baseDsMap.keySet());
//        System.out.println("initiating BLMMCS...");
//        long startTime1 = System.nanoTime();
//        blmmcsfd.initiate(baseDiffSets);
//        long endTime1 = System.nanoTime();
//        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");
//        printFDs(blmmcsfd, MULTI_FD_OUT[0]);
//
//        // load multi diff sets
//        System.out.println("Loading multi diff sets...");
//        Map<BitSet, Integer> prevDsMap = baseDsMap;
//        List<List<BitSet>> multiAddedDs = new ArrayList<>();
//        for (int i = 1; i < 6; i++) {
//            final Map<BitSet, Integer> finalPrevDsMap = prevDsMap;
//            Map<BitSet, Integer> currDsMap = DataLoader.readYyaDiffSets(MULTI_DS_IN[i]);
//            multiAddedDs.add(currDsMap.keySet().stream()
//                    .filter(ds -> !finalPrevDsMap.containsKey(ds)).collect(Collectors.toList()));
//            prevDsMap = currDsMap;
//        }
//
//        // run BLMMCS ADD
//        System.out.println("start adding...");
//        long startTime = System.nanoTime();
//        for (int i = 1; i < 6; i++) {
//            blmmcsfd.processAddedSubsets(multiAddedDs.get(i - 1));
//            printFDs(blmmcsfd, MULTI_FD_OUT[i]);    // remove during benchmark
//        }
//        long endTime = System.nanoTime();
//        System.out.println("total runtime of ADD: " + (endTime - startTime) / 1000000 + "ms");
//    }
//
//    public static void testMultiRemove(BLMMCSFD blmmcsfd) {
//        // initiate on base 20000
//        Map<BitSet, Integer> baseDsMap = DataLoader.readYyaDiffSets(MULTI_DS_IN[5]);
//        List<BitSet> baseDiffSets = new ArrayList<>(baseDsMap.keySet()); // diff sets on all attributes
//        System.out.println("initiating BLMMCS...");
//        long startTime1 = System.nanoTime();
//        blmmcsfd.initiate(baseDiffSets);
//        long endTime1 = System.nanoTime();
//        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");
//        printFDs(blmmcsfd, MULTI_FD_OUT[5]);
//
//        // load multi diff sets
//        System.out.println("Loading multi diff sets...");
//        Map<BitSet, Integer> prevDsMap = baseDsMap;
//        List<List<BitSet>> multiRemovedDs = new ArrayList<>();
//        for (int i = 4; i >= 0; i--) {
//            Map<BitSet, Integer> currDsMap = DataLoader.readYyaDiffSets(MULTI_DS_IN[i]);
//            multiRemovedDs.add(prevDsMap.keySet().stream()
//                    .filter(ds -> !currDsMap.containsKey(ds)).collect(Collectors.toList()));
//            prevDsMap = currDsMap;
//        }
//
//        // run BLMMCS REMOVE
//        System.out.println("start removing...");
//        long startTime = System.nanoTime();
//        for (int i = 4; i >= 0; i--) {
//            blmmcsfd.processRemovedSubsets(multiRemovedDs.get(4 - i));
//            printFDs(blmmcsfd, MULTI_FD_OUT[i]);      // TODO: should remove during benchmark
//        }
//        long endTime = System.nanoTime();
//        System.out.println("total runtime of REMOVE: " + (endTime - startTime) / 1000000 + "ms");
//    }
//
//    public static void testRemove(BLMMCSFD blmmcsfd) {
//        Map<BitSet, Integer> diffSetsMap = DataLoader.readYyaDiffSets(DS_IN_FULL[dataset]);
//        List<BitSet> diffSets = new ArrayList<>(diffSetsMap.keySet()); // diff sets on all attributes
//
//        System.out.println("initiating BLMMCS...");
//        long startTime1 = System.nanoTime();
//        blmmcsfd.initiate(diffSets);
//        long endTime1 = System.nanoTime();
//        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");
//
//        printFDs(blmmcsfd, FD_OUT_FULL[dataset]);
//
//
//        Map<BitSet, Integer> currDiffSetsMap = DataLoader.readYyaDiffSets(DS_IN_PART[dataset]);
//        List<BitSet> removedDiffSets = diffSetsMap.keySet().stream()
//                .filter(ds -> !currDiffSetsMap.containsKey(ds)).collect(Collectors.toList());
//
//        System.out.println("start removing...");
//        long startTime = System.nanoTime();
//        blmmcsfd.processRemovedSubsets(removedDiffSets);
//        long endTime = System.nanoTime();
//        System.out.println("total runtime of REMOVE: " + (endTime - startTime) / 1000000 + "ms");
//
//        printFDs(blmmcsfd, FD_OUT_PART[dataset]);
//    }
//
//    public static void testAdd(BLMMCSFD blmmcsfd) {
//        Map<BitSet, Integer> diffSetsMap = DataLoader.readYyaDiffSets(DS_IN_PART[dataset]);
//        List<BitSet> diffSets = new ArrayList<>(diffSetsMap.keySet()); // diff sets on all attributes
//
//        System.out.println("initiating BLMMCS...");
//        long startTime1 = System.nanoTime();
//        blmmcsfd.initiate(diffSets);
//        long endTime1 = System.nanoTime();
//        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");
//
//        printFDs(blmmcsfd, FD_OUT_PART[dataset]);
//
//
//        Map<BitSet, Integer> currDiffSetsMap = DataLoader.readYyaDiffSets(DS_IN_FULL[dataset]);
//        List<BitSet> addedDiffSets = currDiffSetsMap.keySet().stream()
//                .filter(ds -> !diffSetsMap.containsKey(ds)).collect(Collectors.toList());
//
//        System.out.println("start adding...");
//        long startTime = System.nanoTime();
//        blmmcsfd.processAddedSubsets(addedDiffSets);
//        long endTime = System.nanoTime();
//        System.out.println("total runtime of ADD: " + (endTime - startTime) / 1000000 + "ms");
//
//        printFDs(blmmcsfd, FD_OUT_FULL[dataset]);
//    }
//

    public static void printFDs(FdConnector fdConnector, String writeFilePath) {
        List<List<BitSet>> fd = fdConnector.getMinFDs();
        for (int i = 0; i < fd.size(); i++) {
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(writeFilePath, true));
                pw.println("FDs for attr " + i);
                fd.get(i).forEach(pw::println);
                pw.println();
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
