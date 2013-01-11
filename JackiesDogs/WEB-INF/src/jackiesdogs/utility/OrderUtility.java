package jackiesdogs.utility;

import java.util.*;

import org.springframework.context.ApplicationContext;

public interface OrderUtility {

	public void setApplicationContext (ApplicationContext applicationContext);
	
	public List<Order> findOrders (OrderSearchTerms terms);
	
	public Order updateOrder (Order order);
	
	public List<OrderItem> updateOrderItems (List<OrderItem> orderItems, int orderId);
}
