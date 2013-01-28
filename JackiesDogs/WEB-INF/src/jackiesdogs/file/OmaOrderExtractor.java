package jackiesdogs.file;

import java.io.*;
import java.util.*;


import org.apache.log4j.Logger;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

/*Extract all data from pdf file and put in List of lines*/
public class OmaOrderExtractor implements PdfExtractorUtility{
	private final Logger log = Logger.getLogger(OmaProductExtractor.class);	
	
    public List<String> extractOrder(String fileName) { //function to extract data from pdf file and insert into List<String>
        List<String> dataHolder = ReadPdf(fileName); //extract data from pdf file 
        return dataHolder; //return data
    }

    private List<String> ReadPdf(String fileName) { //read pdf file and put data into list of lines
        List<String> dataHolder = new ArrayList<String>(); //list to hold output of sheet
        PDFParser parser;
        String parsedText;
        PDFTextStripper stripper;
        PDDocument document;
        COSDocument cosDoc;
        PDDocumentInformation pdDocInfo;
        
        try {
        	File file = new File (fileName);
        	FileInputStream fileInputStream = new FileInputStream(file); //stream from file with fileName
        	parser = new PDFParser(fileInputStream); //pass file input stream to pdf document
        	parser.parse(); //parse document
        	cosDoc = parser.getDocument(); // in memory representation of pdf document
        	stripper = new PDFTextStripper(); // stripper to extract text from formatting
        	document = new PDDocument(cosDoc); //get higer level reprensation of pdf document
            parsedText = stripper.getText(document); //strip text from document
            dataHolder = Arrays.asList(parsedText.split("\\r?\\n")); //convert output stream to string, then split string by line and put into array, then convert array to list
            if (!file.delete()) {
            	log.error("Unable to delete file.");
            }           
        } catch (IOException ioe) {
        	log.error("Error reading pdf file: " + ioe);
        }
        return dataHolder;        
    }
}