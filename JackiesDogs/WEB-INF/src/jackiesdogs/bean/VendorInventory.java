package jackiesdogs.bean;

public class VendorInventory {
	private String id, notes;
	private Product product;
	private int quantity;
	private double totalWeight, cost;
	private boolean removed, estimate;	
	
	public VendorInventory(String id, Product product, int quantity,
			double totalWeight, double cost, String notes) {
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.totalWeight = totalWeight;
		this.cost = cost;
		this.notes = notes;
	}
	
	public VendorInventory(String id, int quantity,
			double totalWeight, double cost, String notes, boolean estimate) {
		this.id = id;		
		this.quantity = quantity;
		this.totalWeight = totalWeight;
		this.cost = cost;
		this.notes = notes;
		this.estimate = estimate;
	}
	
	public VendorInventory(Product product, int quantity,
			double totalWeight, double cost) {
		this.product = product;
		this.quantity = quantity;
		this.totalWeight = totalWeight;
		this.cost = cost;
	}	
	
	public VendorInventory(Product product, int quantity,
			double totalWeight, double cost, boolean estimate) {
		this.product = product;
		this.quantity = quantity;
		this.totalWeight = totalWeight;
		this.cost = cost;
		this.estimate = estimate;
	}	

	public VendorInventory(Product product, int quantity,
			double totalWeight) {
		this.product = product;
		this.quantity = quantity;
		this.totalWeight = totalWeight;
	}
	
	public boolean isEstimate() {
		return estimate;
	}

	public void setEstimate(boolean estimate) {
		this.estimate = estimate;
	}
	
	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public double getCost() {
		return cost;
	}
	
	public String getCostFormatted() {
		return String.format("$%.2f",cost);
	}	

	public void setCost(double cost) {
		this.cost = cost;
	}
}
