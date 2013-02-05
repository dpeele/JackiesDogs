package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.json.*;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.Customer;
import jackiesdogs.dataAccess.CustomerUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/customerLookup")
public class CustomerLookup extends HttpServlet {
	
	private CustomerUtility customerUtility;

	
	private final Logger log = Logger.getLogger(CustomerLookup.class);
	
	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		customerUtility = (CustomerUtility) applicationContext.getBean("customerUtility"); //lookup CustomerUtility bean
	}	
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		int maxRowsInt = 12; // default maximum number of rows returned
		String maxRows = ServletUtilities.getParameter(request, "maxRows"); //retrieve maxRows parameter from request
		try {
			maxRowsInt = Integer.parseInt(maxRows); //try to convert maxRows parameter from request to int
		} catch (NumberFormatException nfe) { //parameter was missing or not an int
			log.error ("Unable to parse maxRows to int: ", nfe);
			nfe.printStackTrace();
		}
		
		String match = ServletUtilities.getParameter(request, "match"); //retrieve substring to match from request
		if (match.length() == 0) {
			log.error("No substring to match passed in request");
			return; //no substring to match
		}
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",-1);
		response.setHeader("Content-Type","application/json");
		PrintWriter out = response.getWriter();

		List<Customer> customers = customerUtility.findCustomers(null, match, maxRowsInt); //retrieve list of matching customers
		if (customers.size() == 0) {
			out.print("{[]}");
		} else {
			JSONArray customerJSON = new JSONArray(customers, false); //convert array to JSON
			out.print("{\"customers\":"+customerJSON+"}"); //send back JSON string
		}
	}
}
