package benchmark;

import algorithm.BLMMCS.BLMMCSAlgo;
import algorithm.differenceSet.PLI;
import util.DataLoader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

    static int dataset = 0;


    public static void main(String[] args) {
        // load data
        List<List<String>> csvData = DataLoader.readCsvFile(CSV_IN_FULL[dataset]);

        // initiate pli and differenceSet
        PLI pli = new PLI(csvData);
        pli.generatePLI();
        // List<BitSet> diffSetsAll = pli.generateDiffSets();


        BLMMCSAlgo blmmcsAlgo = new BLMMCSAlgo(pli.nAttributes);

        testRemove(blmmcsAlgo);
        //testAdd(blmmcsAlgo);
    }

    public static void testRemove(BLMMCSAlgo blmmcsAlgo) {
        Map<BitSet, Integer> diffSetsMap = DataLoader.readYyaDiffSets(DS_IN_FULL[dataset]);
        List<BitSet> diffSets = new ArrayList<>(diffSetsMap.keySet()); // diff sets on all attributes

        System.out.println("initiating BLMMCS...");
        long startTime1 = System.nanoTime();
        blmmcsAlgo.initiate(diffSets);
        long endTime1 = System.nanoTime();
        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");

        printFDs(blmmcsAlgo, FD_OUT_FULL[dataset]);


        Map<BitSet, Integer> currDiffSetsMap = DataLoader.readYyaDiffSets(DS_IN_PART[dataset]);
        List<BitSet> removedDiffSets = diffSetsMap.keySet().stream()
                .filter(ds -> !currDiffSetsMap.containsKey(ds)).collect(Collectors.toList());

        System.out.println("start removing...");
        long startTime = System.nanoTime();
        blmmcsAlgo.processRemovedSubsets(removedDiffSets);
        long endTime = System.nanoTime();
        System.out.println("total runtime of REMOVE: " + (endTime - startTime) / 1000000 + "ms");

        printFDs(blmmcsAlgo, FD_OUT_PART[dataset]);
    }

    public static void testAdd(BLMMCSAlgo blmmcsAlgo) {
        Map<BitSet, Integer> diffSetsMap = DataLoader.readYyaDiffSets(DS_IN_PART[dataset]);
        List<BitSet> diffSets = new ArrayList<>(diffSetsMap.keySet()); // diff sets on all attributes

        System.out.println("initiating BLMMCS...");
        long startTime1 = System.nanoTime();
        blmmcsAlgo.initiate(diffSets);
        long endTime1 = System.nanoTime();
        System.out.println("initiating runtime: " + (endTime1 - startTime1) / 1000000 + "ms");

        printFDs(blmmcsAlgo, FD_OUT_PART[dataset]);


        Map<BitSet, Integer> currDiffSetsMap = DataLoader.readYyaDiffSets(DS_IN_FULL[dataset]);
        List<BitSet> addedDiffSets = currDiffSetsMap.keySet().stream()
                .filter(ds -> !diffSetsMap.containsKey(ds)).collect(Collectors.toList());

        System.out.println("start adding...");
        long startTime = System.nanoTime();
        blmmcsAlgo.processAddedSubsets(addedDiffSets);
        long endTime = System.nanoTime();
        System.out.println("total runtime of ADD: " + (endTime - startTime) / 1000000 + "ms");

        printFDs(blmmcsAlgo, FD_OUT_FULL[dataset]);
    }

    public static void printFDs(BLMMCSAlgo blmmcsAlgo, String writeFilePath) {
        List<List<BitSet>> fd = blmmcsAlgo.getMinimalCoverSets();
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
