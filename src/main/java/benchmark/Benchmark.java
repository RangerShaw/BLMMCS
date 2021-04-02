package benchmark;

import algorithm.BLMMCS.BLMMCS;
import algorithm.differenceSet.PLI;
import util.DataLoader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Benchmark {

    public static void main(String[] args) {
        // load data
        String DATA_FILE_PATH = "dataFiles\\letter.csv";
        List<List<String>> csvData = DataLoader.readCsvFile(DATA_FILE_PATH);

        // initiate pli and differenceSet
        PLI pli = new PLI(csvData);
        // pli.generatePLI();
        // List<BitSet> diffSetsAll = pli.generateDiffSets();

        //testAdd(pli.nAttributes);
        testRemove(pli.nAttributes);
    }

    public static void testRemove(int nAttributes) {
        List<BLMMCS> blmmcsList = new ArrayList<>();

        Map<BitSet, Integer> yya_Ds = DataLoader.readYyaDiffSets("dataFiles\\letterDS_yya.txt");
        List<BitSet> diffSetsAll = new ArrayList<>(yya_Ds.keySet()); // diff sets on all attributes
        for (int i = 0; i < 1; i++) {
            List<BitSet> diffSets = generateDiffSetsOnAttrI(diffSetsAll, i);
            blmmcsList.add(new BLMMCS(nAttributes, diffSets));

            System.out.println("initiating blmmcs for letter on attribute " + i + "...");
            blmmcsList.get(i).initiate();

            printMinCoverSets("dataFiles\\letterFD.txt", blmmcsList, i);
        }

        System.out.println("Start removing...");
        Map<BitSet, Integer> yyaDs_15000 = DataLoader.readYyaDiffSets("dataFiles\\letter_15000_DS_yya.txt");
        List<BitSet> removedDiffSets = yya_Ds.keySet().stream().filter(ds -> !yyaDs_15000.containsKey(ds)).collect(Collectors.toList());
        // List<BitSet> removedDiffSets = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            List<BitSet> removedDiffSetsOnI = generateDiffSetsOnAttrI(removedDiffSets, i);

            System.out.println("running blmmcs for letter_remove on attribute " + i + "...");
            long startTime = System.nanoTime();
            blmmcsList.get(i).processRemovedSubsets(removedDiffSetsOnI);
            long endTime = System.nanoTime();
            System.out.println("runtime total for letter_remove on attribute " + i + ": " + (endTime - startTime) / 1000000 + "ms");

            printMinCoverSets("dataFiles\\letter_15000_FD.txt", blmmcsList, i);
        }


    }

    public static void testAdd(int nAttributes) {
//        Set<BitSet> myDs = DataLoader.readDiffSets("dataFiles\\letterDS.txt");
//        List<BitSet> diffSetsAll = new ArrayList<>(myDs);

        List<BLMMCS> blmmcsList = new ArrayList<>();

        Map<BitSet, Integer> yyaDs_15000 = DataLoader.readYyaDiffSets("dataFiles\\letter_15000_DS_yya.txt");
        List<BitSet> diffSetsAll = new ArrayList<>(yyaDs_15000.keySet()); // diff sets on all attributes
        for (int i = 0; i < 1; i++) {
            List<BitSet> diffSets = generateDiffSetsOnAttrI(diffSetsAll, i);
            blmmcsList.add(new BLMMCS(nAttributes, diffSets));

            System.out.println("initiating blmmcs for letter_15000 on attribute " + i + "...");
            blmmcsList.get(i).initiate();

            printMinCoverSets("dataFiles\\letter_15000_FD.txt", blmmcsList, i);
        }


        Map<BitSet, Integer> yya_Ds = DataLoader.readYyaDiffSets("dataFiles\\letterDS_yya.txt");
        List<BitSet> addedDiffSets = yya_Ds.keySet().stream()
                .filter(ds -> !yyaDs_15000.containsKey(ds)).collect(Collectors.toList());
        for (int i = 0; i < 1; i++) {
            List<BitSet> newDiffSets = generateDiffSetsOnAttrI(addedDiffSets, i);

            System.out.println("running blmmcs for letter_add on attribute " + i + "...");
            long startTime = System.nanoTime();
            blmmcsList.get(i).processAddedSubsets(newDiffSets);
            long endTime = System.nanoTime();
            System.out.println("runtime for letter_add on attribute " + i + ": " + (endTime - startTime) / 1000000 + "ms");

            printMinCoverSets("dataFiles\\letterFD.txt", blmmcsList, i);
        }

    }

    public static List<BitSet> generateDiffSetsOnAttrI(List<BitSet> diffSets, int i) {
        // TODO: what if a new dsI is empty or an existing empty dsI is removed:
        //  return empty coverSets, but run blmmcs without the empty dsI
        List<BitSet> diffSetsOnI = new ArrayList<>();
        for (BitSet ds : diffSets) {
            if (ds.get(i)) {
                BitSet dsI = (BitSet) ds.clone();
                dsI.clear(i);
                diffSetsOnI.add(dsI);
            }
        }
        return diffSetsOnI;
    }

    public static void printMinCoverSets(String writeFilePath, List<BLMMCS> blmmcsList, int i) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(writeFilePath, true));
            pw.println();
            pw.println("FDs for attr " + i);
            blmmcsList.get(i).getGlobalMinCoverSets().forEach(pw::println);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
