package gov.nih.nci.cagrid.domain;
/**
 * DomainGenerator
 *
 * @author dhav01
 *
 * @created Apr 2, 2009 10:31:06 AM
 * 
 */

import gov.nih.nci.cagrid.common.Utils;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.cagrid.mdrq.client.MDRQueryClient;
import org.cancergrid.schema.query.Query;
import org.cancergrid.schema.result_set.ConceptCollection;
import org.cancergrid.schema.result_set.ConceptRef;
import org.cancergrid.schema.result_set.DataElement;
import org.cancergrid.schema.result_set.ResultSet;


import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.FixXmiExecutor;
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;



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
		                           + "-Dxmifile=<XMIFileName> -Ddomainfile=<DomainModelFileName>");
		 }
		 else
		 {
			 System.out.println("Starting the Domain Model Creation Process");
			 String xmiEAModelFile = args[0];
			 String domainModelFile = args[1];
			 String shortName = args[2];
	         String version = args[3];
			 System.out.println(" xmiModelFile: "+xmiEAModelFile +" domainModelFile: "+domainModelFile+" shortname: "+shortName+" version: "+version);

	         XMIParser parser = new XMIParser(shortName, version);
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