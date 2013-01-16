package jackiesdogs.utility;

import java.io.*;
import java.nio.charset.Charset;


public class AdminUtilities {
	
	/*convert file with given fileName to a String*/
	public static String fileToString(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			 in = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found: " + fileName);
		}
		byte[] b  = new byte[(int)file.length()];
		int len = b.length;
		int total = 0;
		int result = 0;
		while (total < len) {
			try {
				result = in.read(b, total, len - total);
			} catch (IOException ioe) {
				System.out.println("Unable to read from file");
			}
			if (result == -1) {
				break;
			}
			total += result;
		}

		return new String( b , Charset.forName("UTF-8") );
	}
	
    /*Give the passed String proper capitalization and spacing*/
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
    
    /*Determine if the given string is a number*/
    public static boolean isNumeric(String string)
    {
      return string.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }	
	
    /*Replace unit abbreviation with full unit name*/
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
