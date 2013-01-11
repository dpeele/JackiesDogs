package jackiesdogs.utility;

public class OrderItem {
	private int quantity;
	private double weight;
	private String id, notes;
	private Product product;
	private boolean removed;
	
	public OrderItem(String id, Product product, int quantity, double weight,
			String notes) {
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.weight = weight;
		this.notes = notes;
	}

	public OrderItem(Product product, int quantity, double weight, String notes) {
		this.product = product;
		this.quantity = quantity;
		this.weight = weight;
		this.notes = notes;
	}

	public OrderItem(int quantity, double weight, String notes) {
		this.quantity = quantity;
		this.weight = weight;
		this.notes = notes;
	}

	public OrderItem(int quantity, Product product) {
		this.quantity = quantity;
		this.product = product;
	}

	public OrderItem(int quantity, double weight, Product product) {
		this.quantity = quantity;
		this.weight = weight;
		this.product = product;
	}

	public OrderItem(String id, int quantity, double weight, String notes) {
		this.id = id;
		this.quantity = quantity;
		this.weight = weight;
		this.notes = notes;
	}

	public OrderItem(String id, int quantity, String notes) {
		this.id = id;
		this.quantity = quantity;
		this.notes = notes;		
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
