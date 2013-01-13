package jackiesdogs.utility;

import java.util.*;
import java.sql.*;

import org.springframework.context.ApplicationContext;

public interface ProductUtility {
	
	public void setApplicationContext (ApplicationContext applicationContext);
	
	public List<Product> findProducts (String id, String match, int limit);
	
	public Product updateProduct (Product product);		
	
	public Inventory updateInventory (Inventory inventory, String productId, String vendorId, Connection previousConnection);
	
	public ProductGroup updateProductGroup (ProductGroup productGroup);
	
	public List<UploadLog> generateProductErrorReport ();
	
	public List<UploadLog> generateProductGroupErrorReport ();	
}
