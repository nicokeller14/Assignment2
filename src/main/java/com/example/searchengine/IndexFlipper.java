package com.example.searchengine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName) {
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();

            // Map to store words and corresponding URLs
            Map<String, Set<String>> wordToURLMap = new HashMap<>();

            for (String[] csvLine : csvLines) {
                String url = csvLine[0];
                for (int i = 1; i < csvLine.length; i++) {
                    wordToURLMap.computeIfAbsent(csvLine[i], k -> new HashSet<>()).add(url);
                }
            }

            Set<String[]> lines = new HashSet<>();

            for (Map.Entry<String, Set<String>> entry : wordToURLMap.entrySet()) {
                String word = entry.getKey();
                Set<String> urls = entry.getValue();

                String[] line = new String[urls.size() + 1];
                line[0] = word;
                int idx = 1;
                for (String url : urls) {
                    line[idx++] = url;
                }

                lines.add(line);
            }

            CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName), ',', CSVWriter.NO_QUOTE_CHARACTER, ' ', "\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
