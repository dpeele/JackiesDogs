package jackiesdogs.file;

import java.io.*;
import java.util.*;


import org.apache.log4j.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/*Extract all data from pdf file and put in List of lines*/
public class OmaOrderExtractor implements PdfExtractorUtility{
	private final Logger log = Logger.getLogger(OmaProductExtractor.class);	
	
    public List<String> extractOrder(String file) { //function to extract data from pdf file and insert into List<String>
        List<String> dataHolder = ReadPdf(file); //extract data from pdf file 
        return dataHolder; //return data
    }

    private List<String> ReadPdf(String file) { //read pdf file and put data into list of lines
        List<String> dataHolder = new ArrayList<String>(); //list to hold output of sheet

        try {
            ByteArrayInputStream myInput = new ByteArrayInputStream(file.getBytes());//stream from file
            PDDocument document = PDDocument.load(myInput); //put into pdf document
            PDFTextStripper stripper = new PDFTextStripper(); //create text stripper to get text out of pdf document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //outputStream to hold outputed text
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream)); //writer to write to outputStream
            stripper.writeText(document, writer); //write text to output stream
            dataHolder = Arrays.asList(outputStream.toString().split("\\r?\\n")); //convert output stream to string, then split string by line and put into array, then convert array to list
        } catch (IOException ioe) {
        	log.error("Error reading pdf file: " + ioe);
        }
        return dataHolder;
        
    }
}