package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


@RestController
public class SearchEngine {

	public final String indexFileName = "./src/main/resources/index.csv";

	public final String flippedIndexFileName = "./src/main/resources/index_flipped.csv";

	public final String startUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0";

	@Autowired
	Searcher searcher;

	@Autowired
	IndexFlipper indexFlipper;

	@Autowired
	SearchEngineProperties properties;

	Crawler crawler;

	@PostConstruct
	public void initialize(){
		if (properties.getCrawler().equals("multithread")){
			this.crawler = new MultithreadCrawler(indexFileName);
		} else {
			this.crawler = new SimpleCrawler(indexFileName);
		}
		if (properties.getCrawl()) {
			crawler.crawl(startUrl);
			indexFlipper.flipIndex(indexFileName, flippedIndexFileName);
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<String>> search(@RequestParam("q") String query) {
		// Perform the search using the 'searcher' component
		List<String> urls = searcher.search(query, flippedIndexFileName);
		return ResponseEntity.ok(urls);
	}


	@GetMapping("/lucky")
	public ResponseEntity<String> lucky(@RequestParam("q") String query) {
		// Perform the search, but only retrieve the first result
		List<String> urls = searcher.search(query, flippedIndexFileName);
		return (urls.isEmpty()) ? ResponseEntity.notFound().build() : ResponseEntity.ok(urls.get(0));
	}


	@GetMapping("/")
	public ResponseEntity<String> mainPage() {
		// Assuming you have an HTML file under resources/static/index.html
		Resource resource = new ClassPathResource("static/index.html");
		try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
			String content = FileCopyUtils.copyToString(reader);
			return ResponseEntity.ok(content);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading page");
		}

	}

}
