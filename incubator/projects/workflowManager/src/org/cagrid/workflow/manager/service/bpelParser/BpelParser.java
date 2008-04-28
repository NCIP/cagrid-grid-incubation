package org.cagrid.workflow.manager.service.bpelParser;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.*;

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
}
