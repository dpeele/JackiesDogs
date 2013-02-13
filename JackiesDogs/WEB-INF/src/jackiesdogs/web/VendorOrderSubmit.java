package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.*;
import jackiesdogs.dataAccess.OrderUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;

@WebServlet("/submitVendorOrder")
public class VendorOrderSubmit extends HttpServlet {
	
	private OrderUtility orderUtility;	
	
	private final Logger log = Logger.getLogger(VendorOrderSubmit.class);
	
	private List<VendorInventory> retrieveVendorInventoryItems(List<String> items) {
			List<VendorInventory> orderItems = new ArrayList<VendorInventory>();
			VendorInventory orderItem;
			for (String item : items) {//each item
				if (item.length() == 0) {
					continue;
				}
				String[] fields = item.split("#"); //split item into fields		
				String id = fields[0].substring(3).trim(); //strip out label from id			
				String quantity = fields[1].substring(9).trim(); //strip out label from quantity
				String dbId = fields[2].substring(5).trim(); //strip out label from database id
				String removed = fields[3].substring(8).trim(); //strip out label from removed value
				String estimate = fields[4].substring(9).trim(); //strip out label from estimate value			
				String weight = fields[5].substring(7).trim(); //strip out label from weight value;
				orderItem = new VendorInventory(new Product(id),Integer.parseInt(quantity),Double.parseDouble(weight));
				if (!dbId.equals("0")) {
					orderItem.setId(dbId);
				}
				if (removed.trim().equals("true")) {
					orderItem.setRemoved(true);
				}
				if (estimate.equals("true")) {
					orderItem.setEstimate(true);
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
		
		String deliveryDateString = ServletUtilities.getParameter(request, "deliveryDate");			
		List<String> orderInfo = ServletUtilities.getStringParameterValues(request, "items");
		int discount = ServletUtilities.getIntParameter(request, "discount");
		int mileage = ServletUtilities.getIntParameter(request, "mileage");
		int vendor = ServletUtilities.getIntParameter(request, "vendor");		
		double credit = ServletUtilities.getDoubleParameter(request, "credit");
		double deliveryFee = ServletUtilities.getDoubleParameter(request, "deliveryFee");
		double tollExpense = ServletUtilities.getDoubleParameter(request, "tollExpense");
		String status = ServletUtilities.getParameter(request, "status");
		double totalCost = ServletUtilities.getDoubleParameter(request, "finalCost");
		double totalWeight = ServletUtilities.getDoubleParameter(request, "totalWeight");		
		List<VendorInventory> orderItems = retrieveVendorInventoryItems(orderInfo);
		List<VendorInventory> newOrderItems = new ArrayList<VendorInventory>();
		for (VendorInventory orderItem : orderItems) {
			if (orderItem.getId() == null) {
				newOrderItems.add(orderItem);
			}
		}		
		
		VendorOrder order = new VendorOrder(new Date(),ServletUtilities.getDateFromString(deliveryDateString),status,new ArrayList<String>(ProductGroup.VENDORS.keySet()).get(vendor-1),"",discount,
				credit,mileage,deliveryFee,tollExpense,totalCost,totalWeight);		
		order.setVendorInventoryItems(orderItems);
		if (orderId.equals("0")) {
			order = orderUtility.updateVendorOrder(order); //insert order and return new id
			orderId = order.getId();
		} else { // order already exists
			order.setId(orderId);
			order = orderUtility.updateVendorOrder(order); //insert order and return new id
			if (order == null) {
				log.error ("Unable to update vendor order with id: " + orderId); //error, no records updated
				return;
			}
		}
				
        Gson gson = new Gson();
        String newOrderItemsJSON = gson.toJson(newOrderItems); //convert list to JSON		
		out.print("{\"orderId\":\""+orderId+"\",\"totalCost\":\""+totalCost+"\",\"newOrderItems\":\""+newOrderItemsJSON+"\"}"); // send customer id back to front end		
	}
}
