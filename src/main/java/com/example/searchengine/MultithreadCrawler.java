package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class MultithreadCrawler extends Crawler {

    private ThreadPoolTaskExecutor executorService;
    private CopyOnWriteArraySet<String> visited = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<String[]> lines = new CopyOnWriteArraySet<>();
    private ObserveRunnable observeRunnable;

    public MultithreadCrawler(String indexFileName) {
        super(indexFileName);
        executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(50);
        executorService.setMaxPoolSize(2001);
        executorService.setQueueCapacity(2001);
        executorService.initialize();
    }

    public void crawl(String startUrl) {
        long startTime = System.currentTimeMillis();

        executorService.submit(new CrawlerRunnable(this, startUrl));

        observeRunnable = new ObserveRunnable(this);
        Thread observer = new Thread(observeRunnable);
        observer.start();

        try {
            observer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writeDataToFile();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Duration multi-threaded crawler: " + duration/1000 + " seconds");

        executorService.shutdown();
    }

    private void writeDataToFile() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(super.indexFileName), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            for (String[] line : lines) {
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class CrawlerRunnable implements Runnable {
        MultithreadCrawler crawler;
        String startUrl;

        public CrawlerRunnable(MultithreadCrawler crawler, String startUrl) {
            this.crawler = crawler;
            this.startUrl = startUrl;
        }

        @Override
        public void run() {
            if (!visited.add(startUrl)) {
                return;
            }

            List<List<String>> pageData = getInfo(startUrl);
            processPageData(pageData);

            if (!pageData.isEmpty() && !pageData.get(1).isEmpty()) {
                for (String hyperlink : pageData.get(1)) {
                    try {
                        String absoluteLink = new URL(new URL(startUrl), hyperlink).toString();
                        if (!visited.contains(absoluteLink)) {
                            executorService.submit(new CrawlerRunnable(crawler, absoluteLink));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void processPageData(List<List<String>> pageData) {
            if (pageData == null || pageData.isEmpty()) {
                return;
            }

            try {
                if (!pageData.get(0).isEmpty()) {
                    String[] urlParts = startUrl.split("/");
                    String extractedPart = "/" + urlParts[urlParts.length - 1];
                    List<String> keywords = pageData.get(0).subList(0, Math.min(3, pageData.get(0).size()));
                    String[] line = new String[keywords.size() + 1];
                    line[0] = extractedPart;

                    for (int i = 0; i < keywords.size(); i++) {
                        line[i + 1] = keywords.get(i);
                    }
                    crawler.lines.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ObserveRunnable implements Runnable {
        private MultithreadCrawler crawler;

        public ObserveRunnable(MultithreadCrawler crawler) {
            this.crawler = crawler;
        }

        @Override
        public void run() {
            while (true) {
                if (executorService.getActiveCount() == 0) {
                    break;
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        MultithreadCrawler crawler = new MultithreadCrawler("./src/main/resources/index.csv");
        crawler.crawl("https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0");
    }

}


