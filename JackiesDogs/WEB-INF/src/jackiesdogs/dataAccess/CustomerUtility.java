package jackiesdogs.dataAccess;

import jackiesdogs.bean.Customer;

import java.util.*;

public interface CustomerUtility {
	
	public List<Customer> findCustomers (String id, String match, int limit);
	
	public Customer updateCustomer (Customer customer);
}
