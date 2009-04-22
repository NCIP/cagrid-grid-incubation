package gov.nih.nci.cagrid.domain;


import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


/**
 * DomainGenerator Query MDR grid service and capture results
 * 
 * @author dhav01
 * 
 * @created Mar 24, 2009 10:42:03 AM
 * 
 */

public class DomainGenerator {

	public static void main(String[] args) {

		 if (args.length != 4) 
		 {
		        System.out.println("Usage: ant run "
		                           + "-DxmiFile=<XMIFileName> -DdomainFile=<DomainModelFileName> -DprojectName=<Project Name> -DprojectVersion=<Project Version>");
		 }
		 else
		 {
			 System.out.println("Starting the Domain Model Creation Process");
			 String xmiEAModelFile = args[0];
			 String domainModelFile = args[1];
			 String projectName = args[2];
	         String projectVersion = args[3];
	         if ((xmiEAModelFile== null) || (domainModelFile==null) || (projectName==null) || (projectVersion==null))
	         {
	        	 System.out.println("Please specify the inputs");
	         }
			 System.out.println("XMI Model File: "+xmiEAModelFile +"; Domain Model File: "+domainModelFile+"; Project Name: "+projectName+"; Project Version: "+projectVersion);

	         XMIParser parser = new XMIParser(projectName, projectVersion);
	         File xmiFile = new File(xmiEAModelFile);
	         try {
				 InputStream is = new BufferedInputStream(
				            new FileInputStream(xmiEAModelFile));
		         DomainModel model = null;
		         try {
		        	 model = parser.parse(is, XmiFileType.SDK_40_EA);
		         } 
		         catch (Exception ex) {
		        	 ex.printStackTrace();
		             System.out.println("Error parsing XMI to domain model! " +ex.getMessage());
		         }
		         if (model != null) {
		        	 try {
		        		 FileWriter writer = new FileWriter(args[1]);
				         MetadataUtils.serializeDomainModel(model, writer);
				         writer.flush();
				         writer.close();
				     } 
		        	 catch (Exception ex) {
		        		 ex.printStackTrace();
				         System.exit(1);
		        	 }
				 }
	         }
	         catch (FileNotFoundException e) 
	         {
	            System.err.println("FileNotFoundError: " + e);
	         } 
	         catch (IOException e) 
	         {
	            System.err.println("IOException: " + e);
	         }
		 }		
	}
}