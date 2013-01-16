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
	
	private final String fileDirectory = "files";

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
		Part filePart;
		String fileName;
		String command = ServletUtilities.getParameter(request, "command");
		if (command != null && command.length() > 0) {
			List<UploadLog> output = null;
			if (command.equals("pricelist")) {
				filePart = request.getPart("pricelistInput");
				fileName = saveFile(filePart);
				if (fileName != null) {
					output = uploadUtility.uploadProducts(fileName);
				} else {
					log.error("Unable to write file to local server.");
				}
			}
			if (command.equals("invoice")) {
				filePart = request.getPart("invoiceInput");
				fileName = saveFile(filePart);
				if (fileName != null) {
					output = uploadUtility.uploadInvoice(fileName);
				} else {
					log.error("Unable to write file to local server.");
				}
			}			
			if (command.equals("scrape")) {
				String url = ServletUtilities.getParameter(request, "scrapeInput");
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
	
	//write file from form part to local server directory with same filename 1024 bytes at a time
	private String saveFile (Part filePart) throws IOException {
		String fileName = getFileName(filePart);
		if (fileName == null) {
			return null;
		}
		fileName = fileDirectory + File.pathSeparator + fileName;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(new File(fileName)); //output stream to local copy of file
			inputStream = filePart.getInputStream(); //get input stream for uploaded file
			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				fileOutputStream.write(bytes,0,read);
			}
		} catch (FileNotFoundException fnfe) {
			log.error("Unable to find file.");
			return null;
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return fileName;
	}
	
	//get file name from content disposition header of form part containing file
	private String getFileName (Part filePart) {
		String partHeader = filePart.getHeader("content-disposition");
		for (String content: partHeader.split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf("=")+1).replace("\"", "");
			}
		}
		return null;
	}
	
}

