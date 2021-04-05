package util;

import com.csvreader.CsvReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataLoader {

    public static List<List<String>> readCsvFile(String readCsvFilePath) {
        List<List<String>> content = new ArrayList<>();

        try {
            CsvReader csvReader = new CsvReader(readCsvFilePath, ',', StandardCharsets.UTF_8);

            csvReader.readHeaders();    // 跳过表头
            while (csvReader.readRecord()) {
                content.add(new ArrayList<>(Arrays.asList(csvReader.getValues())));
            }

            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public static Map<BitSet, Integer> readYyaDiffSets(String readDiffSetsFilePath) {
        File file = new File(readDiffSetsFilePath);
        HashMap<BitSet, Integer> differenceSets = new HashMap<>();
        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个Bufferedreader读取文件
            String s = null;
            while ((s = br.readLine()) != null) {
                result.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String s : result) {
            int index = s.indexOf('}');
            BitSet bitSet = new BitSet();
            String[] newS = s.substring(1, index).split(", ");
            for (String str : newS) {
                if (str != null && str.length() > 0)
                    bitSet.set(Integer.parseInt(str));
            }
            differenceSets.put(bitSet, Integer.parseInt(s.substring(index + 2)));
        }

        return differenceSets;
    }

    public static Set<BitSet> readDiffSets(String readDiffSetsFilePath) {
        File file = new File(readDiffSetsFilePath);
        Set<BitSet> differenceSets = new HashSet<>();

        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个Bufferedreader读取文件
            String s = null;
            while ((s = br.readLine()) != null) {
                result.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String s : result) {
            int index = s.indexOf('}');
            BitSet bitSet = new BitSet();
            String[] newS = s.substring(1, index).split(", ");
            for (String str : newS) {
                if (str != null && str.length() > 0)
                    bitSet.set(Integer.parseInt(str));
            }
            differenceSets.add(bitSet);
        }

        return differenceSets;
    }

}
