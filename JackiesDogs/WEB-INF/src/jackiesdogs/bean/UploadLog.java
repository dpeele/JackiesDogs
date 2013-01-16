package jackiesdogs.bean;

import java.util.*;

public class UploadLog {
	private String logDescription;
	private List<String> headings;
	private List<List<String>> log;
	
	public UploadLog(String logDescription, List<String> headings, List<List<String>> log) {
		this.logDescription = logDescription;
		this.headings = headings;
		this.log = log;
	}

	public String getLogDescription() {
		return logDescription;
	}

	public void setLogDescription(String logDescription) {
		this.logDescription = logDescription;
	}

	public List<String> getHeadings() {
		return headings;
	}
	
	public void setHeadings(List<String> headings) {
		this.headings = headings;
	}
	
	public List<List<String>> getLog() {
		return log;
	}
	
	public void setLog(List<List<String>> log) {
		this.log = log;
	}
	
}
