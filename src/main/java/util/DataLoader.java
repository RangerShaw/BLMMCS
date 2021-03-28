package util;

import com.csvreader.CsvReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataLoader {

    public static List<List<String>> readCsvFile(String readCsvFilePath) {
        List<List<String>> content = new ArrayList<>();

        try {
            // 创建 CSV Reader 对象, 参数说明（读取的文件路径，分隔符，编码格式)
            CsvReader csvReader = new CsvReader(readCsvFilePath, ',', StandardCharsets.UTF_8);
            // 跳过表头
            csvReader.readHeaders();

            // 读取除表头外的内容
            while (csvReader.readRecord()) {
                // 读取一整行
                content.add(Arrays.stream(csvReader.getValues()).collect(Collectors.toCollection(ArrayList::new)));
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }
}
