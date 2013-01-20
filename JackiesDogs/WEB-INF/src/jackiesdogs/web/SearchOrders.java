package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.Order;
import jackiesdogs.bean.OrderSearchTerms;
import jackiesdogs.dataAccess.OrderUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/searchOrders")
public class SearchOrders extends HttpServlet {
	
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
		
		terms.setCustomerIds(ServletUtilities.getParameterValues(request, "customer")); //get list of customers to search on from request and put it into search term object
		terms.setStatusIds(ServletUtilities.getParameterValues(request, "status")); //get list of statuses to search on from request and put it into search term object
		Date startDate = ServletUtilities.getDateParameter(request, "startDate"); //get start date
		if (startDate != null) {
			terms.setStartOrderDate(startDate);
		}
		Date endDate = ServletUtilities.getDateParameter(request, "endDate"); //get end date
		if (endDate != null) {
			terms.setEndOrderDate(endDate);
		}
		String personal = ServletUtilities.getParameter(request, "personal"); //get value of personal checkbox
		String business = ServletUtilities.getParameter(request, "business"); //get value of business checkbox
		boolean businessBoolean = (business != null && business.length() > 0);
		if (personal != null && personal.length() > 0) {
			if (!businessBoolean) {
				terms.setPersonal(OrderSearchTerms.PERSONAL); //only show personal orders
			}
		} else if (businessBoolean) {
			terms.setPersonal(OrderSearchTerms.BUSINESS); //only show business orders
		}
		String delivered = ServletUtilities.getParameter(request, "delivered"); //get value of delivered checkbox
		String undelivered = ServletUtilities.getParameter(request, "undelivered"); //get value of undelivered checkbox
		boolean undeliveredBoolean = (undelivered != null && undelivered.length() > 0); 
		if (delivered != null && delivered.length() > 0) {
			if  (!undeliveredBoolean) {
				terms.setPersonal(OrderSearchTerms.PERSONAL); //only show personal orders
			}
		} else if (undeliveredBoolean) {
			terms.setPersonal(OrderSearchTerms.UNDELIVERED); //only show undelivered orders
		}				
		List<Order> orders = orderUtility.findOrders(terms);//get list of orders based on parameters
		if (orders.size() > 0) {
			request.setAttribute("orders",orders);
		} else {
			log.debug("No orders found");
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/forwards/search.jsp");
		dispatcher.forward(request, response);
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {	
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/forwards/search.jsp");
		dispatcher.forward(request, response);
	}
}
