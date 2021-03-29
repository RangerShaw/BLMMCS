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
        String DATA_FILE_PATH = "dataFiles\\letter_15000.csv";
        List<List<String>> csvData = DataLoader.readCsvFile(DATA_FILE_PATH);

        // initiate pli and differenceSet
        PLI pli = new PLI(csvData);
        pli.generatePLI();
        //List<BitSet> diffSetsAll = pli.generateDiffSets();


/*
        Map<BitSet,Integer> yyaDs = DataLoader.readYyaDiffSets("dataFiles\\letterDS_yya.txt");
        Set<BitSet> myDs = DataLoader.readDiffSets("dataFiles\\letterDS.txt");
        List<BitSet> more = new ArrayList<>();
        for(BitSet yDs : yyaDs.keySet()) {
            if(!myDs.contains(yDs)) {
                System.out.println(yDs);
                more.add(yDs);
            }
        }
        System.out.println(more.size());
        System.out.println(more.stream().min(Comparator.comparing(BitSet::cardinality)));
*/


//        Set<BitSet> myDs = DataLoader.readDiffSets("dataFiles\\letterDS.txt");
//        List<BitSet> diffSetsAll = new ArrayList<>(myDs);
        Map<BitSet, Integer> yyaDs_15000 = DataLoader.readYyaDiffSets("dataFiles\\letter_15000_DS_yya.txt");
        List<BitSet> diffSetsAll = new ArrayList<>(yyaDs_15000.keySet()); // diff sets on all attributes
        List<BLMMCS> blmmcsList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            List<BitSet> diffSets = new ArrayList<>();
            for (BitSet diffSet : diffSetsAll) {
                if (diffSet.get(i)) {
                    BitSet sb = (BitSet) diffSet.clone();
                    sb.clear(i);
                    diffSets.add(sb);
                }
            }

            System.out.println("initiating blmmcs for letter_15000 on attribute " + i + "...");
            BLMMCS blmmcs = new BLMMCS(pli.nAttributes, diffSets);
            blmmcsList.add(blmmcs);
            blmmcs.initiate();
            try {
                PrintWriter pw = new PrintWriter(new FileWriter("dataFiles\\letter_15000_FD.txt", true));
                pw.println();
                pw.println("FDs for letter_15000 on attr " + i);
                blmmcs.getGlobalMinCoverSets().stream().forEach(pw::println);
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        Map<BitSet, Integer> yya_Ds = DataLoader.readYyaDiffSets("dataFiles\\letterDS_yya.txt");
        List<BitSet> addedDiffSets = yya_Ds.keySet().stream()
                .filter(ds -> !yyaDs_15000.containsKey(ds)).collect(Collectors.toList());
        for (int i = 0; i < 1; i++) {
            System.out.println("running blmmcs for letter_add on attribute " + i + "...");
            blmmcsList.get(i).processAddedSubsets(addedDiffSets);
            try {
                PrintWriter pw = new PrintWriter(new FileWriter("dataFiles\\letterFD.txt", true));
                pw.println();
                pw.println("FDs for attr " + i);
                blmmcsList.get(i).getGlobalMinCoverSets().stream().forEach(pw::println);
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
