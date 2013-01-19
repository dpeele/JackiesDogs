package jackiesdogs.dataAccess;

import jackiesdogs.bean.Inventory;
import jackiesdogs.bean.Product;
import jackiesdogs.bean.ProductGroup;
import jackiesdogs.bean.UploadLog;
import jackiesdogs.bean.VendorInventory;

import java.util.*;
import java.sql.*;

public interface ProductUtility {
	
	public List<Product> findProducts (String id, String match, int limit);
	
	public Product updateProduct (Product product);		
	
	public List<Inventory> updateInventoryItems (List<Inventory> inventoryItems, String productId, String vendorId, Connection previousConnection);
	
	public List<VendorInventory> findVendorInventoryByOrderId (int vendorOrderId);
	
	public List<VendorInventory> updateVendorInventoryItems (List<VendorInventory> vendorInventoryItems, int vendorOrderId, Connection connection);
	
	public ProductGroup updateProductGroup (ProductGroup productGroup);
	
	public List<UploadLog> generateProductErrorReport ();
	
	public List<UploadLog> generateProductGroupErrorReport ();	
}
