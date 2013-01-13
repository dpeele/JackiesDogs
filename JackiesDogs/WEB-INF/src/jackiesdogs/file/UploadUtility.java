package jackiesdogs.file;

import jackiesdogs.utility.UploadLog;

import java.util.List;

import org.springframework.context.ApplicationContext;

public interface UploadUtility {
	
	public void setApplicationContext (ApplicationContext applicationContext);
	
	public List<UploadLog> uploadProducts (String file);
	
	public List<UploadLog> uploadInvoice (String file);
	
	public void printCellData(List<List<String>> dataHolder);
}
