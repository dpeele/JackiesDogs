package jackiesdogs.bean;

import java.util.*;

public class Product {
	private String id, productName, description, productUrl, notes, 
		 orderBy, billBy, vendorId, vendorName;
	private List<String> categories, imageUrls;
	private int estimatedWeight, websiteId;
	private double price;
	private Inventory inventory;
	
	public static Map<String,Integer> UNITS = new HashMap<String,Integer>();

	static {	
		UNITS.put("Each", 1); // assign values for unit lookup
		UNITS.put("Pound", 2);	
		UNITS.put("Case", 3);
		UNITS.put("Bag", 4);
		UNITS.put("Pouch", 5);
		UNITS.put("Tube", 6);
		UNITS.put("Package", 7);
		UNITS.put("Piece", 8);
		UNITS.put("Tub", 9);		
	}
	
	public Product(String id) {
		this.id = id;
	}	

	public Product() {
	}		
	
	public Product(String id, String productName, String description, double price, 
			String orderBy, String billBy, int estimatedWeight, String vendorId, Inventory inventory) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.inventory = inventory;
		this.vendorId = vendorId;
	}

	public Product(String id, String productName, String description, double price, 
			String billBy, int estimatedWeight, String vendorId, Inventory inventory) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 		
		this.vendorId = vendorId;		
		this.inventory = inventory;
	}	
	
	public Product(String id, String productName, String description, double price, 
					String orderBy, String billBy, int estimatedWeight, String vendorId) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.vendorId = vendorId;
	}
	
	public Product(String productName, double price, String orderBy, String billBy,  
			int estimatedWeight, String notes, String vendorId) {
		this.productName = productName;
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.notes = notes;
		this.vendorId = vendorId;
	}	

	public Product(String id, String productName, String description, double price, 
			String billBy, int estimatedWeight, String vendorId, List<String> categories) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.vendorId = vendorId;		
		this.categories = categories;
	}
	
	public Product(String id, String productName, String description, List<String> imageUrls, 
			String notes, double price, String orderBy, String billBy, int estimatedWeight, 
			List<String> categories, String vendorId) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.imageUrls = imageUrls;
		this.notes = notes;
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.categories = categories;
		this.vendorId = vendorId;
	}
	
	public Product(String id, String productName, String description, List<String> imageUrls, 
			String notes, double price, String orderBy, String billBy, int estimatedWeight, 
			List<String> categories, String vendorId, Inventory inventory) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.imageUrls = imageUrls;
		this.notes = notes;
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.categories = categories;
		this.inventory = inventory;
		this.vendorId = vendorId;		
	}	

	public Product(String id, String productName, String description, List<String> imageUrls, String productUrl,
			String notes, double price, String orderBy, String billBy, int estimatedWeight, List<String> categories, String vendorId, String vendorName) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.imageUrls = imageUrls;
		this.productUrl = productUrl;		
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.categories = categories;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
	}
	
	public Product(String id, String productName, String description, List<String> imageUrls, String productUrl,
			String notes, double price, String orderBy, String billBy, int estimatedWeight, List<String> categories, String vendorId,
			Inventory inventory) {
		this.id = id;
		this.productName = productName;
		this.description = description;
		this.imageUrls = imageUrls;		
		this.productUrl = productUrl;		
		this.price = price;
		this.orderBy = orderBy; 
		this.billBy = billBy; 
		this.estimatedWeight = estimatedWeight; 
		this.categories = categories;
		this.inventory = inventory;
		this.vendorId = vendorId;		
	}	

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public int getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(int websiteId) {
		this.websiteId = websiteId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getImageUrls() {
		return imageUrls;
	}
	
	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getBillBy() {
		return billBy;
	}

	public void setBillBy(String billBy) {
		this.billBy = billBy;
	}

	public int getEstimatedWeight() {
		return estimatedWeight;
	}

	public void setEstimatedWeight(int estimatedWeight) {
		this.estimatedWeight = estimatedWeight;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}
