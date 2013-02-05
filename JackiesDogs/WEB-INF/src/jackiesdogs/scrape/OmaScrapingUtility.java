package jackiesdogs.scrape;

import org.apache.log4j.Logger;

import jackiesdogs.bean.ProductGroup;
import jackiesdogs.bean.UploadLog;
import jackiesdogs.dataAccess.ProductUtility;
import jackiesdogs.utility.*;

import java.util.concurrent.*;

import java.util.*;

public class OmaScrapingUtility implements ScrapingUtility{
	
	private final Logger log = Logger.getLogger(OmasCategoryScraper.class);
	private ProductUtility productUtility;
	
	private final int THREAD_POOL_SIZE = 30; //max threads - CHANGE TO 30 ONCE IT STARTS WORKING
	
	public OmaScrapingUtility (ProductUtility productUtility) { //set the productUtility instance
		productUtility = this.productUtility;
	}
	
	public List<UploadLog> scrapeSite (String url) { //scrape all product information off the site 
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
		Callable<ProductGroup> request;
		Future<ProductGroup> productScraperFuture;
		List<Future<ProductGroup>> productFutures = new ArrayList<Future<ProductGroup>>();
		List<ProductGroup> errorProductGroups = new ArrayList<ProductGroup>();
		for (int i=1; i<=30; i++) {
			categoryScraperFuture = categoryFutures.get(i); //get future for each category
			try {
				productUrls = categoryScraperFuture.get(); //get list of urls from each future
			} catch (ExecutionException ee) {
				log.error("Unable to execute category scrape for category: " + i + " with ExecutionException: ", ee);
			} catch (InterruptedException ie) {
				log.error("Unable to execute category scrape for category: " + i + " with InterruptedException: ", ie);
			}
			for (String productUrl: productUrls) { //if there are any urls, scrape them
				request = new OmasProductScraper(productUtility, productUrl, i); //create Callable object to scrape site
				productScraperFuture = executorService.submit(request); //add to ExecutorService so it can be run with a thread when available and set future
				productFutures.add(productScraperFuture);
			}
			ProductGroup productGroup = null;
			for (Future<ProductGroup> productFuture: productFutures) { //for each Callable thread that scraped a url, get the future 
				try {
					productGroup = productFuture.get(); //get the productGroup object
				} catch (ExecutionException ee) {
					log.error("Unable to extract productGroup from product scrape for with ExecutionException: ", ee);
				} catch (InterruptedException ie) {
					log.error("Unable to extract productGroup from product scrape for with ExecutionException: ", ie);
				}
				if (productGroup != null) {
					errorProductGroups.add(productGroup);
				}
			}
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {} //wait until all threads are finished		
		//then go to database and retrieve list of products that weren't scraped
		List<UploadLog> uploadLogs = productUtility.generateProductGroupErrorReport(); //report of items that don't have scraped info
		if (errorProductGroups.size() != 0) {
			String logDescription = "Product Groups that failed in upload to Database";
			List<String> headings = Arrays.asList("Website Id", "url", "Vendor");
			List<List<String>> logRows = new ArrayList<List<String>>();
			for (ProductGroup productGroup : errorProductGroups) {
				logRows.add(Arrays.asList(productGroup.getWebsiteId(),productGroup.getUrl(),productGroup.getVendorName())); //add information about each productGroup with error to log
			}
			uploadLogs.add(new UploadLog(logDescription,headings,logRows));
		}
		return uploadLogs;
	}
}
