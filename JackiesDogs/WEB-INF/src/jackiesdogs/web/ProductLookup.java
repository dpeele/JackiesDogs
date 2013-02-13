package jackiesdogs.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import jackiesdogs.bean.Product;
import jackiesdogs.dataAccess.ProductUtility;
import jackiesdogs.utility.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;

@WebServlet("/productLookup")
public class ProductLookup extends HttpServlet {
	
	private ProductUtility productUtility;	
	
	private final Logger log = Logger.getLogger(ProductLookup.class);
	
	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		productUtility = (ProductUtility) applicationContext.getBean("productUtility"); //lookup ProductUtility bean
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
		}
		
		String match = ServletUtilities.getParameter(request, "match"); //retrieve substring to match from request

		if (match.length() == 0) {
			log.error("No substring to match passed in request");
			return; //no substring to match
		}

		String vendorTypeId = ServletUtilities.getParameter(request, "vendorType"); //retrieve optional vendor type id
		
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",-1);
		response.setHeader("Content-Type","application/json");
		PrintWriter out = response.getWriter();
		List<Product> products = productUtility.findProducts(null,match,maxRowsInt,vendorTypeId); //retrieve list of matching products		
		if (products.size() == 0) {
			out.print("{[]}");
		} else {
	        Gson gson = new Gson();
	        String productJSON = gson.toJson(products); //convert list to JSON		
			out.print("{\"products\":"+productJSON+"}"); //send back JSON string
		}
	}
}
