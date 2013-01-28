package jackiesdogs.file;

import jackiesdogs.bean.*;
import jackiesdogs.dataAccess.*;
import jackiesdogs.utility.*;

import java.util.*;

import org.apache.log4j.Logger;

public class OmaUploader implements UploadUtility {

	private final ProductUtility productUtility;
	private final OrderUtility orderUtility;	
	private final ExcelExtractorUtility excelExtractorUtility;
	private final PdfExtractorUtility pdfExtractorUtility;
	private final Logger log = Logger.getLogger(OmaProductExtractor.class);
	
	public OmaUploader (ProductUtility productUtility, OrderUtility orderUtility, ExcelExtractorUtility excelExtractorUtility, PdfExtractorUtility pdfExtractorUtility) { //set dependencies
		this.productUtility = productUtility;
		this.orderUtility = orderUtility;
		this.pdfExtractorUtility = pdfExtractorUtility;
		this.excelExtractorUtility = excelExtractorUtility;
	}

	/** upload inventory data from order to database from String containing file and return report
	 * 
	 */
	
	public static void main (String[] args) {
		
		OmaUploader omaUploader = new OmaUploader(null, null, new OmaProductExtractor(), null);
		omaUploader.uploadProducts("c:\\users\\dana new\\documents\\090111Omas1800.xls");
	}
	
	public List<UploadLog> uploadInvoice(String fileName) { //upload products to database
		
		String line;
		PdfExtractorUtility testPdfExtractorUtility = new OmaOrderExtractor();
		List<String> dataHolder = testPdfExtractorUtility.extractOrder(fileName); //extract data from given fileName
		int lineNum = 1;
		List<VendorInventory> inventory = new ArrayList<VendorInventory>();
		List<VendorInventory> insertionErrors = new ArrayList<VendorInventory>();
		List<UploadLog> errorLogs = new ArrayList<UploadLog>();
		VendorOrder vendorOrder;
		Product product;
		String[] splitLine;
		String vendorId;
		int quantity;
		double weight = 0;
		double price;
		double credit = 0;
		double deliveryFee = 0;
		double totalPrice = 0;
		String weightString;
		for (int i=0; i<dataHolder.size();i++) { //for each row in sheet
        	line = dataHolder.get(i).trim(); //get current row
        	splitLine = line.split("^"+lineNum+"\\s{4}");
			if (splitLine.length > 1) { //this is an item line
				lineNum++;
				if (line.contains(" 8338 ")) { //ignore line for Oma's info packet
					continue; 
				}
				
				if (line.contains(" SRVCRG ")) { //add this charge to order charges table
					splitLine = line.split("^.+?(?=\\d+\\.\\d{2}$)");
					deliveryFee = Double.parseDouble(splitLine[1]);
					continue;
				}
				line = splitLine[1]; //set line equal to everything after line number
				splitLine = line.split("(?<=\\d{2,4}\\w{0,2})\\s+",2);
				vendorId = splitLine[0]; //set vendor id to first piece of split
				line = splitLine[1]; //set line equal to rest of split
				splitLine = line.split("\\d+\\.\\d{2}\\s+",2);
				line = splitLine[1]; //remove quantity ordered
				splitLine = line.split("(?<=\\d+\\.\\d{2})\\s",2);
				quantity = (int)Double.parseDouble(splitLine[0]); //set quantity received to first piece of split
				line = splitLine[1]; //set line equal to rest of split
				splitLine = line.split("^.+?(?=\\d+\\.?\\d*\\s+-?\\d+\\.\\d{2})",2);
				line = splitLine[1]; //remove bill by label and product name along with trailing spaces
				splitLine = line.split("\\s+(?=-?\\d+\\.\\d{2}\\s+-?\\d+\\.\\d{2}$)",2);
				weightString = splitLine[0]; //number representing either quantity or exact weight
				if (weightString.contains(".")) { //this is an exact weight
					weight = Double.parseDouble(weightString);
				} else {
					weight = 0;
				}
				line = splitLine[1];
				splitLine = line.split("(?<=\\d+\\.\\d{2})\\s+",2);
				price = Double.parseDouble(splitLine[0]); //set price each
				if (price < 0) { //this is a credit, add to credits
					if (weight != 0) {
						credit = credit + (price*weight);
					} else {					
						credit = credit + (price*quantity);
					}
					continue;
				} else { //this is a billed item, add to total bill
					if (weight != 0) {
						totalPrice = totalPrice + (price*weight);
					} else {					
						totalPrice = totalPrice + (price*quantity);
					}					
				}
				product = new Product();
				product.setVendorId(vendorId);
				inventory.add(new VendorInventory(product, quantity, weight, price, false));
			}
    	}	
		String logDescription;
		List<String> headers;
		List<List<String>> logData;
		String orderId;
		vendorOrder = new VendorOrder (credit, deliveryFee, totalPrice+deliveryFee-credit, ProductGroup.OMAS);
		vendorOrder.setStatus("Received");
		if ((vendorOrder = orderUtility.updateVendorOrder(vendorOrder)) == null) {
			logDescription = "Unable to upload vendor order information.";
			headers = Arrays.asList("Credit", "Delivery Fee", "Total Cost", "Status");
			logData = new ArrayList<List<String>>();
			logData.add(Arrays.asList(String.format("$%.2f",credit), String.format("$%.2f",deliveryFee), String.format("$%.2f",totalPrice), "Received"));
			errorLogs.add(new UploadLog(logDescription, headers, logData));
		}
		orderId = vendorOrder.getId();
		for (VendorInventory vendorInventory: inventory) {
			if (productUtility.updateVendorInventoryItems(Arrays.asList(vendorInventory), Integer.parseInt(orderId), null) == null) {
				insertionErrors.add(vendorInventory);
			}
		}
		if (insertionErrors.size() > 0) {
			logDescription = "Vendor order items that were not uploaded properly.";
			headers = Arrays.asList("Vendor Id", "Quantity", "Weight", "Cost");
			logData = new ArrayList<List<String>>();
			for (VendorInventory vendorInventory : insertionErrors) {
				logData.add(Arrays.asList(vendorInventory.getProduct().getVendorId(),Double.toString(vendorInventory.getQuantity()), vendorInventory.getCostFormatted()));
			}
			errorLogs.add(new UploadLog(logDescription, headers, logData));
		}
		System.out.println (credit+", "+ deliveryFee);
		return errorLogs; //this should be report of orphaned products/inventory
    }
	
	/** upload product data to database from String containing file and return report
	 * 
	 */
	public List<UploadLog> uploadProducts(String fileName) { //upload products to database
		List<List<String>> dataHolder = excelExtractorUtility.extractProducts(fileName); //extract products
        String name = null; //name of current item
    	String previousName = null; // name of previous item
        int size; //number of cells in row
        int index; //index of start of white space and extra text
        String msrp = null; //value of msrp for row		
        String id = null; //id for item
        String billBy = null; //unit to bill by
        String orderBy = null; //unit to order by
        String estimatedWeight = null; //estimated weight of item   
        String[] estimatedRange = null; //holds the two numbers for estimatedWeight if it's a range	
        List<Product> errorProducts = new ArrayList<Product>(); //products that didn't upload
        List<Product> products = new ArrayList<Product>(); //list of products to upload      
        Product product = null;
        String defaultWeight = "0";
        String defaultCaseWeight = "0";
        String[] estimatedQuantityWeight;
        String[] nameSplit;
        String remainingName;
        String[] remainingNameSplit;
        String[] fraction;
        boolean mix = false;
        String mixString = null;
    	for (int i=0; i<dataHolder.size();i++) { //for each row in sheet
        	List<String> row = dataHolder.get(i); //get current row
        	size = row.size(); //number of cells
			name = row.get(0); //get name from 1st cell
			if (name!= null) {
				if (name.contains("Freeze Dried Treats")) {
					defaultWeight = ".25";
					defaultCaseWeight = "3.0";
				}
				if (name.contains("TEMPTINGS")) {
					defaultWeight = ".125";
					defaultCaseWeight = "0";
				}        	
				if (name.contains("Dr. Harveys Treats")) {
					defaultWeight = "0";
					defaultCaseWeight = "0";
				}
				if (name.contains("MIXES")) {
					mix = true;
				}
				if (name.contains("CHICKEN")) {
					mix = false;
				}
			}
        	if (size>10) { //if row has at least 10 cells
        		msrp = row.get(7); //get msrp column
        		if (msrp != null && (AdminUtilities.isNumeric(msrp.trim()) || msrp.trim().contains("mkt"))) { //check to see if this is a row with an item in it, otherwise skip it
        			name = name.replaceAll(new Character((char)8237).toString(), ""); //remove weird special character
        			name = name.replaceAll(" +", " ");
					index = name.toUpperCase().indexOf("(GREAT");
					if (index >= 0) {
						name = name.substring(0,index);
					}
					index = name.toUpperCase().indexOf("SPECIAL ORDER");
					if (index >= 0) {
						name = name.substring(0,index);
					}
					index = name.toUpperCase().indexOf("ORDER BY THE TUBE");
					if (index >= 0) {
						name = name.substring(0,index);
					}
					index = name.toUpperCase().indexOf("CALL FOR AVAILABILITY");
					if (index >= 0) {
						name = name.substring(0,index);
					}        	        			
        			if (name.length() == 0) { //if name isn't there, this is a duplicate item of the previous row in case quantity 
        				name = previousName;            		
        			}
        			billBy = AdminUtilities.formatUnit(row.get(6)); //replace unit abbr if necessary
        			orderBy = AdminUtilities.formatUnit(row.get(5)); //replace unit abbr if necessary       			
        			//get estimated weight if applicable
        			estimatedWeight = "0";
        				if (name.toUpperCase().contains("LB")) {
        					if (name.matches("(?i)^.*\\s+\\d+\\s+\\d+/\\d+\\s*lb.*$")) {			
        						nameSplit = name.split("(?i)\\s*lbs?\\.?"); //remove lb and any leading spaces
        						//nameSplit = name.split("\\s*lb(?!.*\\d+\\s*/\\s*\\d)"); //remove lb and any leading spaces
        						name = nameSplit[0];        						
        						estimatedWeight = name; //temporarily set estimated weight to name, we will trim off name shortly
        						name = name.split("(?i)\\s+\\d+\\s+\\d+/\\d+.*$")[0]; //split off estimated weight or weight range from rest of name
        						estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
        						estimatedWeight = estimatedWeight.replaceAll(" +"," "); //remove white space from estimated weight
        						estimatedWeight = estimatedWeight.trim();
        						estimatedRange = estimatedWeight.split(" ");
        						fraction = estimatedRange[1].split("/");        						
        					    estimatedWeight = Double.toString(Double.parseDouble(estimatedRange[0]) + Double.parseDouble(fraction[0])/Double.parseDouble(fraction[1]));    						
        					} else if (name.matches("(?i)^.*\\d+\\.?\\d*\\s*/\\s*\\d+\\.?\\d*\\s*lb.*$")) {
        						nameSplit = name.split("(?i)\\s*lbs?\\.?(?!.*\\d+\\s*/\\s*\\d\\.?\\d*)"); //remove lb and any leading spaces
        						//nameSplit = name.split("\\s*lb(?!.*\\d+\\s*/\\s*\\d)"); //remove lb and any leading spaces
        						name = nameSplit[0];
        						estimatedWeight = name; //temporarily set estimated weight to name, we will trim off name shortly
        						name = name.split("(?i)\\s+\\d+\\.?\\d*\\s*/\\s*\\d*\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name
        						estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
        						estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight
        						estimatedRange = estimatedWeight.split("/"); //split the two numbers into an array
        					    estimatedWeight = Double.toString(Double.parseDouble(estimatedRange[0]) * Double.parseDouble(estimatedRange[1])); //multiply the two numbers    						
        					} else {
        						nameSplit = name.split("(?i)\\s*lbs?\\.?"); //remove lb and any leading spaces
        						//nameSplit = name.split("\\s*lb"); //remove lb and any leading spaces
        						name = nameSplit[0];
        						estimatedWeight = name; //temporarily set estimated weight to name, we will trim off name shortly
        						name = name.split("(?i)\\s+\\d+\\.?\\d*\\s*-?\\s*\\d*\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name        						
        						estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
        						estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight 
        						if (estimatedWeight.contains("-")) { //this is a range, average the two numbers
        							estimatedRange = estimatedWeight.split("-"); //split the two numbers into an array
        							estimatedWeight = Double.toString(((Double.parseDouble(estimatedRange[0]) + Double.parseDouble(estimatedRange[1])))/2); //average the two numbers
        						}
        						if (nameSplit.length > 1) {
        							remainingName = nameSplit[1];
        						} else {
        							remainingName = "";
        						}
        						if (mix) {
        							remainingNameSplit = remainingName.trim().split(" ");        			
        							if (remainingNameSplit.length > 1 && remainingNameSplit[0].equals(remainingNameSplit[1])) { //1lb mix, ignore
        								mixString = "";
        							} else if (remainingName.contains("(") && remainingName.contains("-")) { //2lb mix with list of vegetables
        								mixString = remainingName.trim();
        								product.setProductName(product.getProductName() + "- " + AdminUtilities.toProperCase(mixString)); //set name of 1lb package appropriately
        								name = name + "- " + mixString; //set name of 2lb package        								        								
        							} else if (remainingName.contains ("BOX")) { //10 lb box
        								name = name + " " + remainingName + " " + mixString.substring(mixString.indexOf(" "));
        							} else { //5 or 10 pound mix
        								name = name + "- " + mixString;
        							}
        						} else {						
        							name = name + " " + remainingName.trim();
        						}
        					}
        				} else if (name.contains("#")) {
    						nameSplit = name.split("\\s*#"); //remove lb and any leading spaces
    						//nameSplit = name.split("\\s*lb(?!.*\\d+\\s*/\\s*\\d)"); //remove lb and any leading spaces
    						name = nameSplit[0];
    						estimatedWeight = name; //temporarily set estimated weight to name, we will trim off name shortly
    						if (name.contains("/")) {
    							name = name.split("\\s+\\d+\\.?\\d*\\s*/\\s*\\d*\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name
    							estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
    							estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight
    							estimatedRange = estimatedWeight.split("/"); //split the two numbers into an array
    							estimatedWeight = Integer.toString(Integer.parseInt(estimatedRange[0]) * Integer.parseInt(estimatedRange[1])); //multiply the two numbers
    						} else {
    							name = name.split("\\s+\\d+\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name
    							estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
    							estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight
    						}
    						if (nameSplit.length > 1) {
    							name = name + " " + nameSplit[1].trim();
    						}				    
        				}else if (name.toUpperCase().contains(" OZ")) {
    						nameSplit = name.split("(?i)\\s*oz\\.?"); //remove lb and any leading spaces
    						//nameSplit = name.split("\\s*lb(?!.*\\d+\\s*/\\s*\\d)"); //remove lb and any leading spaces
    						name = nameSplit[0];
    						estimatedWeight = name; //temporarily set estimated weight to name, we will trim off name shortly
    						if (name.matches("^.*\\s+\\d+\\.?\\d*\\s*-\\s*\\d*\\.?\\d*$")) {
    							name = name.split("\\s+\\d+\\.?\\d*\\s*-\\s*\\d*\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name
    							estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
    							estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight
    							estimatedRange = estimatedWeight.split("-"); //split the two numbers into an array
    							estimatedWeight = Double.toString(((double)(Integer.parseInt(estimatedRange[0]) + Integer.parseInt(estimatedRange[1])))/32); //average two numbers
    						} else {
    							name = name.split("\\s+\\d+\\.?\\d*$")[0]; //split off estimated weight or weight range from rest of name
    							estimatedWeight = estimatedWeight.substring(name.length()); //trim name from estimatedWeight
    							estimatedWeight = estimatedWeight.replaceAll(" ",""); //remove white space from estimated weight
    							estimatedWeight = Double.toString(Double.parseDouble(estimatedWeight)/16);
    						}    
    						if (nameSplit.length > 1) {
    							name = name + " " + nameSplit[1].trim();
    						}			    						
        				} else if (name.contains("mg. ")) {
        					nameSplit = name.split("\\s+(?=\\d+-\\d+\\s+mg)"); //remove everything before weight
        					estimatedWeight = nameSplit[1];
        					name = nameSplit[0];
        					estimatedWeight = estimatedWeight.split("\\s+mg")[0]; //remove everything after weight       					
        					estimatedQuantityWeight = estimatedWeight.split("-");
        					estimatedWeight = Double.toString(((double)(Integer.parseInt(estimatedQuantityWeight[0]) * Integer.parseInt(estimatedQuantityWeight[1])/4480))/100);		        					
        				} else if (name.contains("gm. ")) {
        					nameSplit = name.split("\\s+(?=\\d+\\s+gm)");
        					estimatedWeight = nameSplit[1]; //remove everything before weight
        					name = nameSplit[0];
        					estimatedWeight = estimatedWeight.split("\\s+gm")[0]; //remove everything after weight     					
        				} else if (!defaultWeight.equals("0") && billBy != "Case" && estimatedWeight.equals("0")) { //this is a set weight for items sold individually for the whole group of products
        					estimatedWeight = defaultWeight;
        				} else if (!defaultCaseWeight.equals("0") && billBy == "Case" && estimatedWeight.equals("0")) { //this is a set weight for items sold by the case for the whole group of products
        					estimatedWeight = defaultCaseWeight;
        				}
        			id = row.get(4);
   					try {
   						id = Integer.toString((int)Double.parseDouble(id)); //if this has an extraneous .0 because of import from excel sheet, remove
   					} catch(NumberFormatException nfe) {}
   					double msrpDouble = 0;
    				if (!msrp.contains("mkt")) {
    					msrpDouble = Double.parseDouble(msrp);
    				} 
    				try {
    					product = new Product(AdminUtilities.toProperCase(name),msrpDouble,orderBy,billBy,Double.parseDouble(estimatedWeight),"",id);
    				} catch (Exception e) {
    					System.out.println(name);
    					e.printStackTrace();
    				}
   					products.add(product);   					
        			previousName = name; 
        		}
        	}
    	}
    	for (Product uploadProduct: products) {
			if (uploadProduct.getEstimatedWeight() == 0) {
				System.out.println (uploadProduct.getProductName() + " - NO ESTIMATED WEIGHT ****************************");
			} else {
				System.out.println (uploadProduct.getProductName() + " - " + uploadProduct.getEstimatedWeight());
			}    		
    	}
    	for (Product uploadProduct: products) {
			if (productUtility.updateProduct(uploadProduct) == null) { //update product in database
				errorProducts.add(uploadProduct);
			}
    	}
		List<UploadLog> uploadLogs = productUtility.generateProductErrorReport(); //report of items that don't have uploaded info from Excel file
		if (errorProducts.size() != 0) {
			String logDescription = "Products that failed in upload to Database";
			List<String> headings = Arrays.asList("Vendor Id", "Name", "price","Order By", "Bill By", "Estimated Weight", "Vendor");
			List<List<String>> logRows = new ArrayList<List<String>>();
			for (Product errorProduct : errorProducts) {

				logRows.add(Arrays.asList(errorProduct.getVendorId(),errorProduct.getProductName(),Double.toString(errorProduct.getPrice()),errorProduct.getOrderBy(), errorProduct.getBillBy(),Double.toString(errorProduct.getEstimatedWeight()),ProductGroup.OMAS)); //add information about each product with error to log
			}
			uploadLogs.add(new UploadLog(logDescription,headings,logRows));
		}
		return uploadLogs;    
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

