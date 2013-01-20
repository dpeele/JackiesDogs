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

@WebServlet("/searchVendorOrders")
public class SearchVendorOrders extends HttpServlet {
	
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
	public void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		OrderSearchTerms terms = new OrderSearchTerms();
		terms.setStatusIds(ServletUtilities.getParameterValues(request, "vendor")); //get list of vendors to search on from request and put it into search term object
		terms.setStatusIds(ServletUtilities.getParameterValues(request, "vendorStatus")); //get list of statuses to search on from request and put it into search term object
		Date startDate = ServletUtilities.getDateParameter(request, "vendorStartDate"); //get start date
		if (startDate != null) {
			terms.setStartOrderDate(startDate);
		}
		Date endDate = ServletUtilities.getDateParameter(request, "vendorEndDate"); //get end date
		if (endDate != null) {
			terms.setEndOrderDate(endDate);
		}
		List<VendorOrder> orders = orderUtility.findVendorOrders(terms);//get list of orders based on parameters
		if (orders.size() > 0) {
			request.setAttribute("orders",orders);
		} else {
			log.debug("No orders found");
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/forwards/vendorSearch.jsp");
		dispatcher.forward(request, response);
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {	
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/forwards/vendorSearch.jsp");
		dispatcher.forward(request, response);
	}
}
