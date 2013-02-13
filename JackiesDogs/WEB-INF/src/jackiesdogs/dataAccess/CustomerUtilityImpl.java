package jackiesdogs.dataAccess;

import jackiesdogs.bean.Customer;

import java.sql.*;
import java.util.*;

import javax.sql.*;

import org.apache.log4j.Logger;

public class CustomerUtilityImpl implements CustomerUtility {

	private final DataSource dataSource;
	
	private final Logger log = Logger.getLogger(CustomerUtilityImpl.class);
	
	private final String listCustomersSql = "{CALL customer_retrieve (?, ?, ?, ?)}";
	
	private final String updateCustomerSql = "{CALL customer_update (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	
	public CustomerUtilityImpl (DataSource dataSource) {
		this.dataSource = dataSource; //set data source
	}
	
	public List<Customer> findCustomers (String id, String match, int limit) {		
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		List<Customer> customers = new ArrayList<Customer>();
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //prevent dirty reads
			callableStatement = connection.prepareCall(listCustomersSql); //prepare callable statement	
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
			callableStatement.setNull(4, Types.INTEGER);							
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.isBeforeFirst()) { //customers have been returned
					while (resultSet.next()) {
						customers.add(new Customer(resultSet.getString("id"),
											   	   resultSet.getString("first_name"),
											   	   resultSet.getString("last_name"),
											   	   resultSet.getString("street_address"),										 
											   	   resultSet.getString("apt_address"),
											   	   resultSet.getString("city"),
											   	   resultSet.getString("state"),
											   	   resultSet.getString("zip"),
											   	   resultSet.getString("phone"),
											   	   resultSet.getString("email"),
											   	   resultSet.getString("notes")));
					}
				} else {
					log.debug("No customers returned.");
				}
			} else {
				log.debug("No customers returned.");
			}
		} catch (SQLException se) {
			log.error ("SQL error: " , se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " , se);
			}
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " , se);
			}						
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " , se);
			}			
		}
		return customers;
	}
	
	public Customer updateCustomer (Customer customer) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet resultSet = null;
		boolean hasResults;
		String id;
		try {			
			connection = dataSource.getConnection(); //get connection from dataSource
			callableStatement = connection.prepareCall(updateCustomerSql); //prepare callable statement
			id = customer.getId();
			if (id == null) {
				callableStatement.setNull(1, Types.INTEGER);
			} else {
				callableStatement.setInt(1, Integer.parseInt(id));
			}
			if (customer.isInactive()) {
				callableStatement.setNull(2, Types.VARCHAR);
				callableStatement.setNull(3, Types.VARCHAR);
				callableStatement.setNull(4, Types.VARCHAR);
				callableStatement.setNull(5, Types.VARCHAR);
				callableStatement.setNull(6, Types.VARCHAR);
				callableStatement.setNull(7, Types.VARCHAR);
				callableStatement.setNull(8, Types.VARCHAR);
				callableStatement.setNull(9, Types.VARCHAR);
				callableStatement.setNull(10, Types.VARCHAR);
				callableStatement.setNull(11, Types.VARCHAR);				
				callableStatement.setByte(12, (byte)1);						
			} else {
				callableStatement.setString(2, customer.getFirstName());
				callableStatement.setString(3, customer.getLastName());
				callableStatement.setString(4, customer.getStreetAddress());
				callableStatement.setString(5, customer.getAptAddress());
				callableStatement.setString(6, customer.getCity());
				callableStatement.setString(7, customer.getState());
				callableStatement.setString(8, customer.getZip());
				callableStatement.setString(9, customer.getPhone());
				callableStatement.setString(10, customer.getEmail());
				callableStatement.setString(11, customer.getNotes());
				callableStatement.setByte(12, (byte)0);							
			}
			hasResults = callableStatement.execute();
			if (hasResults) {
				resultSet = callableStatement.getResultSet();
				if (resultSet.next()) {
					customer.setId(resultSet.getString(1));
				} else {
					throw new SQLException ("Unable to update customer.");
				}
			} else {
				throw new SQLException ("Unable to update customer.");
			}			
		} catch (SQLException se) {
			log.error ("SQL error: " , se);
			return null;
		} finally {
			try {
				resultSet.close();
			} catch (Exception se) {
				log.error("Unable to close resultSet: " , se);
			}		
			try {
				callableStatement.close();
			} catch (Exception se) {
				log.error("Unable to close callableStatement: " , se);
			}							
			try {
				connection.close();
			} catch (Exception se) {
				log.error("Unable to close connection: " , se);
			}			
		}
		return customer;
	}		
}
