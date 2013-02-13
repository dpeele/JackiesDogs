package jackiesdogs.utility;

import javax.servlet.http.*;

import java.util.*;
import java.text.*;

import org.apache.log4j.Logger;


public class ServletUtilities {

	private static final Logger log = Logger.getLogger(ServletUtilities.class);
	
	public static String getParameter(HttpServletRequest request, String parameterName) {
		String parameter = request.getParameter(parameterName);
		if (parameter == null) {
			return null;
		}
		return (parameter.trim()); //retrieve parameter from request and trim any whitespace
	}
	
	public static List<String> getStringParameterValues(HttpServletRequest request, String parameterName) {
		String[] parameterArray = request.getParameterValues(parameterName);
		List<String> parameterList = new ArrayList<String>();
		if (parameterArray == null) {
			return parameterList;
		}
		for (String parameter: parameterArray) {
			try {
				parameterList.add(parameter.trim());
			} catch (NumberFormatException nfe) {
				log.error("Unable to parse to int : " + parameter, nfe);
			}
		}
		return (parameterList); //retrieve list of parameters from request
	}		
	
	public static List<Integer> getParameterValues(HttpServletRequest request, String parameterName) {
		String[] parameterArray = request.getParameterValues(parameterName);
		List<Integer> parameterList = new ArrayList<Integer>();
		if (parameterArray == null) {
			return parameterList;
		}
		for (String parameter: parameterArray) {
			try {
				parameterList.add(Integer.parseInt(parameter.trim()));
			} catch (NumberFormatException nfe) {
				log.error("Unable to parse to int : " + parameter, nfe);
			}
		}
		return (parameterList); //retrieve list of parameters from request
	}	
	
	public static Date getDateFromString(String dateString) {
		if (dateString.length() == 0) {
			return null;
		}
		try {
			return (new SimpleDateFormat("MM/d/yyyy h:mm a", Locale.ENGLISH).parse(dateString)); //convert string to date
		} catch (ParseException pe) {
			log.error("Unable to parse date", pe);
			return null;
		}
	}
	
	public static Date getDateParameter(HttpServletRequest request, String parameter) {
		String dateString = request.getParameter(parameter);
		if (dateString == null || dateString.length() == 0) {
			return null;
		}
		return (getDateFromString(dateString)); //get Date from String
	}	
	

	public static Double getDoubleParameter (HttpServletRequest request, String parameter) {
		String doubleString = request.getParameter(parameter);
		if (doubleString.contains("$")) {
			doubleString = doubleString.substring(1); //strip out $
		}
		double doubleValue = 0;
		if (doubleString != null && doubleString.length() > 0) {
			try {
				doubleValue = Double.parseDouble(doubleString);
			} catch (NumberFormatException nfe) {
				log.error("Unable to format credit", nfe);
				return null;
			}
		}				
		return doubleValue;
	}	

	public static int getIntParameter (HttpServletRequest request, String parameter) {
		String intString = request.getParameter(parameter);
		int intValue = 0;
		if (intString != null && intString.length() > 0) {
			try {
				intValue = Integer.parseInt(intString);
			} catch (NumberFormatException nfe) {
				log.error("Unable to format credit.", nfe);
				return 0;
			}
		}				
		return intValue;
	}		
	
}
