package jackiesdogs.bean;

import java.util.*;

public class ProductGroup {
	private String url, description, websiteId, vendorName, categoryName, id;
	private List<String> imageUrls, productGroupIds;
	private List<Product> products;
	public static Map<String,Integer> VENDORS = new HashMap<String,Integer>();
	public static Map<String,Integer> CATEGORIES = new HashMap<String,Integer>();	
	
	public static final String OMAS = "Omas";

	static {
		VENDORS.put(OMAS, 1);
		
		CATEGORIES.put("Chicken", 1); // assign values for category lookup 
		CATEGORIES.put("Turkey", 2);
		CATEGORIES.put("Beef, Buffalo, and Pork", 3);
		CATEGORIES.put("Lamb", 4);
		CATEGORIES.put("Bones", 5);
		CATEGORIES.put("Fish", 6);
		CATEGORIES.put("Fruit and Vegetables", 7);
		CATEGORIES.put("Mixes", 8);
		CATEGORIES.put("Other Poultry", 9);
		CATEGORIES.put("Exotics", 10);
		CATEGORIES.put("Cat Friendly", 11);
		CATEGORIES.put("Tripe and Etc", 13);
		CATEGORIES.put("Treats and Chews", 14);
		CATEGORIES.put("Supplements", 15);
		CATEGORIES.put("Dr. Harveys", 16);		
		CATEGORIES.put("Canidae and Felidae", 17);
		CATEGORIES.put("Freeze Dried- 9Pk", 18);
		CATEGORIES.put("Freeze Dried", 19);		
		CATEGORIES.put("Smoked Treats", 20);
		CATEGORIES.put("Cat Friendly Treats", 22);		
		CATEGORIES.put("Best Sellers", 23);
		CATEGORIES.put("Freeze Dried- 2oz", 24);
		CATEGORIES.put("Freeze Dried- 4oz", 25);
		CATEGORIES.put("Freeze Dried- Bulk", 26);			
	}	
	
	public ProductGroup() {}
	
	public ProductGroup(String url, String description, String websiteId,
			String vendorName, List<String> imageUrls, List<Product> products, String categoryName) {
		this.url = url;
		this.description = description;
		this.websiteId = websiteId;
		this.vendorName = vendorName;
		this.imageUrls = imageUrls;
		this.products = products;
		this.categoryName = categoryName;
		this.productGroupIds = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getProductGroupIds() {
		return productGroupIds;
	}
	
	public void addProductGroupId(String productGroupId) {
		this.productGroupIds.add(productGroupId);
	}
	

	public void setProductGroupIds(List<String> productGroupIds) {
		this.productGroupIds = productGroupIds;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
}
