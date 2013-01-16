package jackiesdogs.dataAccess;

import jackiesdogs.bean.Inventory;
import jackiesdogs.bean.Product;
import jackiesdogs.bean.ProductGroup;
import jackiesdogs.bean.UploadLog;

import java.util.*;
import java.sql.*;

public interface ProductUtility {
	
	public List<Product> findProducts (String id, String match, int limit);
	
	public Product updateProduct (Product product);		
	
	public Inventory updateInventory (Inventory inventory, String productId, String vendorId, Connection previousConnection);
	
	public ProductGroup updateProductGroup (ProductGroup productGroup);
	
	public List<UploadLog> generateProductErrorReport ();
	
	public List<UploadLog> generateProductGroupErrorReport ();	
}
