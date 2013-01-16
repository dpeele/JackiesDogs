package jackiesdogs.scrape;

import java.util.List;

import jackiesdogs.bean.UploadLog;

import org.springframework.context.ApplicationContext;

public interface ScrapingUtility {
	
	public List<UploadLog> scrapeSite (String url);
}
