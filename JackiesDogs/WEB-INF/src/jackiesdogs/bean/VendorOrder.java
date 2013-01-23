package jackiesdogs.bean;

import java.text.SimpleDateFormat;
import java.util.*;

public class VendorOrder {
	private String id, status, notes, vendor; 
	private Date orderDate, deliveryDate;
	private int discount, mileage;
	private double credit, deliveryFee, tollExpense, totalCost, totalWeight;
	private List<VendorInventory> vendorInventoryItems;
	
	public static Map<String,Integer> STATUS = new HashMap<String,Integer>();	
	
	static {
		STATUS.put("Requested", 1);
		STATUS.put("Received", 2);
		STATUS.put("Cancelled", 3);
	}

	public VendorOrder(String id, Date orderDate, Date deliveryDate,
			String status, String vendor, String notes, int discount,
			double credit, int mileage, double deliveryFee, double tollExpense,
			double totalCost, double totalWeight) {
		this.id = id;
		this.orderDate = orderDate;
		this.deliveryDate = deliveryDate;
		this.status = status;
		this.vendor = vendor;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.mileage = mileage;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.totalWeight = totalWeight;		
	}
	
	public VendorOrder(Date orderDate, Date deliveryDate,
			String status, String vendor, String notes, int discount,
			double credit, int mileage, double deliveryFee, double tollExpense,
			double totalCost, double totalWeight) {
		this.orderDate = orderDate;
		this.deliveryDate = deliveryDate;
		this.status = status;
		this.vendor = vendor;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.mileage = mileage;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.totalWeight = totalWeight;		
	}	

	public VendorOrder(String id, String status, Date orderDate,
			Date deliveryDate, int discount, int mileage, double credit,
			double deliveryFee, double tollExpense, double totalCost,
			String notes) {
		this.id = id;
		this.status = status;
		this.orderDate = orderDate;
		this.deliveryDate = deliveryDate;
		this.discount = discount;
		this.mileage = mileage;
		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.notes = notes;
	}
	
	public VendorOrder(double credit, double deliveryFee, double totalCost, String vendor) {

		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.totalCost = totalCost;
		this.vendor = vendor;
	}

	public VendorOrder(double totalWeight, double totalCost, String vendor, List<VendorInventory> vendorInventoryItems) {

		this.totalWeight = totalWeight;
		this.totalCost = totalCost;
		this.vendor = vendor;
		this.orderDate = new Date();
		this.vendorInventoryItems = vendorInventoryItems;
	}	
	
	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public List<VendorInventory> getVendorInventoryItems() {
		return vendorInventoryItems;
	}

	public void setVendorInventoryItems(List<VendorInventory> vendorInventoryItems) {
		this.vendorInventoryItems = vendorInventoryItems;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(double deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	public double getTollExpense() {
		return tollExpense;
	}

	public void setTollExpense(double tollExpense) {
		this.tollExpense = tollExpense;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public String getDeliveryDateFormatted() {
		return new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH).format(deliveryDate);
	}
	
	public String getOrderDateFormatted() {
		return new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH).format(orderDate);
	}	
}
