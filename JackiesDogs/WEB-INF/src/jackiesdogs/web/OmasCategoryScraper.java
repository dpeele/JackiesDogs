package jackiesdogs.web;

import java.util.concurrent.Callable;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class OmasCategoryScraper implements Callable<List<String>> {

	private final String urlString;
	
	private final String mainPageDivId = "vmMainPage";
	
	private final String errorMessage = "This Category is currently empty.";
	
	private final String mainUrl; 
	
	private final Logger log = Logger.getLogger(OmasCategoryScraper.class);
		
	/*constructor to pass applicationContext for database access, url to be tested, website product id, and product category*/ 
	public OmasCategoryScraper (String urlString) {
		this.urlString = urlString; //set url
		mainUrl = urlString.split("(?<!/)/(?!/)")[0]; //set main url to everything before first single forward slash (not preceded or followed by another forward slash)
	}
		
	public List<String> call () {
		List<String> urlList = new ArrayList<String>(); 
		try {
			Document doc = Jsoup.connect(urlString).get();
			if (!doc.getElementById(mainPageDivId).html().contains(errorMessage)) { //good category- contains products
				Elements anchors = doc.select(".browseProductTitle a");
				for (Element anchor: anchors) {
					urlList.add(anchor.attr("href")); //get product url for each product in category
				}
			}
		} catch (IOException ioe) {
			log.error("Jsoup error with category url: " + urlString + " with exception " + ioe);
		}
		return urlList;
	}
}
