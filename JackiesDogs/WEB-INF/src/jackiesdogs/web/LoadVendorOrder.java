package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

import jackiesdogs.bean.*;
import jackiesdogs.dataAccess.OrderUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/loadVendorOrder")
public class LoadVendorOrder extends HttpServlet {
	
	private OrderUtility orderUtility;	
	
	private final Logger log = Logger.getLogger(OrderSubmit.class);

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		orderUtility = (OrderUtility) applicationContext.getBean("orderUtility"); //lookup OrderUtility bean
	}		
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		String orderId = ServletUtilities.getParameter(request, "orderId");
		List<Integer> orderIds = ServletUtilities.getParameterValues(request, "customerOrderId"); //get list of customer order ids to create vendor order from
		String vendorTypeId = ServletUtilities.getParameter(request, "vendorTypeId");
		if (orderId != null && orderId.length() > 0) {
			List<VendorOrder> orders = orderUtility.findVendorOrders(new OrderSearchTerms(Integer.parseInt(orderId)));//there is an order number so load this order
			if (orders.size() > 0) {
				request.setAttribute("order",orders.get(1));
			} else {
				log.debug("No order returned for id: " + orderId);
			}
		} else if (orderIds != null && vendorTypeId != null) {
			VendorOrder order = orderUtility.generateVendorOrder (orderIds, Integer.parseInt(vendorTypeId));
			if (order != null) {
				request.setAttribute("order",order);
			} else {
				log.debug("No order returned for customer ids and vendor type id");
			}		
		} else {
			log.debug("No order id and no list of customer order ids and vendor type to generate order from");
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/forwards/vendorOrder.jsp");
		dispatcher.forward(request, response);
	}
}
