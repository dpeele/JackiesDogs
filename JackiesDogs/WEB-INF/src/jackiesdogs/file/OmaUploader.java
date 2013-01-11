package jackiesdogs.file;

import jackiesdogs.utility.*;

import java.util.*;
import java.sql.*;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import org.apache.log4j.Logger;

public class OmaUploader implements UploadUtility{

	private ApplicationContext applicationContext;
	private ProductUtility productUtility;
	private ExcelExtractorUtility excelExtractorUtility;
	private PdfExtractorUtility pdfExtractorUtility;
	private final Logger log = Logger.getLogger(OmaProductExtractor.class);
	
	public void setApplicationContext (ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		productUtility = (ProductUtility) applicationContext.getBean("productUtility"); //lookup ProductUtility bean
		productUtility.setApplicationContext(applicationContext); //set ApplicationContext for bean
		pdfExtractorUtility = (PdfExtractorUtility) applicationContext.getBean("pdfExtractorUtility"); //lookup utility to extract data from file
		excelExtractorUtility = (ExcelExtractorUtility) applicationContext.getBean("excelExtractorUtility"); //lookup utility to extract data from file
	}
	
	/** upload product data to database from file and return report
	 * 
	 */
	public String uploadProducts(String file) { //upload products to database
		
		String line;
		List<String> dataHolder = pdfExtractorUtility.extractOrder(file); //extract data from given file
		List<Inventory> insertionErrors = new ArrayList<Inventory>();
		for (int i=0; i<dataHolder.size();i++) { //for each row in sheet
        	line = dataHolder.get(i); //get current row
    	}	    	
		return new String(); //this should be report of orphaned products/inventory
    }
	
	public String uploadOrder(String file) { //upload products to database
		
		String line;
		List<Product> insertionErrors = new ArrayList<Product>();
		for (int i=0; i<dataHolder.size();i++) { //for each row in sheet
        	line = dataHolder.get(i); //get current row
		}        	
		return new String(); //this should be report of orphaned products/inventory
    }
	

    public void printCellData(List<List<String>> dataHolder) { //test function to print data to standard output
        String name = null; //name of current item
    	String previousName = null; // name of previous item
    	String cell = null; //string to hold cell value
        int size; //number of cells in row
        int index; //index of start of white space and extra text
        String line = null; //line of output for each row
        String msrp = null; //value of msrp for row
        
    	for (int i=0; i<dataHolder.size();i++) { //for each row in sheet
        	List<String> row = dataHolder.get(i); //get current row
        	size = row.size(); //number of cells
        	line = "";
        	if (size>8) { //if row has at least 8 cells
        		msrp = row.get(7); //get msrp column
        		if (msrp != null && (AdminUtilities.isNumeric(msrp.trim()) || msrp.trim().equals("mkt"))) { //check to see if this is a row with an item in it, otherwise skip it
        			name = row.get(0); //get name from 1st cell
        			index = name.indexOf("          "); //index of extra white space and text
        			if (index > -1) { //if there is extra white space and text
        				name = name.substring(0,index); //strip extra white space and text from name
        			} else {
        				index = name.indexOf("       ("); //index of extra white space and parenthesis
        				if (index > -1) { //if there is extra white space and parenthesis
        					name = name.substring(0,index); //strip extra white space and parenthesis from name
        				}
        			}
        			if (name.length() == 0) { //if name isn't there, this is a duplicate item of the previous row in case quantity 
        				name = previousName;            		
        			}
        			line = AdminUtilities.toProperCase(name) + ", ";
        			for (int j=1; j<row.size(); j++) { //for each cell in row
        				cell = row.get(j);
        				if (j == 4) {
        					try {
        						cell = Integer.toString((int)Double.parseDouble(cell)); //if this has an extraneous .0 because of import from excel sheet, remove
        					} catch(NumberFormatException nfe) {}
        				}
        				if (cell != null && cell.length() > 0 && !cell.trim().equals("^ ^") && !cell.trim().equals("####")) {
        					line = line + cell.trim() + ", ";
        				}
        			}
        			System.out.println(line.substring(0,line.length()-2));
        			previousName = name; 
        		}
        	}
        }
    }

}

