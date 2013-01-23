package jackiesdogs.dataAccess;

import jackiesdogs.bean.*;

import java.sql.*;
import java.util.*;

import javax.sql.*;

import org.apache.log4j.Logger;

public class OrderUtilityImpl implements OrderUtility{

	private final DataSource dataSource;
	
	private final ProductUtility productUtility;
	
	private final Logger log = Logger.getLogger(OrderUtilityImpl.class);
	
	private final String findOrderSql = "{CALL order_info_retrieve (?, ?, ?, ?, ?, ?, ?)}";
	
	private final String findVendorOrderSql = "{CALL vendor_order_info_retrieve (?, ?, ?, ?)}";
	
	private final String generateVendorOrderSql = "{CALL generate_vendor_order(?, ?)}";		
	
	private final String updateOrderSql = "{CALL order_info_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	
	private final String updateVendorOrderSql = "{CALL vendor_order_info_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";	

	private final String findOrderItemsSql = "{CALL order_item_retrieve (?)}";

	private final String updateOrderItemSql = "{CALL order_item_update(?, ?, ?, ?, ?, ?)}";

	public OrderUtilityImpl (DataSource dataSource, ProductUtility productUtility) {
		this.dataSource = dataSource; //set dataSource
		this.productUtility = productUtility; //set productUtility
	}
	
	public List<Order> findOrders (OrderSearchTerms terms) {
		List<Order> orders = new ArrayList<Order>();
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		Order order = null;
		boolean hasResults;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads, phantom reads, and nonrepeatable reads
			callableStatement = connection.prepareCall(findOrderSql); //prepare callable statement
			
			int id = terms.getId();
			if (id != 0) { //we need to filter by id
				callableStatement.setInt(1,id);
				callableStatement.setNull(2,Types.DATE);
				callableStatement.setNull(3,Types.DATE);
				callableStatement.setNull(4,Types.VARCHAR);
				callableStatement.setNull(5,Types.VARCHAR);
				callableStatement.setNull(6,Types.BOOLEAN);
				callableStatement.setNull(7,Types.BOOLEAN);
			} else {
				callableStatement.setNull(1,Types.INTEGER);
				java.util.Date startDate = terms.getStartOrderDate();
				if (startDate != null) { //we need to filter orderDate by a start value
					callableStatement.setDate(2,new java.sql.Date(startDate.getTime()));
				} else {
					callableStatement.setNull(2,Types.DATE);
				}
				java.util.Date endDate = terms.getEndOrderDate();
				if (startDate != null) { //we need to filter orderDate by an end value
					callableStatement.setDate(3,new java.sql.Date(endDate.getTime()));
				} else {
					callableStatement.setNull(3,Types.DATE);		
				}
				List<Integer> customerIds = terms.getCustomerIds();
				int customerSize = customerIds.size();				
				if (customerSize > 0) { //we need to filter by customerIds
					String customerSet = "";					
					for (int i=0; i<customerSize; i++) { //for each customer id, add "?, " to prepared statement and add customer id to where values
						customerSet = customerSet+customerIds.get(i)+",";
					}
					customerSet = customerSet.substring(0,customerSet.length()-1); //remove final ","
					callableStatement.setString(4,customerSet);
				} else {
					callableStatement.setNull(4,Types.VARCHAR);
				}
				List<Integer> statusIds = terms.getStatusIds();
				int statusSize = statusIds.size();
				if (statusSize > 0) { //we need to filter by customerIds
					String statusSet = "";					
					for (int i=0; i<statusSize; i++) { //for each customer id, add "?, " to prepared statement and add customer id to where values
						statusSet = statusSet+statusIds.get(i)+",";
					}
					statusSet = statusSet.substring(0,statusSet.length()-1); //remove final ","
					callableStatement.setString(5,statusSet);
				} else {
					callableStatement.setNull(5,Types.VARCHAR);
				}				
				String personal = terms.getPersonal();
				if (personal != null) {
					if (personal.equals("personal")) { //only show personal orders
						callableStatement.setByte(6,(byte)1);
					} else { //we want only business orders
						callableStatement.setByte(6,(byte)0);
					}
				} else { //else show both and we don't need where clause
					callableStatement.setNull(6,Types.BOOLEAN);
				}				
				String delivered = terms.getDelivered();
				if (delivered != null) {
					if (!delivered.equals("delivered")) { //only show delivered orders
						callableStatement.setByte(7,(byte)1);
					} else { //only show undelivered orders
						callableStatement.setByte(7,(byte)0);
					}
				} else { //else show both and we don't need where clause
					callableStatement.setNull(7,Types.BOOLEAN);
				}				
			}									
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.isBeforeFirst()) { //returns false if ResultSet is empty 
					log.debug("No orders returned.");					
				} else {
					while (resultSet.next()) {
						order = new Order(resultSet.getString("id"),
										  resultSet.getDate("order_date"),
										  resultSet.getDate("delivery_date_time"),
										  resultSet.getString("delivery_address"),										 
										  resultSet.getString("delivery_phone"),
										  resultSet.getString("status_name"),
										  resultSet.getString("notes"),										 
										  resultSet.getInt("discount"),
										  resultSet.getDouble("credit"),
										  resultSet.getDouble("delivery_fee"),
										  resultSet.getDouble("toll_expense"),
										  resultSet.getDouble("total_cost"),
										  resultSet.getDouble("total_weight"),										  
										  resultSet.getDouble("change_due"),										 
										  resultSet.getBoolean("delivered"),
										  resultSet.getBoolean("personal"));
						
						Customer customer;
						if (callableStatement.getMoreResults()) { //if this is a single order retrieved by id then we have a 2nd resultset with full customer info
							customer = new Customer(resultSet.getString("id"),
								   	   				resultSet.getString("first_name"),
								   	   				resultSet.getString("last_name"),
								   	   				resultSet.getString("street_address"),										 
								   	   				resultSet.getString("apt_address"),
								   	   				resultSet.getString("city"),
								   	   				resultSet.getString("state"),
								   	   				resultSet.getString("zip"),
								   	   				resultSet.getString("phone"),
								   	   				resultSet.getString("email"),
								   	   				resultSet.getString("notes"));
						} else { //we have thin customer info from main query
							customer = new Customer(resultSet.getString("customer_id"),
													resultSet.getString("first_name"),
													resultSet.getString("last_name"));
						}
						order.setCustomer(customer);
						List<OrderItem> orderItems = null;
						if (id != 0) {
							orderItems = findOrderItemsByOrderId(id);
							if (orderItems == null) {
								throw new SQLException ("Unable to retrieve items for this order.");
							}
							order.setOrderItems(orderItems);
						}
						orders.add(order);
					}
				}
			} else {
				log.debug("No orders returned.");
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
		return orders;
	}	
	
	public Order updateOrder (Order order) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		String id;
		int status;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource		
			connection.setAutoCommit(false); //don't automatically commit statements
			callableStatement = connection.prepareCall(updateOrderSql); //prepare callable statement
			id = order.getId();			
			if (id == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(id));
			}		
			status = Order.STATUS.get(order.getStatus());
			if (status == Order.STATUS.get("Cancelled")) { //this is a cancellation request
				callableStatement.setNull(2, Types.DATE);
				callableStatement.setNull(3, Types.INTEGER);
				callableStatement.setNull(4, Types.DATE);
				callableStatement.setNull(5, Types.VARCHAR);
				callableStatement.setNull(6, Types.VARCHAR);
				callableStatement.setNull(7, Types.INTEGER);
				callableStatement.setNull(8, Types.FLOAT);
				callableStatement.setNull(9, Types.FLOAT);
				callableStatement.setNull(10, Types.FLOAT);
				callableStatement.setNull(11, Types.FLOAT);
				callableStatement.setNull(12, Types.FLOAT);				
				callableStatement.setNull(13, Types.INTEGER);
				callableStatement.setNull(14, Types.FLOAT);
				callableStatement.setNull(15, Types.INTEGER);
				callableStatement.setNull(16, Types.VARCHAR);
				callableStatement.setNull(17, Types.INTEGER);			
				callableStatement.setByte(18, (byte)1);
			} else {
				callableStatement.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
				callableStatement.setInt(3, Integer.parseInt(order.getCustomer().getId()));
				callableStatement.setDate(4, new java.sql.Date(order.getDeliveryDateTime().getTime()));
				callableStatement.setString(5, order.getDeliveryAddress());
				callableStatement.setString(6, order.getDeliveryPhone());
				callableStatement.setInt(7, order.getDiscount());
				callableStatement.setDouble(8, order.getCredit());
				callableStatement.setDouble(9, order.getDeliveryFee());
				callableStatement.setDouble(10, order.getTollExpense());
				callableStatement.setDouble(11, order.getTotalCost());
				callableStatement.setDouble(12, order.getTotalWeight());				
				callableStatement.setInt(13, Order.STATUS.get(order.getStatus()));
				callableStatement.setDouble(14, order.getChangeDue());
				callableStatement.setByte(15, order.isDelivered()? (byte)1 : (byte)0);
				callableStatement.setString(16, order.getNotes());
				callableStatement.setByte(17, order.isPersonal()? (byte)1 : (byte)0);		
				callableStatement.setByte(18, (byte)0);			
			}
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.next()) {
					id = resultSet.getString(1);
					List<OrderItem> orderItems = order.getOrderItems(); 
					orderItems = updateOrderItems(order.getOrderItems(),Integer.parseInt(id), connection);
					if (orderItems == null) {
						throw new SQLException("Unable to update order items.");						
					}
				} else {
					throw new SQLException("Unable to update order.");					
				}
			} else {
				throw new SQLException("Unable to update order.");
			}
			connection.commit();// commit all statement for this connection
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
		return order;
	}		
	
	public VendorOrder generateVendorOrder (List<Integer> orderIds, int vendorTypeId) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		VendorOrder vendorOrder = null;
		List<VendorInventory> vendorInventoryItems = new ArrayList<VendorInventory>();		
		boolean hasResults;
		double totalCost = 0;
		double totalWeight = 0;
		double cost = 0;
		double weight = 0;		
		
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads, phantom reads, and nonrepeatable reads
			callableStatement = connection.prepareCall(generateVendorOrderSql); //prepare callable statement
			int orderSize = orderIds.size();			
			if (orderSize > 0) { //we have order ids to generate order from
				String orderSet = "";					
				for (int i=0; i<orderSize; i++) { //for each order id
					orderSet = orderSet+orderIds.get(i)+",";
				}
				orderSet = orderSet.substring(0,orderSet.length()-1); //remove final ","
				callableStatement.setString(1,orderSet);
			} else {
				log.error("No order ids passed.");
				return null;
			}			
			if (vendorTypeId != 0) {
				callableStatement.setInt(2,vendorTypeId);
			} else {
				log.error("No vendor type id passed.");
				return null;
			}
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.isBeforeFirst()) {
					while (resultSet.next()) {
						cost = resultSet.getDouble("cost");
						weight = resultSet.getDouble("weight");
						totalCost = totalCost + cost;
						totalWeight = totalWeight + weight;
						vendorInventoryItems.add(new VendorInventory(new Product(resultSet.getString("product_id"),
																				 resultSet.getString("product_name"),
																				 resultSet.getString("vendor_id")),
																	 resultSet.getInt("quantity"), weight, cost));
					}
					vendorOrder = new VendorOrder (totalWeight, totalCost, new ArrayList<String>(ProductGroup.VENDORS.keySet()).get(vendorTypeId-1), vendorInventoryItems);
				} else {
					throw new SQLException ("Unable to create vendor order.");
				} 
			} else {
				throw new SQLException ("Unable to create vendor order.");
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
		return vendorOrder;		
	}

	public List<VendorOrder> findVendorOrders (OrderSearchTerms terms) {
		List<VendorOrder> vendorOrders = new ArrayList<VendorOrder>();
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		VendorOrder vendorOrder = null;
		List<VendorInventory> vendorInventoryItems = null;		
		boolean hasResults;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads, phantom reads, and nonrepeatable reads
			callableStatement = connection.prepareCall(findVendorOrderSql); //prepare callable statement
			
			int id = terms.getId();
			if (id != 0) { //we need to filter by id
				callableStatement.setInt(1,id);
				callableStatement.setNull(2,Types.DATE);
				callableStatement.setNull(3,Types.DATE);
				callableStatement.setNull(4,Types.VARCHAR);
				callableStatement.setNull(5,Types.VARCHAR);				
			} else {
				callableStatement.setNull(1,Types.INTEGER);
				java.util.Date startDate = terms.getStartOrderDate();
				if (startDate != null) { //we need to filter orderDate by a start value
					callableStatement.setDate(2,new java.sql.Date(startDate.getTime()));
				} else {
					callableStatement.setNull(2,Types.DATE);
				}
				java.util.Date endDate = terms.getEndOrderDate();
				if (startDate != null) { //we need to filter orderDate by an end value
					callableStatement.setDate(3,new java.sql.Date(endDate.getTime()));
				} else {
					callableStatement.setNull(3,Types.DATE);		
				}
				List<Integer> statusIds = terms.getStatusIds();
				int statusSize = statusIds.size();
				if (statusSize > 0) { //we need to filter by status ids
					String statusSet = "";					
					for (int i=0; i<statusSize; i++) { //for each status id
						statusSet = statusSet+statusIds.get(i)+",";
					}
					statusSet = statusSet.substring(0,statusSet.length()-1); //remove final ","
					callableStatement.setString(4,statusSet);
				} else {
					callableStatement.setNull(4,Types.VARCHAR);
				}
				List<Integer> vendorIds = terms.getVendorIds();
				int vendorSize = vendorIds.size();
				if (vendorSize > 0) { //we need to filter by vendor ids
					String vendorSet = "";					
					for (int i=0; i<vendorSize; i++) { //for each vendor id
						vendorSet = vendorSet+vendorIds.get(i)+",";
					}
					vendorSet = vendorSet.substring(0,vendorSet.length()-1); //remove final ","
					callableStatement.setString(5,vendorSet);
				} else {
					callableStatement.setNull(5,Types.VARCHAR);
				}							
				
			}									
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.isBeforeFirst()) { //returns false if ResultSet is empty 
					log.debug("No orders returned.");					
				} else {
					while (resultSet.next()) {
						vendorOrder = new VendorOrder(resultSet.getString("id"),
										  resultSet.getDate("order_date"),
										  resultSet.getDate("delivery_date_time"),
										  resultSet.getString("vendor_status_name"),
										  resultSet.getString("vendor_name"),
										  resultSet.getString("notes"),										 
										  resultSet.getInt("discount"),
										  resultSet.getDouble("credit"),
										  resultSet.getInt("mileage"),
										  resultSet.getDouble("delivery_fee"),
										  resultSet.getDouble("toll_expense"),
										  resultSet.getDouble("total_cost"),
										  resultSet.getDouble("total_weight"));
						vendorInventoryItems = null;
						if (id != 0) {
							vendorInventoryItems = productUtility.findVendorInventoryByOrderId(id);
							if (vendorInventoryItems == null) {
								log.debug("Unable to retrieve items for this order.");
							}
							vendorOrder.setVendorInventoryItems(vendorInventoryItems);
						}
						vendorOrders.add(vendorOrder);
					}
				}
			} else {
				log.debug("No orders returned.");
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
		return vendorOrders;
	}	
	
	public VendorOrder updateVendorOrder (VendorOrder vendorOrder) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		String id;
		int status;
		List<VendorInventory> vendorInventoryItems;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource		
			connection.setAutoCommit(false); //don't automatically commit statements
			callableStatement = connection.prepareCall(updateVendorOrderSql); //prepare callable statement
			id = vendorOrder.getId();			
			if (id == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(id));
			}		
			status = Order.STATUS.get(vendorOrder.getStatus());
			if (status == Order.STATUS.get("Cancelled")) { //this is a cancellation request
				callableStatement.setNull(2, Types.DATE);
				callableStatement.setNull(3, Types.DATE);
				callableStatement.setNull(4, Types.INTEGER);
				callableStatement.setNull(5, Types.FLOAT);
				callableStatement.setNull(6, Types.FLOAT);
				callableStatement.setNull(7, Types.FLOAT);
				callableStatement.setNull(8, Types.INTEGER);
				callableStatement.setNull(9, Types.FLOAT);
				callableStatement.setNull(10, Types.FLOAT);				
				callableStatement.setNull(11, Types.INTEGER);
				callableStatement.setNull(12, Types.INTEGER);				
				callableStatement.setNull(13, Types.VARCHAR);			
				callableStatement.setByte(14, (byte)1);
			} else {			
				callableStatement.setDate(2, new java.sql.Date(vendorOrder.getOrderDate().getTime()));
				callableStatement.setDate(3, new java.sql.Date(vendorOrder.getDeliveryDate().getTime()));
				callableStatement.setInt(4, vendorOrder.getDiscount());
				callableStatement.setDouble(5, vendorOrder.getCredit());
				callableStatement.setDouble(6, vendorOrder.getDeliveryFee());
				callableStatement.setDouble(7, vendorOrder.getTollExpense());
				callableStatement.setInt(8, vendorOrder.getMileage());			
				callableStatement.setDouble(9, vendorOrder.getTotalCost());
				callableStatement.setDouble(10, vendorOrder.getTotalWeight());				
				callableStatement.setInt(11, VendorOrder.STATUS.get(vendorOrder.getStatus()));
				callableStatement.setInt(12, ProductGroup.VENDORS.get(vendorOrder.getVendor()));
				callableStatement.setString(13, vendorOrder.getNotes());
				callableStatement.setByte(14, (byte)0);
			}
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.next()) {
					id = resultSet.getString(1);
					vendorInventoryItems = vendorOrder.getVendorInventoryItems(); 
					vendorInventoryItems = productUtility.updateVendorInventoryItems(vendorInventoryItems,Integer.parseInt(id), connection);
					if (vendorInventoryItems == null) {
						throw new SQLException("Unable to update vendor inventory items.");						
					}
				} else {
					throw new SQLException("Unable to update vendor order.");					
				}
			} else {
				throw new SQLException("Unable to update vendor order.");
			}
			connection.commit();// commit all statement for this connection
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
		return vendorOrder;
	}		
	
	
	public List<OrderItem> findOrderItemsByOrderId (int orderId) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		String categoryString = null;		
		List<String> categories = null;		
		boolean hasResults;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource			
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); //prevent dirty reads, phantom reads, and nonrepeatable reads			
			callableStatement = connection.prepareCall(findOrderItemsSql); //prepare callable statement
			callableStatement.setInt(1, orderId);
			hasResults = callableStatement.execute();
			Product product;
			OrderItem orderItem;
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (!resultSet.isBeforeFirst()) { //returns false if ResultSet is empty 
					log.debug("No order items returned.");					
				} else {
					while (resultSet.next()) {
						categoryString = resultSet.getString("categories");
						if (categoryString != null && categoryString.contains("|")) {
							categories = new ArrayList<String>(Arrays.asList(categoryString.split("|")));
						}					
						orderItem = new OrderItem(resultSet.getString("order_item_id"),
												 			resultSet.getInt("quantity"),
												 			resultSet.getDouble("weight"),
												 			resultSet.getString("notes"));										 
						product = new Product (resultSet.getString("product_id"),
													   resultSet.getString("product_name"),
													   resultSet.getString("description"),
													   resultSet.getDouble("price"),
													   resultSet.getString("unit_name"),
													   resultSet.getInt("estimated_weight"),
													   resultSet.getString("vendor_id"),
													   categories);
						orderItem.setProduct(product);
						orderItems.add(orderItem);
					}
					
				}	
			} else {
				log.debug("No order item returned");
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
		return orderItems;
	}	
	
	public List<OrderItem> updateOrderItems (List<OrderItem> orderItems, int orderId, Connection previousConnection) {
		boolean closeConnection = false;
		Connection connection = previousConnection; //set connection equal to previous connection so that we can commit in one transaction
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		String id;
		try {
			if (connection == null) { //if we didn't pass a previous connection			
				closeConnection = true;
				connection = dataSource.getConnection(); //get connection from dataSource
			}
			callableStatement = connection.prepareCall(updateOrderItemSql); //prepare callable statement			
			for (OrderItem orderItem: orderItems) {
				id = orderItem.getId();  
				if ( id == null) { //item is new
					callableStatement.setNull(1, Types.INTEGER);
				} else { // existing item
					callableStatement.setInt(1, Integer.parseInt(id));					
				}
				if (orderItem.isRemoved()) { //remove item
					callableStatement.setNull(2, Types.INTEGER);
					callableStatement.setNull(3, Types.INTEGER);
					callableStatement.setNull(4, Types.INTEGER);
					callableStatement.setNull(5, Types.FLOAT);
					callableStatement.setNull(6, Types.VARCHAR);					
					callableStatement.setByte(7, (byte)1);
				} else { //update/insert item
					callableStatement.setInt(1, orderId);
					callableStatement.setInt(2, Integer.parseInt(orderItem.getProduct().getId()));
					callableStatement.setInt(3, orderItem.getQuantity());
					callableStatement.setDouble(4, orderItem.getWeight());
					callableStatement.setString(5, orderItem.getNotes());
					callableStatement.setByte(7, (byte)0);						
				}					
				hasResults = callableStatement.execute();			
				if (hasResults) {
					resultSet = callableStatement.getResultSet();
					if (resultSet.next()) {
						orderItem.setId(id);
					} else {
						throw new SQLException("Unable to update order item.");						
					}
				} else {
					throw new SQLException("Unable to update order item.");
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
		return orderItems;
	}			
}
