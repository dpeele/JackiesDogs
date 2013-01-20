package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.*;
import jackiesdogs.dataAccess.CustomerUtility;
import jackiesdogs.dataAccess.OrderUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/loadVendorSearch")
public class LoadVendorSearch extends HttpServlet {
	
	private OrderUtility orderUtility;
	private CustomerUtility customerUtility;	
	
	private final Logger log = Logger.getLogger(OrderSubmit.class);

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		orderUtility = (OrderUtility) applicationContext.getBean("orderUtility"); //lookup OrderUtility bean
		customerUtility = (CustomerUtility) applicationContext.getBean("customerUtility"); //lookup CustomerUtility bean		
	}		
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		int dayOffset = -21;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add( Calendar.DAY_OF_YEAR, dayOffset);
		Date startOrderDate = cal.getTime();		
		OrderSearchTerms terms = new OrderSearchTerms();
		terms.setStartOrderDate(startOrderDate);
		List<VendorOrder> orders = orderUtility.findVendorOrders(terms);//get default list of orders
		if (orders.size() > 0) {
			request.setAttribute("orders",orders);
		} else {
			log.debug("No orders found");
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/includes/vendorSearch.jsp");
		dispatcher.forward(request, response);
	}
}
