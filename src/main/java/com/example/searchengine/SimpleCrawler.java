package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleCrawler extends Crawler {
    protected SimpleCrawler(String indexFileName) {
        super(indexFileName);
    }

    public void crawl(String startUrl) {
        try {
            long startTime = System.currentTimeMillis();
            Set<String[]> lines = explore(startUrl, new HashSet<>(), new HashSet<>());
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, "\r");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Duration simple crawler: " + duration/1000 + " seconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String[]> explore(String startUrl, Set<String[]> lines, Set<String> visited) {
        if (visited.contains(startUrl)) {
            return lines;
        }

        visited.add(startUrl);
        List<List<String>> info = getInfo(startUrl);
        List<String> words = info.get(0);
        List<String> hyperlinks = info.get(1);

        String[] row = new String[4];
        row[0] = startUrl.substring(startUrl.lastIndexOf("/")); // Get the shortened link of the site

        for (int i = 0; i < Math.min(words.size(), 3); i++) {
            row[i + 1] = words.get(i);  // Get the 3 words
        }
        lines.add(row);

        for (String hyperlink : hyperlinks) {
            explore(hyperlink, lines, visited);
        }
        return lines;
    }


    public static void main(String[] args){
        SimpleCrawler simpleCrawler = new SimpleCrawler("./src/main/resources/index.csv");
        simpleCrawler.crawl("https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0");
    }
}