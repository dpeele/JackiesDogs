package jackiesdogs.bean;

public class Inventory {
	private String id, notes;
	private int quantity, reservedQuantity; 
	private double cost, reservedWeight, actualTotalWeight;
	
	public Inventory(String id, String notes, int quantity,
			int reservedQuantity, double cost, double reservedWeight,
			double actualTotalWeight) {
		this.id = id;
		this.notes = notes;
		this.quantity = quantity;
		this.reservedQuantity = reservedQuantity;
		this.cost = cost;
		this.reservedWeight = reservedWeight;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(String notes, int quantity,
			int reservedQuantity, double cost, double reservedWeight,
			double actualTotalWeight) {
		this.notes = notes;
		this.quantity = quantity;
		this.reservedQuantity = reservedQuantity;
		this.cost = cost;
		this.reservedWeight = reservedWeight;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(String id, int quantity, double cost,
			double actualTotalWeight) {
		this.id = id;
		this.quantity = quantity;
		this.cost = cost;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(String id, int quantity, double actualTotalWeight) {
		this.id = id;
		this.quantity = quantity;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(int quantity, double cost,
			double actualTotalWeight) {
		this.quantity = quantity;
		this.cost = cost;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(int quantity, double actualTotalWeight) {
		this.quantity = quantity;
		this.actualTotalWeight = actualTotalWeight;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getReservedQuantity() {
		return reservedQuantity;
	}

	public void setReservedQuantity(int reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getReservedWeight() {
		return reservedWeight;
	}

	public void setReservedWeight(double reservedWeight) {
		this.reservedWeight = reservedWeight;
	}

	public double getActualTotalWeight() {
		return actualTotalWeight;
	}

	public void setActualTotalWeight(double actualTotalWeight) {
		this.actualTotalWeight = actualTotalWeight;
	}	
}
