package jackiesdogs.utility;

public class Inventory {
	private String id, notes;
	private int quantity, specialQuantity; 
	private double cost, specialCost, actualTotalWeight;
	
	public Inventory(String id, String notes, int quantity,
			int specialQuantity, double cost, double specialCost,
			double actualTotalWeight) {
		this.id = id;
		this.notes = notes;
		this.quantity = quantity;
		this.specialQuantity = specialQuantity;
		this.cost = cost;
		this.specialCost = specialCost;
		this.actualTotalWeight = actualTotalWeight;
	}

	public Inventory(String notes, int quantity,
			int specialQuantity, double cost, double specialCost,
			double actualTotalWeight) {
		this.notes = notes;
		this.quantity = quantity;
		this.specialQuantity = specialQuantity;
		this.cost = cost;
		this.specialCost = specialCost;
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

	public int getSpecialQuantity() {
		return specialQuantity;
	}

	public void setSpecialQuantity(int specialQuantity) {
		this.specialQuantity = specialQuantity;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getSpecialCost() {
		return specialCost;
	}

	public void setSpecialCost(double specialCost) {
		this.specialCost = specialCost;
	}

	public double getActualTotalWeight() {
		return actualTotalWeight;
	}

	public void setActualTotalWeight(double actualTotalWeight) {
		this.actualTotalWeight = actualTotalWeight;
	}	
}
