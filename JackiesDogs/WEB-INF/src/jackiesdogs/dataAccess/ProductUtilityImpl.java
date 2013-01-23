package jackiesdogs.dataAccess;

import jackiesdogs.bean.*;

import javax.sql.*;

import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProductUtilityImpl implements ProductUtility {
	
	private final DataSource dataSource;
	
	private final Logger log = Logger.getLogger(ProductUtilityImpl.class);
		
	private final String findProductsSql = "{CALL product_retrieve (?, ?, ?, ?)}";
	
	private final String updateProductSql = "{CALL product_update (?, ?, ?, ?, ?, ?, ?, ?)}";
	
	private final String updateInventorySql = "{CALL inventory_update (?, ?, ?, ?, ?, ?, ?, ?)}";
	
	private final String updateVendorInventorySql = "{CALL vendor_inventory_update (?, ?, ?, ?, ?, ?, ?, ?, ?)}";	
	
	private final String findVendorInventorySql = "{CALL vendor_inventory_retrieve (?)}";
	
	private final String updateProductGroupSql = "{CALL product_group_update (?, ?, ?, ?)}";
	
	private final String updateProductGroupCategorySql = "{CALL product_group_category_update (?, ?)}";
	
	private final String productReportSql = "{CALL product_report ()}";
	
	private final String productGroupReportSql = "{CALL product_group_report ()}";
	
	private final String updateProductGroupMemberSql = "{CALL product_group_member_update (?, ?)}";
	
	private final String updateProductGroupImageSql = "{CALL product_group_image_update (?, ?)}";	
	
	public ProductUtilityImpl (DataSource dataSource) {
		this.dataSource = dataSource; //set dataSource
	}
		
	public List<Product> findProducts (String id, String match, int limit, String vendorTypeId) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		Product product = null;
		String categoryString = null;
		String imageString = null;
		List<String> categories = null;
		List<String> imageUrls = null;
		List<Product> products = new ArrayList<Product>();
		boolean hasResults;
		try {
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads and nonrepeatable reads
			callableStatement = connection.prepareCall(findProductsSql); //prepare call
			if (id == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(id));
			}
			if (match == null) {
				callableStatement.setNull(2,Types.VARCHAR);
			} else {
				callableStatement.setString(2,match);
			}
			callableStatement.setInt(3, limit);			
			if (vendorTypeId == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(vendorTypeId));				
			}
			hasResults = callableStatement.execute();			
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.isBeforeFirst()) { //this returns false if ResultSet is empty
					log.debug("No products retrieved.");					
				} else {
					while (resultSet.next()) {
						categoryString = resultSet.getString("categories");
						if (categoryString != null && categoryString.contains("|")) {
							categories = new ArrayList<String>(Arrays.asList(categoryString.split("|")));
						}
						imageString = resultSet.getString("images");
						if (imageString != null && imageString.contains("|")) {
							imageUrls = new ArrayList<String>(Arrays.asList(imageString.split("|")));
						}					
						product = new Product(resultSet.getString("product_id"),
								 			  resultSet.getString("product_name"),
								 			  resultSet.getString("description"),
								 			  imageUrls,
								 			  resultSet.getString("url"),
								 			  resultSet.getString("product_notes"),
								 			  resultSet.getDouble("price"),					 
								 			  resultSet.getString("bill_by_unit_name"),
								 			  resultSet.getString("order_by_unit_name"),						 
								 			  resultSet.getInt("estimated_weight"),
								 			  categories,
								 			  resultSet.getString("vendor_id"),
								 			  resultSet.getString("vendor_name"));
						Inventory inventory = new Inventory (resultSet.getString("inventoryId"),
															 resultSet.getString("inventory_notes"),						 
															 resultSet.getInt("quantity"),						 						
															 resultSet.getInt("reserved_quantity"),
															 resultSet.getDouble("cost"),						 
															 resultSet.getDouble("reserved_weight"),	
															 resultSet.getDouble("total_weight"));	
						product.setInventory(inventory);
						products.add(product);
					}
				}
			} else {
				log.debug("No products retrieved.");
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}							
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return products;
	}	
	
	public Product updateProduct (Product product) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		Inventory inventory;
		boolean hasResults;
		String stringId;
		try {
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setAutoCommit(false); //we want an atomic transaction with possible inventory update
			callableStatement = connection.prepareCall(updateProductSql); //prepare call
			stringId = product.getId();
			if (stringId == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(stringId));
			}					
			callableStatement.setString(2, product.getProductName());
			callableStatement.setDouble(3, product.getPrice());
			callableStatement.setInt(4, Product.UNITS.get(product.getOrderBy()));
			callableStatement.setInt(5, Product.UNITS.get(product.getBillBy()));
			callableStatement.setInt(6, product.getEstimatedWeight());
			callableStatement.setString(7, product.getNotes());
			callableStatement.setString(8, product.getVendorId());							
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.isBeforeFirst()) { //this returns false if ResultSet is empty
					throw new SQLException ("Update of product failed.");			
				} else {
					resultSet.next();
					stringId = resultSet.getString(1);
					product.setId(stringId);
					inventory = product.getInventory();
					if (inventory != null) {
						inventory = updateInventoryItems(Arrays.asList(inventory), stringId, null, connection).get(0);
					} else {
						throw new SQLException ("Update of product failed.");
					}		
					connection.commit();  //commit all statements
				}
			} else {
				throw new SQLException ("Update of product failed.");
			}			
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			if (connection != null) {
				log.error ("Transaction is being rolled back.");
				try {
					connection.rollback();
				} catch (SQLException se2) {
					log.error("Unable to rollback transaction.");
				}
			}
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}			
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}								
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return product;
	}		

	public List<Inventory> updateInventoryItems (List<Inventory> inventoryItems, String productId, String vendorId, Connection previousConnection) {
		boolean closeConnection = false;
		Connection connection = previousConnection; //set connection to previous connection if this is part of a larger transaction
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		String id = null;
		boolean hasResults;
		try {
			if (connection == null) { //if we didn't pass a connection as a parameter
				closeConnection = true;
				connection = dataSource.getConnection(); //get connection from dataSource
			}
			callableStatement = connection.prepareCall(updateInventorySql); //prepare call
			for (Inventory inventory: inventoryItems) {
				id = inventory.getId();			
				if (id == null) {
					callableStatement.setNull(1, Types.INTEGER);
				} else {
					callableStatement.setInt(1, Integer.parseInt(id));
				}						
				if (productId == null) {
					callableStatement.setNull(2, Types.INTEGER);
				} else {
					callableStatement.setInt(2, Integer.parseInt(productId));
				}							
				callableStatement.setInt(3, inventory.getQuantity());			
				callableStatement.setDouble(4, inventory.getActualTotalWeight());
				callableStatement.setDouble(5, inventory.getCost());
				callableStatement.setDouble(6, inventory.getReservedQuantity());
				callableStatement.setDouble(7, inventory.getReservedWeight());
				callableStatement.setString(8, inventory.getNotes());
				if (vendorId == null) {
					callableStatement.setNull(2, Types.VARCHAR);
				} else {
					callableStatement.setString(2, vendorId);
				}			
				hasResults = callableStatement.execute();
				if (hasResults) { 
					resultSet = callableStatement.getResultSet();
					if (resultSet.next()) {
						inventory.setId(resultSet.getString(1));
					} else {
						throw new SQLException("Update of Inventory failed.");				
					}				
				} else {
					throw new SQLException("Update of Inventory failed.");				
				}
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}			
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}	
			if (closeConnection) { //if this connection was created in this method instead of passed to it, close the connection
				try {				
					connection.close();
				} catch (Exception se) {
					log.error("Unable to close connection: " + se);
					se.printStackTrace();
				}
			}
		}
		return inventoryItems;
	}		

	public List<VendorInventory> updateVendorInventoryItems (List<VendorInventory> vendorInventoryItems, int vendorOrderId, Connection previousConnection) {
		boolean closeConnection = false;
		Connection connection = previousConnection; //set connection equal to previous connection so that we can commit in one transaction
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		String id = null;
		Product product = null;
		boolean hasResults;
		try {
			if (connection == null) { //if we didn't pass a connection as a parameter
				closeConnection = true;
				connection = dataSource.getConnection(); //get connection from dataSource
			}
			callableStatement = connection.prepareCall(updateVendorInventorySql); //prepare call
			for (VendorInventory vendorInventory: vendorInventoryItems) {
				id = vendorInventory.getId();	
				product = vendorInventory.getProduct();
				if (id == null) {
					callableStatement.setNull(1, Types.INTEGER);
				} else {
					callableStatement.setInt(1, Integer.parseInt(id));
				}						
				if (vendorInventory.isRemoved()) { //remove item	
					callableStatement.setNull(2, Types.INTEGER);
					callableStatement.setNull(3, Types.INTEGER);
					callableStatement.setNull(4, Types.VARCHAR);
					callableStatement.setNull(5, Types.INTEGER);
					callableStatement.setNull(6, Types.FLOAT);
					callableStatement.setNull(7, Types.FLOAT);
					callableStatement.setNull(8, Types.VARCHAR);
					callableStatement.setByte(9, (byte)1);				
				} else {	
					callableStatement.setInt(2, vendorOrderId);
					if (product.getId() == null) {
						callableStatement.setNull(3, Types.INTEGER);
					} else {
						callableStatement.setInt(3, Integer.parseInt(product.getId()));
					}
					callableStatement.setString(4, product.getVendorId());
					callableStatement.setInt(5, vendorInventory.getQuantity());
					callableStatement.setDouble(6, vendorInventory.getTotalWeight());
					callableStatement.setDouble(7, vendorInventory.getCost());
					callableStatement.setString(8, vendorInventory.getNotes());
					callableStatement.setByte(9, (byte)0);
				}
				hasResults = callableStatement.execute();
				if (hasResults) { 
					resultSet = callableStatement.getResultSet();
					if (resultSet.next()) {
						vendorInventory.setId(resultSet.getString(1));
					} else {
						throw new SQLException("Update of vendor inventory failed.");				
					}				
				} else {
					throw new SQLException("Update of vendor inventory failed.");				
				}
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}			
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}			
			if (closeConnection) { //we weren't passed a connection so this is a connection created within this method and must be closed
				try {
					connection.close();
				} catch (Exception se) {
					log.error("Unable to close connection: " + se);
					se.printStackTrace();
				}
			}
		}
		return vendorInventoryItems;
	}		
	
	public List<VendorInventory> findVendorInventoryByOrderId (int vendorOrderId) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		List<VendorInventory> vendorInventoryItems = new ArrayList<VendorInventory>();
		boolean hasResults;
		VendorInventory vendorInventory; 
		Product product;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource			
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads, phantom reads, and nonrepeatable reads			
			callableStatement = connection.prepareCall(findVendorInventorySql); //prepare callable statement
			callableStatement.setInt(1, vendorOrderId);
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.isBeforeFirst()) { //returns false if ResultSet is empty 
					log.debug("No order items returned.");					
				} else {
					while (resultSet.next()) {
						vendorInventory = new VendorInventory(resultSet.getString("vendor_inventory_id"),
												 			resultSet.getInt("quantity"),
												 			resultSet.getDouble("total_weight"),
												 			resultSet.getDouble("cost"),
												 			resultSet.getString("notes"));										 
						product = new Product (resultSet.getString("product_id"),
													   resultSet.getString("product_name"),
													   resultSet.getString("description"),
													   resultSet.getDouble("price"),
													   resultSet.getString("vendor_id"));	
						vendorInventory.setProduct(product);
						vendorInventoryItems.add(vendorInventory);
					}
					
				}	
			} else {
				log.debug("No vendor inventory items returned");
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}							
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return vendorInventoryItems;
	}	

	
	public ProductGroup updateProductGroup (ProductGroup productGroup) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		int id;
		try {
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setAutoCommit(false); //commit all group changes at once
			callableStatement = connection.prepareCall(updateProductGroupSql); //update product group information
			callableStatement.setString(1, productGroup.getUrl());
			callableStatement.setString(2, productGroup.getDescription());
			callableStatement.setInt(3, Integer.parseInt(productGroup.getWebsiteId()));
			callableStatement.setInt(4, ProductGroup.VENDORS.get(productGroup.getVendorName()));			
			boolean hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.next()) {
					id = resultSet.getInt(1);
					productGroup.setId(Integer.toString(id));
				} else {
					throw new SQLException("Update of product group failed.");
				}
			} else {
				throw new SQLException("Update of product group failed.");
			}
			callableStatement = connection.prepareCall(updateProductGroupCategorySql); //update product group category
			callableStatement.setInt(1,id); //set product group id
			callableStatement.setInt(2,ProductGroup.CATEGORIES.get(productGroup.getCategoryName()));
			hasResults = callableStatement.execute();
			if (!hasResults) {
				throw new SQLException("Update of product group categories failed.");				
			} else {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.next()) {
					throw new SQLException("Update of product group categories failed.");
				}
			}		
			callableStatement = connection.prepareCall(updateProductGroupMemberSql); //update product group members
			callableStatement.setInt(1,id); //set product group id
			for (Product product : productGroup.getProducts()) {			
				callableStatement.setString(2,product.getVendorId());
				hasResults = callableStatement.execute();
				if (hasResults) {
					resultSet = callableStatement.getResultSet();
					if (resultSet.next()) {
						product.setId(resultSet.getString(1));
					} else {
						throw new SQLException("Update of product group members failed.");
					}
				} else {
					throw new SQLException("Update of product group members failed.");
				}				
			}
			callableStatement = connection.prepareCall(updateProductGroupImageSql); //update product group images
			callableStatement.setInt(1,id); //set product group id
			for (String imageUrl : productGroup.getImageUrls()) {			
				callableStatement.setString(2,imageUrl);
				hasResults = callableStatement.execute();
				if (!hasResults) {
					throw new SQLException("Update of product group images failed.");
				} else {
					resultSet = callableStatement.getResultSet();
					if (!resultSet.next()) {
						throw new SQLException("Update of product group images failed.");
					}
				}
			}			
			connection.commit(); //commit all statements
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			if (connection != null) {
				log.error ("Transaction is being rolled back.");
				try {
					connection.rollback();
				} catch (SQLException se2) {
					log.error("Unable to rollback transaction.");
				}
			}			
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}									
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return productGroup;
	}	
	
	private UploadLog generateUploadLog (String logDescription, List<String> headings, ResultSet resultSet) throws SQLException{
		List<String> row;
		List<List<String>> logRows = new ArrayList<List<String>>();
		int size = headings.size();
		if (resultSet.isBeforeFirst()) { // there are rows in the product report
			while (resultSet.next()) {
				row = new ArrayList<String>();
				for (int i=1; i<size; i++) {
					row.add(resultSet.getString(i)); //add string columns to log
				}
				row.add(new SimpleDateFormat("MM/d/yyyy h:mm a", Locale.ENGLISH).format(resultSet.getDate(size)));
				logRows.add(row);
			}
			return new UploadLog(logDescription, headings,logRows);
		}
		return null; //empty log resultSet
	}
	
	public List<UploadLog> generateProductErrorReport () {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		List<UploadLog> uploadLogs = new ArrayList<UploadLog>();
		UploadLog uploadLog;
		boolean hasResults;
		try {
			connection = dataSource.getConnection(); //get connection from dataSource			
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //prevent dirty reads			
			callableStatement = connection.prepareCall(productReportSql); //prepare call
			hasResults = callableStatement.execute();
			String logDescription = "Products that are out of date.";			
			List<String> headings = Arrays.asList("Id", "Name", "Vendor", "Url", "Website Id", "Vendor Id", "Last Modified Date"); //log headers
			if (hasResults) { 
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}			
			} else {
				throw new SQLException("Retrieval of product log failed.");				
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}			
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}						
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return uploadLogs;
	}
	
	public List<UploadLog> generateProductGroupErrorReport () {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		List<UploadLog> uploadLogs = new ArrayList<UploadLog>();		
		try {

			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //prevent dirty reads			
			callableStatement = connection.prepareCall(productGroupReportSql); //prepare call
			UploadLog uploadLog;
			String logDescription = "Products that aren't in any product group.";
			List<String> headings = Arrays.asList("Id", "Name", "Vendor Id", "Last Modified Date"); //log headers
			hasResults = callableStatement.execute();
			if (hasResults) { 
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}
				logDescription = "Product groups that are out of date.";
				headings = Arrays.asList("Id", "Url", "Website Id", "Vendor Name", "Last Modified Date"); //log headers
				callableStatement.getMoreResults();
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}			
				logDescription = "Product groups that have no up to date images.";
				headings = Arrays.asList("Id", "Url", "Website Id", "Vendor Name", "Last Modified Date"); //log headers
				callableStatement.getMoreResults();			
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}			
				logDescription = "Product groups that have no up to date categories.";
				headings = Arrays.asList("Id", "Url", "Website Id", "Vendor Name", "Last Modified Date"); //log headers
				callableStatement.getMoreResults();			
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}			
				logDescription = "Out of date product group images.";
				headings = Arrays.asList("Group Id", "Image url", "Last Modified Date"); //log headers
				callableStatement.getMoreResults();			
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}			
				logDescription = "Out of date product group categories.";
				headings = Arrays.asList("Group Id", "Category Id", "Category Name", "Last Modified Date"); //log headers
				callableStatement.getMoreResults();			
				resultSet = callableStatement.getResultSet();
				uploadLog = generateUploadLog(logDescription, headings, resultSet);
				if (uploadLog != null) {
					uploadLogs.add(uploadLog);
				}
			} else {
				throw new SQLException ("Retrieval of product log failed.");
			}
		} catch (SQLException se) {
			log.error ("SQL error: " + se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " + se);
				se.printStackTrace();
			}			
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " + se);
				se.printStackTrace();
			}						
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " + se);
				se.printStackTrace();
			}			
		}
		return uploadLogs;
	}
}
