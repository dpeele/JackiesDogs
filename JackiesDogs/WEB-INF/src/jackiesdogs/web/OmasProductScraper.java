package jackiesdogs.web;

import jackiesdogs.utility.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.springframework.context.ApplicationContext;

public class OmasProductScraper implements Callable<ProductGroup>{

	private final ProductUtility productUtility;	
	
	private final String websiteIdName = "product_id"; 
	
	private final String urlString;
		
	private final int category;
	
	private final Logger log = Logger.getLogger(OmasProductScraper.class);
	
	/*constructor to pass applicationContext for database access, url to be tested, website product id, and product category*/ 
	public OmasProductScraper (ApplicationContext applicationContext, String urlString, int category) {
		productUtility = (ProductUtility) applicationContext.getBean("productUtility"); //lookup ProductUtility bean
		productUtility.setApplicationContext(applicationContext); //set ApplicationContext for bean		
		this.urlString = urlString;
		this.category = category;
	}
	
	public ProductGroup call () {
		ProductGroup productGroup = null;
		try {
			Document doc = Jsoup.connect(urlString).get();
			Elements images = doc.select("table[border=0][style=width: 750px;] a"); //all images for this product- if image is http://www.omaspride.com/components/com_virtuemart/themes/default/images/noimage.gif then ignore
			List<String> imageUrls = new ArrayList<String>();
			for (Element image: images) {
				imageUrls.add(image.attr("href"));
			}
			String productIdsTd = doc.select("tr:contains(Product Number)").get(0).nextElementSibling().child(0).html(); //this is now the inner html of td element that contains the product ids for this product
			String[] productIds = productIdsTd.split("\\s*<\\s*br\\s*/\\s*>\\s*"); //put product numbers into array
			List<Product> products = new ArrayList<Product>(); //create array of products based off of 
			Product product;
			for (String productId: productIds) {
				product = new Product();
				product.setVendorId(productId);
			}
			String websiteId = doc.getElementsByAttributeValue("name", websiteIdName).val(); //get website id value
			List<String> headings = new ArrayList<String>(Arrays.asList("Ingredients","Benefits","Cat Friendly", "Suggested", "Guaranteed")); //headings list
			String description = getDescription(doc,headings); //get the description for this html document using the headings listed
			productGroup = new ProductGroup(urlString, description, websiteId, "Omas", imageUrls, products, (new ArrayList<String>(ProductGroup.CATEGORIES.keySet())).get(category-1));
			if (productUtility.updateProductGroup(productGroup) == null) {
				log.error("Unable to update for product group with website id: " + websiteId + " with url: " + urlString);
				return productGroup;
			}
		} catch (IOException ioe) {
			log.error("Jsoup error with product url: " + urlString + " with exception " + ioe);
			if (productGroup == null) {
				productGroup = new ProductGroup(urlString,"","","Omas",null,null,null);
			}
			return productGroup;
		}		
		return null;
	}
	
	private String getDescription (Document doc, List<String> headings) {
		String description = "<table>\n"; //start description html table
		String descriptionPart;
		for (String heading: headings) { //for each heading
			descriptionPart = getDescriptionPart(doc, heading); //if it exists pull out relevent html from Oma's site
			if (descriptionPart != null) { //if something was found add to description table
				description.replace("images/stories/guaranteedAnalysis", "http://www.omaspride.com/images/stories/guaranteedAnalysis");//if there is an image for guaranteed analysis, replace relative url with absolute url
				description = description + "<tr><td>" + descriptionPart + "</td></tr>\n";
			}
		}
		
		if (description.length() > 9) { //if description table isn't empty
			description = description + "</table>\n"; //close description table
			return description;
		} 
		return null; //otherwise return null
	}
	
	private String getDescriptionPart(Document doc, String heading) {
		Elements tds = doc.select("tr:contains("+heading+") td"); //these tds contains description for heading if there are any
		String description = "";
		if (tds.size() == 0) { //if nothing was found in html document, return null
			return null;
		}
		String tdHtml;
		for (Element td: tds) { //examine each td //pull inner html out of each td
			tdHtml = td.html().trim();
			if (tdHtml.length() == 0) { //if either of the td's is empty, return numm
				return null;
			}
			description = tdHtml + " ";
		}
		return  description.trim(); //return this piece of description
	}
}