package jackiesdogs;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.io.*;
import java.util.List;

import jackiesdogs.file.*;
import jackiesdogs.utility.UploadLog;
import jackiesdogs.web.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
	
	private UploadUtility uploadUtility;
	private ScrapingUtility scrapingUtility;
	
	private final Logger log = Logger.getLogger(OrderSubmit.class);

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);		
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());			
		uploadUtility = (UploadUtility) applicationContext.getBean("uploadUtility"); //lookup uploadUtility bean
		uploadUtility.setApplicationContext(applicationContext); //set ApplicationContext for bean
		scrapingUtility = (ScrapingUtility) applicationContext.getBean("scrapingUtility"); //lookup uploadUtility bean
		scrapingUtility.setApplicationContext(applicationContext); //set ApplicationContext for bean
	}	
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)	
		throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/includes/admin.jsp");
		dispatcher.forward(request, response);				
	}
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		String command = ServletUtilities.getParameter(request, "command");
		if (command != null && command.length() > 0) {
			List<UploadLog> output = null;
			if (command.equals("uploadProducts")) {
				String file = ServletUtilities.getParameter(request, "pricelist");
				if (file != null && file.length() > 0) {
					output = uploadUtility.uploadProducts(file);
				} else {
					log.error("No filename passed");
				}
			}
			if (command.equals("uploadInvoice")) {
				String file = ServletUtilities.getParameter(request, "invoice");
				if (file != null && file.length() > 0) {
					output = uploadUtility.uploadProducts(file);
				} else {
					log.error("No filename passed");
				}
			}			
			if (command.equals("scrape")) {
				String url = ServletUtilities.getParameter(request, "url");
				if (url != null && url.length() > 0) {
					output = scrapingUtility.scrapeSite(url);
				} else {
					log.error("No url passed");
				}
			}			
			PrintWriter out = response.getWriter();
			out.print("{\"uploadLogs\":"+new JSONArray(output)+"}");
		} else {
			log.debug("No command passed");
		}
	}
}

