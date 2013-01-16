package jackiesdogs.file;

import jackiesdogs.bean.UploadLog;

import java.util.List;

import org.springframework.context.ApplicationContext;

public interface UploadUtility {
	
	public List<UploadLog> uploadProducts (String fileName);
	
	public List<UploadLog> uploadInvoice (String fileName);
	
	public void printCellData(List<List<String>> dataHolder);
}
