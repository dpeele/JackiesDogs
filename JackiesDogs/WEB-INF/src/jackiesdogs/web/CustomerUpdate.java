package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;

import jackiesdogs.bean.Customer;
import jackiesdogs.dataAccess.CustomerUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/customerUpdate")
public class CustomerUpdate extends HttpServlet {
	
	private CustomerUtility customerUtility;
	
	private final Logger log = Logger.getLogger(CustomerUpdate.class);
	
	ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		customerUtility = (CustomerUtility) applicationContext.getBean("customerUtility"); //lookup CustomerUtility bean			
	}		
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		String lastName = ServletUtilities.getParameter(request, "lastName"); //get parameters
		String firstName = ServletUtilities.getParameter(request, "firstName");
		String streetAddress = ServletUtilities.getParameter(request, "streetAddress");
		String aptAddress = ServletUtilities.getParameter(request, "aptAddress");
		String city = ServletUtilities.getParameter(request, "city");
		String state = ServletUtilities.getParameter(request, "state");			
		String zip = ServletUtilities.getParameter(request, "zip");
		String phone = ServletUtilities.getParameter(request, "phone");
		String email = ServletUtilities.getParameter(request, "email");			
		String editCustId = ServletUtilities.getParameter(request, "editCustId");
		int intCustId = 0;
		try {
			intCustId = Integer.parseInt(editCustId); //try to convert customer id parameter from request to int
		} catch (NumberFormatException nfe) { //parameter was missing or not an int
			log.error ("Unable to parse editCustId to int: " + nfe);
			nfe.printStackTrace();
			return;
		}
		Customer customer;
		if (intCustId == 0) { //new customer
			customer = new Customer(firstName, lastName, streetAddress, aptAddress, city, state, zip,
					phone, email, ""); 
			customer = customerUtility.updateCustomer(customer); //insert customer and return new id						
		} else { // customer already exists
			customer = new Customer(editCustId, firstName, lastName, streetAddress, aptAddress, city, state, zip,
					phone, email, "");
			customer = customerUtility.updateCustomer(customer); //update customer
		}
		if (customer == null) {
			log.error ("Unable to update customer with id: " + editCustId); //error, no records updated
			return;
		}				
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",-1);
		response.setHeader("Content-Type","application/json");
		PrintWriter out = response.getWriter();
		out.print("{\"custId\":\""+customer.getId()+"\"}"); // send customer id back to front end		
	}
}
