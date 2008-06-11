package org.cagrid.workflow.manager.service.bpelParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class BpelParser {
	
	
	public BpelParser() {

	}
	public WorkflowProcessLayout startParsing(String fileName){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		WorkflowProcessLayout workflowLayout = null;
		try{
			SAXParser saxParser = factory.newSAXParser();
			File newFile = new File(fileName);
			ParserHandler handler = new ParserHandler();
			saxParser.parse(newFile, handler);
			workflowLayout = handler.getLayout();
			workflowLayout.printClass();

		}catch(javax.xml.parsers.ParserConfigurationException e){
			e.printStackTrace();
		}catch(java.io.IOException e){

			e.printStackTrace();
		}catch(org.xml.sax.SAXException e){

			e.printStackTrace();
		}
		return workflowLayout;
	}

	public static void main(String args[]){
		String fileName = "";
		if(args.length != 1){
			System.out.println("Usage is: java MainClass [xml file Name]");
			System.exit(0);
		}else{
			fileName = args[0];
		}

		BpelParser mainClass = new BpelParser();
		mainClass.startParsing(fileName);

	}
	
	
	/** Parse a BPEL description within a string */
	public WorkflowProcessLayout parseString(String bpelDescription, String workflowID) {
		

		String auxFileName = null;
		try {
			auxFileName = java.io.File.createTempFile("WorkflowDescriptor_"
					+ workflowID.replace('/', '-'),".bpel").
					getAbsolutePath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("FileName = " + auxFileName);
		
		
		// now we have to parse the bpel file we received
		// first of all I am going to write it into the current directory
		try {
			// write the
			writeTextFile(bpelDescription, auxFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return this.startParsing(auxFileName);
	}
	
	
	/** Write the contents of a string into a file */
	public static void writeTextFile(String contents, String fullPathFilename)
	throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				fullPathFilename));
		writer.write(contents);
		writer.flush();
		writer.close();
	}
}
