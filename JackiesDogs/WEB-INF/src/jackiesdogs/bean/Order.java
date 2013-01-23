package jackiesdogs.bean;

import java.text.SimpleDateFormat;
import java.util.*;

public class Order {
	private Customer customer;
	private List<OrderItem> orderItems;
	private Date orderDate, deliveryDateTime; 
	private String id, deliveryAddress, deliveryPhone, status, notes;
	private double credit, deliveryFee, tollExpense, totalCost, changeDue, totalWeight;
	private boolean delivered, personal;
	private int discount;
	
	public static Map<String,Integer> STATUS = new HashMap<String,Integer>();	

	static {
		STATUS.put("Pending", 1); // assign values for category lookup
		STATUS.put("Open", 2);		
		STATUS.put("Paid (Cash)", 3);
		STATUS.put("Paid (Credit Card)", 4);
		STATUS.put("Paid (Check)", 5);
		STATUS.put("Paid (Paypal)", 6);
		STATUS.put("Cancelled", 7);		
	}	
	
	public Order(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public Order(Date orderDate, Date deliveryDateTime) {
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
	}

	public Order(String id, Date orderDate, Date deliveryDateTime) {
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
		this.id = id;
	}

	public Order(String id, Date orderDate) {
		this.orderDate = orderDate;
		this.id = id;
	}
	
	public Order(String id, String status) {
		this.id = id;
		this.status = status;
	}	

	public Order(String id, Customer customer, List<OrderItem> orderItems,
			Date orderDate, Date deliveryDateTime, String deliveryAddress,
			String deliveryPhone, String status, String notes,
			int discount, double credit, double deliveryFee,
			double tollExpense, double totalCost, double totalWeight, double changeDue,
			boolean delivered, boolean personal) {
		this.id = id;
		this.customer = customer;
		this.orderItems = orderItems;
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
		this.deliveryAddress = deliveryAddress;
		this.deliveryPhone = deliveryPhone;
		this.status = status;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.totalWeight = totalWeight;			
		this.changeDue = changeDue;
		this.delivered = delivered;
		this.personal = personal;
	}
	
	public Order(String id, Date orderDate, Date deliveryDateTime,
			String deliveryAddress, String deliveryPhone, String status,
			String notes, int discount, double credit, double deliveryFee,
			double tollExpense, double totalCost, double totalWeight, double changeDue,
			boolean delivered, boolean personal) {
		this.id = id;
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
		this.deliveryAddress = deliveryAddress;
		this.deliveryPhone = deliveryPhone;
		this.status = status;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.totalWeight = totalWeight;		
		this.changeDue = changeDue;
		this.delivered = delivered;
		this.personal = personal;		
	}
	
	public Order(Date orderDate, Date deliveryDateTime, String deliveryAddress,
			String deliveryPhone, String status, String notes,
			int discount, double credit, double deliveryFee,
			double tollExpense, double totalCost, double totalWeight, double changeDue,
			boolean delivered, boolean personal) {
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
		this.deliveryAddress = deliveryAddress;
		this.deliveryPhone = deliveryPhone;
		this.status = status;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.totalWeight = totalWeight;		
		this.changeDue = changeDue;
		this.delivered = delivered;
		this.personal = personal;				
	}
	
	public Order(Customer customer, List<OrderItem> orderItems, Date orderDate,
			Date deliveryDateTime, String deliveryAddress,
			String deliveryPhone, String status, String notes,
			int discount, double credit, double deliveryFee,
			double tollExpense, double totalCost, double changeDue,
			boolean delivered) {
		this.customer = customer;
		this.orderItems = orderItems;
		this.orderDate = orderDate;
		this.deliveryDateTime = deliveryDateTime;
		this.deliveryAddress = deliveryAddress;
		this.deliveryPhone = deliveryPhone;
		this.status = status;
		this.notes = notes;
		this.discount = discount;
		this.credit = credit;
		this.deliveryFee = deliveryFee;
		this.tollExpense = tollExpense;
		this.totalCost = totalCost;
		this.changeDue = changeDue;
		this.delivered = delivered;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public boolean isPersonal() {
		return personal;
	}

	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
	}	

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getDeliveryDateTime() {
		return deliveryDateTime;
	}

	public void setDeliveryDateTime(Date deliveryDateTime) {
		this.deliveryDateTime = deliveryDateTime;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
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

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
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

	public double getChangeDue() {
		return changeDue;
	}

	public void setChangeDue(double changeDue) {
		this.changeDue = changeDue;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}
	
	public String getDeliveryDateTimeFormatted() {
		return new SimpleDateFormat("MM/d/yyyy h:mm a", Locale.ENGLISH).format(deliveryDateTime);
	}
	
	public String getOrderDateFormatted() {
		return new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH).format(orderDate);
	}	
}
