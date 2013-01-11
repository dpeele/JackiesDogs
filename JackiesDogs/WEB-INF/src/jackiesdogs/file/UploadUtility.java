package jackiesdogs.file;

import java.util.List;

import org.springframework.context.ApplicationContext;

public interface UploadUtility {
	
	public void setApplicationContext (ApplicationContext applicationContext);
	
	public String uploadProducts (String file);
	
	public String uploadInvoice (String file);
	
	public void printCellData(List<List<String>> dataHolder);
}
