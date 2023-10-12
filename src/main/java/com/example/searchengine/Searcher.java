package com.example.searchengine;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class Searcher {

    private static final String URL_PREFIX = "https://api.interactions.ics.unisg.ch/hypermedia-environment";

    /**
     *
     * @param keyword to search
     * @param flippedIndexFileName the file where the search is performed.
     * @return the list of urls
     */
    public List<String> search(String keyword, String flippedIndexFileName){
        long startTime = System.currentTimeMillis();
        List<String> urls = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(flippedIndexFileName))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equalsIgnoreCase(keyword)) {
                    for (int i = 1; i < nextLine.length; i++) {
                        // Prepending the prefix to each URL before adding it to the list
                        urls.add(URL_PREFIX + nextLine[i]);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("duration searcher flipped: "+duration);
        return urls;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Search Engine!");
        System.out.print("Enter the keyword you'd like to search for: ");
        String keyword = scanner.nextLine();

        String flippedIndexFileName = "src/main/resources/index_flipped.csv";

        Searcher searcher = new Searcher();
        List<String> urls = searcher.search(keyword, flippedIndexFileName);

        if (urls.isEmpty()) {
            System.out.println("No results found for the keyword: " + keyword);
        } else {
            System.out.println("URLs containing the keyword \"" + keyword + "\":");
            for (String url : urls) {
                System.out.println(url); // URLs will be printed with the prefix already attached
            }
        }
    }
}
