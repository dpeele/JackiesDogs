package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.Customer;
import jackiesdogs.bean.Order;
import jackiesdogs.bean.OrderItem;
import jackiesdogs.bean.Product;
import jackiesdogs.dataAccess.OrderUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/submitOrder")
public class OrderSubmit extends HttpServlet {
	
	private OrderUtility orderUtility;	
	
	private final Logger log = Logger.getLogger(OrderSubmit.class);
	
	private List<OrderItem> retrieveOrderItems(String data) {
		String[] items = data.split(">"); //split data into order items
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		OrderItem orderItem;
		for (String item : items) {//each item
			if (item.length() == 0) {
				continue;
			}
			String[] fields = item.split("#"); //split item into fields		
			String id = fields[0].substring(3).trim(); //strip out label from id			
			String quantity = fields[1].substring(9).trim(); //strip out label from quantity
			String dbId = fields[2].substring(5).trim(); //strip out label from database id
			String removed = fields[3].substring(8).trim(); //strip out label from database removed flag
			String weight;
			int start = quantity.indexOf(" ("); //location of front parenthesis if it exists
			if (start != -1) { //front parenthesis exists and we have an exact weight for this item
				weight = quantity.substring(start+2, quantity.indexOf("lbs)")); //strip out weight
				quantity = quantity.substring(0,start); //strip out quantity
				orderItem = new OrderItem(Integer.parseInt(quantity),Double.parseDouble(weight),new Product(id));
			} else {
				orderItem = new OrderItem(Integer.parseInt(quantity),new Product(id));
			}
			if (!dbId.equals("0")) {
				orderItem.setId(dbId);
			}
			if (removed.length() > 0) {
				orderItem.setRemoved(true);
			}
			orderItems.add(orderItem);
		}
		return orderItems;
	}
	
	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);				
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		orderUtility = (OrderUtility) applicationContext.getBean("orderUtility"); //lookup OrderUtility bean
	}		
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",-1);
		response.setHeader("Content-Type","application/json");
		PrintWriter out = response.getWriter();

		
		String orderId = ServletUtilities.getParameter(request, "orderId");
		String cancelled = ServletUtilities.getParameter(request, "cancelled");
		if (cancelled != null && cancelled.length() > 0) { //this is an existing order that needs to be cancelled
			orderUtility.updateOrder(new Order(orderId,cancelled));
			out.print("{\"orderId\":\""+orderId+"\"}"); // send customer id back to front end			
			return;
		}
		
		String custId = ServletUtilities.getParameter(request, "custId");
		String deliveryDateString = ServletUtilities.getParameter(request, "deliveryDate");			
		String orderInfo = ServletUtilities.getParameter(request, "orderInfo");
		int discount = ServletUtilities.getIntParameter(request, "discount");
		double credit = ServletUtilities.getDoubleParameter(request, "credit");
		double deliveryFee = ServletUtilities.getDoubleParameter(request, "deliveryFee");
		double tollExpense = ServletUtilities.getDoubleParameter(request, "tollExpense");
		String status = ServletUtilities.getParameter(request, "status");
		double changeDue = ServletUtilities.getDoubleParameter(request, "changeDue");
		String deliveredString = ServletUtilities.getParameter(request, "delivered");
		String personalString = ServletUtilities.getParameter(request, "personal");		
		double totalCost = ServletUtilities.getDoubleParameter(request, "finalCost");
		double totalWeight = ServletUtilities.getDoubleParameter(request, "totalWeight");		
		String deliveryTimeString = ServletUtilities.getParameter(request, "deliveryTime");
		List<OrderItem> orderItems = retrieveOrderItems(orderInfo);
		
		boolean delivered = false;
		if (deliveredString.length() > 0) {
			delivered = true;
		}		
		boolean personal = false;
		if (personalString.length() > 0) {
			personal = true;
		}						
		
		Order order = new Order(new Date(),ServletUtilities.getDateFromString(deliveryDateString+" "+deliveryTimeString),"","",status,"",discount,
				credit,deliveryFee,tollExpense,totalCost,totalWeight,changeDue,delivered, personal);		
		order.setOrderItems(orderItems);
		order.setCustomer(new Customer(custId));
		if (orderId.equals("0")) {
			order = orderUtility.updateOrder(order); //insert order and return new id
			orderId = order.getId();
		} else { // order already exists
			order.setId(orderId);
			order = orderUtility.updateOrder(order); //insert order and return new id
			if (order == null) {
				log.error ("Unable to update customer order with id: " + orderId); //error, no records updated
				return;
			}
		}
				
		out.print("{\"orderId\":\""+orderId+"\",\"totalCost\":\""+totalCost+"\"}"); // send customer id back to front end		
	}
}
