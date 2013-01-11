package jackiesdogs.utility;

import java.util.*;

import org.springframework.context.ApplicationContext;

public interface ProductUtility {
	
	public void setApplicationContext (ApplicationContext applicationContext);
	
	public List<Product> findProducts (String id, String match, int limit);
	
	public Product updateProduct (Product product);		
	
	public Inventory updateInventory (Inventory inventory, String productId, String vendorId);
	
	public ProductGroup updateProductGroup (ProductGroup productGroup);
	
	public String generateProductErrorReport ();
	
	public String generateProductGroupErrorReport ();	
}
