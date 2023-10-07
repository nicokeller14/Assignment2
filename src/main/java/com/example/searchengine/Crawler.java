package com.example.searchengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Component
public abstract class Crawler {

     final String indexFileName;

    private String baseUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/";

    /**
     *
     * @param indexFileName the name of the file that is used as index.
     */
    protected Crawler(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    /**
     *
     * @param url the url where the crawling starts
     */
    public abstract void crawl(String url);

    public List<List<String>> getInfo(String urlString) {
        List<String> words = new ArrayList<>();
        List<String> hyperlinks = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(urlString).get();

            // Split the entire text content of the document into words
            String[] allWords = doc.body().text().split("\\s+");
            for (int i = 0; i < Math.min(allWords.length, 3); i++) {
                words.add(allWords[i]);
            }

            Elements hyperlinkElements = doc.select("a[href]");
            for (Element hyperlinkElement : hyperlinkElements) {
                hyperlinks.add(baseUrl + hyperlinkElement.attr("href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        returnList.add(words);
        returnList.add(hyperlinks);
        return returnList;
    }
}
