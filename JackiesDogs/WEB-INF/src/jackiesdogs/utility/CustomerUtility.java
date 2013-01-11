package jackiesdogs.utility;

import java.util.*;

import org.springframework.context.ApplicationContext;

public interface CustomerUtility {
	
	public void setApplicationContext (ApplicationContext applicationContext);
	
	public List<Customer> findCustomers (String id, String match, int limit);
	
	public Customer updateCustomer (Customer customer);
}
