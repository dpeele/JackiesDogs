package jackiesdogs.file;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import org.apache.log4j.Logger;

/*Extract all cell from excel file and put in List of Lists*/
public class OmaProductExtractor implements ExcelExtractorUtility{
	private final Logger log = Logger.getLogger(OmaProductExtractor.class);	
	
    public List<List<String>> extractProducts(String file) { //function to extract data from excel file and insert into database
        List<List<String>> dataHolder = ReadExcel(file); //extract data from excel file 
        return dataHolder; //return data
    }

    private List<List<String>> ReadExcel(String fileName) { //read excel file and put data into list of lists
        List<List<String>> sheetHolder = new ArrayList<List<String>>(); //list to hold output of sheet

        try {
        	File file = new File(fileName);
            FileInputStream myInput = new FileInputStream(fileName);//stream from file with fileName
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput); //poi filesystem object fed my stream
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem); //excel workbook from poi filesystem object
            HSSFSheet mySheet = myWorkBook.getSheetAt(0); //sheet from workbook
            Iterator<Row> rowIter = mySheet.rowIterator(); //iterator to iterate through rows
            while (rowIter.hasNext()) { //for each row
                HSSFRow myRow = (HSSFRow) rowIter.next(); //get the row
                List<String> rowHolder = new ArrayList<String>(); //create list to hold output of this row
                for (int i=0; i<11; i++) { //for each cell
                    HSSFCell myCell = (HSSFCell) myRow.getCell(i); //extract cell
                    String cellData = getCellvalue(myCell); //convert cell to string
                    rowHolder.add(cellData); //convert to String
                }
                sheetHolder.add(rowHolder);
            }
            if (!file.delete()) {
            	log.error("Unable to delete file.");
            }
        } catch (Exception e) {
        	log.error("Error extracting data: ", e);
        }        
        return sheetHolder;
    }

    private String getCellvalue(HSSFCell cell) {

            if (cell != null) { //if cell isn't null
            	if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) { //if it's a formula
                    switch(cell.getCachedFormulaResultType()) { //determine if it's string or number
                        case HSSFCell.CELL_TYPE_NUMERIC: //if it's a number, return String containing number rounded to two decimal places
                            return new Double((double)Math.round(cell.getNumericCellValue()*100)/100).toString();
                        case HSSFCell.CELL_TYPE_STRING: //if it's a string, return
                            return cell.getRichStringCellValue().toString();
                    }
                }            	
                if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) { //if it's a string, return value
                    return cell.getRichStringCellValue().toString();
                }  else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) { //if it's a boolean, convert to true/false string
                    return new String( (cell.getBooleanCellValue() == true ? "true" : "false") );
                } else if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) { // if it's blank, return empty string
                    return "";
                } else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) { //if it's numeric
                    if(HSSFDateUtil.isCellDateFormatted(cell)){ //if it's a data, return string containing simple data format
                        return ( new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue()) );
                    }else{ //if it's a number, return string containing number rounded to two decimal places
                        return new Double((double)Math.round(cell.getNumericCellValue()*100)/100).toString();
                    }
                }
            }

        return null;
    }    
}