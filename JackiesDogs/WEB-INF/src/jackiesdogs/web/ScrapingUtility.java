package jackiesdogs.web;

import org.springframework.context.ApplicationContext;

public interface ScrapingUtility {
	
	public void setApplicationContext(ApplicationContext applicationContext);
	
	public String scrapeSite (String url);
}
