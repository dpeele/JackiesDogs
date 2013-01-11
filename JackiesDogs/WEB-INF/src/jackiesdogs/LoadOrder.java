package jackiesdogs;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/loadOrder")
public class LoadOrder extends HttpServlet {
	
	private OrderUtility orderUtility;	
	
	private final Logger log = Logger.getLogger(OrderSubmit.class);

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		orderUtility = (OrderUtility) applicationContext.getBean("orderUtility"); //lookup OrderUtility bean
		orderUtility.setApplicationContext(applicationContext); //set ApplicationContext for bean	
	}		
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		String orderId = ServletUtilities.getParameter(request, "orderId");
		if (orderId != null && orderId.length() > 0) {
			List<Order> orders = orderUtility.findOrders(new OrderSearchTerms(Integer.parseInt(orderId)));//there is an order number so load this order
			if (orders.size() > 0) {
				request.setAttribute("order",orders.get(1));
			} else {
				log.debug("No order returned for id: " + orderId);
			}
		} else {
			log.debug("No order id");
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/includes/order.jsp");
		dispatcher.forward(request, response);
	}
}
