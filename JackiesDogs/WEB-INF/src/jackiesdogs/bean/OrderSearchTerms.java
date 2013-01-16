package jackiesdogs.bean;

import java.util.*;

public class OrderSearchTerms {
	private List<Integer> customerIds;
	private List<Integer> statusIds;
	private Date startOrderDate, endOrderDate; 
	private String delivered, personal;
	private int id;
	
	public static final String DELIVERED = "delivered";
	public static final String UNDELIVERED = "undelivered";
	public static final String PERSONAL = "personal";
	public static final String BUSINESS = "business";
	
	public OrderSearchTerms(int id) {
		this.id = id;
	}

	public OrderSearchTerms() {
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getCustomerIds() {
		return customerIds;
	}
	
	public void setCustomerIds(List<Integer> customerIds) {
		this.customerIds = customerIds;
	}
	
	public List<Integer> getStatusIds() {
		return statusIds;
	}
	
	public void setStatusIds(List<Integer> statusIds) {
		this.statusIds = statusIds;
	}
	
	public Date getStartOrderDate() {
		return startOrderDate;
	}
	
	public void setStartOrderDate(Date startOrderDate) {
		this.startOrderDate = startOrderDate;
	}
	
	public Date getEndOrderDate() {
		return endOrderDate;
	}
	
	public void setEndOrderDate(Date endOrderDate) {
		this.endOrderDate = endOrderDate;
	}
	
	public String getDelivered() {
		return delivered;
	}
	
	public void setDelivered(String delivered) {
		this.delivered = delivered;
	}
	
	public String getPersonal() {
		return personal;
	}
	
	public void setPersonal(String personal) {
		this.personal = personal;
	}
}
