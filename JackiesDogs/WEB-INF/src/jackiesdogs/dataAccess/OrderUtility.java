package jackiesdogs.dataAccess;

import jackiesdogs.bean.*;
import java.util.*;
import java.sql.*;

public interface OrderUtility {
	
	public List<Order> findOrders (OrderSearchTerms terms);
	
	public Order updateOrder (Order order);
	
	public List<VendorOrder> findVendorOrders (OrderSearchTerms terms);
	
	public VendorOrder updateVendorOrder (VendorOrder order);	
	
	public List<OrderItem> updateOrderItems (List<OrderItem> orderItems, int orderId, Connection previousConnection);
}
