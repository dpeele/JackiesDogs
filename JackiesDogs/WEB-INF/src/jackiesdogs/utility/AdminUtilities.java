package jackiesdogs.utility;

public class AdminUtilities {
    
    public static String toProperCase(String string) {
    	String[] words = string.split(" ");
    	String finalTitle = "";
    	for (String word : words) {
    		if (word.length() == 1) {
    			finalTitle = finalTitle + word.toUpperCase() + " ";
    		} else if( word.length() > 1) {
    			finalTitle = finalTitle + word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase() + " ";
    		}
    	}
    	finalTitle = finalTitle.substring(0,finalTitle.length()-1);
    	return finalTitle;
    }
    
    public static boolean isNumeric(String string)
    {
      return string.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }	
	
    public static String formatUnit (String unit) {
    	if (unit.contains("ea")) {
    		return ("Each");
    	}
    	if (unit.contains("cs") || unit.trim().length() == 0) {
    		return ("Case");
    	}
    	if (unit.contains("lb")) {
    		return ("Pound");
    	}
    	if (unit.contains("pc")) {
    		return ("Piece");
    	}
    	if (unit.contains("pkg")) {
    		return ("Package");
    	}    	
    	return toProperCase(unit);
    }	

}
