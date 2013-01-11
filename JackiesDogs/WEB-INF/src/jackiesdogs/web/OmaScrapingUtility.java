package jackiesdogs.web;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;

import java.util.concurrent.*;

import java.util.*;

public class OmaScrapingUtility implements ScrapingUtility{
	
	private final Logger log = Logger.getLogger(OmasCategoryScraper.class);
	
	private ApplicationContext applicationContext;
	private final int THREAD_POOL_SIZE = 30; //max threads - CHANGE TO 30 ONCE IT STARTS WORKING
	
	public void setApplicationContext (ApplicationContext applicationContext) { //set the Spring application context
		this.applicationContext = applicationContext;
	}
	
	public String scrapeSite (String url) { //scrape all product information off the site 
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE); //create fixed thread pool
		String[] urlPieces = url.split("=\\d+"); //split url up
		url = urlPieces[0]+"=%d"+urlPieces[1]; //convert string into input for String.format
		Map<Integer,Future<List<String>>> categoryFutures = new HashMap<Integer,Future<List<String>>>();//map to hold category and corresponding future with return values from category scrapers
		Callable<List<String>> categoryScraper;
		Future<List<String>> categoryScraperFuture;
		for (int i=1; i<=30; i++) { //for each category
			categoryScraper = new OmasCategoryScraper(String.format(url,  i)); //create category scraper for each potential category number
			categoryScraperFuture = executorService.submit(categoryScraper); //submit category to executor service
			categoryFutures.put(i,categoryScraperFuture); //add category and future to map of categories and category scraper futures
		}
		List<String> productUrls = null;
		for (int i=1; i<=30; i++) {
			categoryScraperFuture = categoryFutures.get(i); //get future for each category
			try {
				productUrls = categoryScraperFuture.get(); //get list of urls from each future
			} catch (ExecutionException ee) {
				log.error("Unable to execute category scrape for category: " + i + " with ExecutionException: " + ee);
			} catch (InterruptedException ie) {
				log.error("Unable to execute category scrape for category: " + i + " with InterruptedException: " + ie);
			}
			for (String productUrl: productUrls) { //if there are any urls, scrape them
				Runnable request = new OmasProductScraper(applicationContext, productUrl, i); //create Runnable object to scrape site
				executorService.execute(request); //and add to ExecutorService so it can be run with a thread when available
			}			
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {} //wait until all threads are finished		
		//then go to database and retrieve list of products that weren't scraped
		return new String(); //report of items that don't have scraped info
	}
}
