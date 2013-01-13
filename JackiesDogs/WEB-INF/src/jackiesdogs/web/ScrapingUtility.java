package jackiesdogs.web;

import java.util.List;
import jackiesdogs.utility.UploadLog;

import org.springframework.context.ApplicationContext;

public interface ScrapingUtility {
	
	public void setApplicationContext(ApplicationContext applicationContext);
	
	public List<UploadLog> scrapeSite (String url);
}
