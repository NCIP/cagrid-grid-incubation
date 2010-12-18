package gov.nih.nci.cagrid.metadata;


import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mdrq.client.MDRQueryClient;
import org.cagrid.openmdr.ws.cache.CacheManager;
import org.cagrid.openmdr.ws.config.QueryServiceConfig;
import org.cancergrid.schema.config.Config;
import org.cancergrid.schema.config.Query_service;
import org.cancergrid.schema.query.Query;
import org.cancergrid.schema.result_set.ConceptRef;
import org.cancergrid.schema.result_set.DataElement;
import org.cancergrid.schema.result_set.ObjectClass;
import org.cancergrid.schema.result_set.Property;
import org.cancergrid.schema.result_set.ResultSet;
import org.cancergrid.schema.result_set.ValidValue;
import org.cancergrid.schema.result_set.Values;

/**
 * MDRUtils
 * Query MDR grid service and capture results
 * 
 * @author dhav01
 *
 * @created Mar 24, 2009 10:42:03 AM
 * 
 */

public class MDRUtils {

	public String MDRId;
	public String resourceName;
	public String identifier_prefix;
	public String publicId;
	public String version;
	public String mdrQueryURL;
	private static final Log LOG = LogFactory.getLog(MDRUtils.class);   
	   
	public MDRUtils() {	
		this.MDRId = "";
		this.resourceName = "";
		this.identifier_prefix = "";
		this.publicId = "";
		this.version = "";
		this.mdrQueryURL = "";
	}

	public void idTokenizer(String mdrURN) {
		MDRId = mdrURN;
		   System.out.println("Processing PublicId: "+MDRId);

		if (MDRId != null) {
			StringTokenizer st = new StringTokenizer(MDRId, "_");
			int tokenCount = st.countTokens();
			if (tokenCount == 3) {
				this.identifier_prefix = st.nextToken();
				this.publicId = st.nextToken();
				this.version = st.nextToken();
				LOG.debug("\tDataElement String: " + mdrURN);
			} else {
				LOG.debug("\tThe input MDR Id should comprise of: Identifier_prefix_PublicId_Version");
				LOG.debug("\tExample: cabio.nci.nih.gov_2404655_1.0");
			}
		}

	}

	  /**
     * This method queries the MDR and returns the DataElement Array
	 * @return 
     * 	
     * @return 	org.cancergrid.schema.result_set.DataElement
     */
	public  List<ConceptRef> getConceptRefs() {	

		DataElement dataElement[]=null;
		Vector<ConceptRef> vectorConceptRef = new Vector<ConceptRef>();
		Set<ConceptRef> setConceptRef = new HashSet<ConceptRef>();
        List<ConceptRef> listConceptRef = new LinkedList<ConceptRef>();
        Properties properties = new Properties(); 
        Set<ValidValue> validValues = new HashSet<ValidValue>();
	  	try {
	  		FileInputStream fin = new FileInputStream("./mdrQuery.properties");
	  		properties.load(fin); 
		  	mdrQueryURL = properties.getProperty("mdrQueryUrl"); 
	  	} 
	  	
  		catch(FileNotFoundException fnf)
	  	{
  			System.out.println("File : mdrQuery.properties Not Found!! Please check for the file!!!!");
  			System.out.println("Using default mdrQueryUrl : File : http://localhost:8080/wsrf/services/cagrid/MDRQuery ");
  			mdrQueryURL = "http://localhost:8080/wsrf/services/cagrid/MDRQuery";
	  	}
  		catch (IOException e) {  
  			System.out.println("Some IO Exception occurred. Please again!!!!");
  			System.out.println("Using default mdrQueryUrl : File : http://localhost:8080/wsrf/services/cagrid/MDRQuery ");
  			mdrQueryURL = "http://localhost:8080/wsrf/services/cagrid/MDRQuery";
  		}
  		try 
  		{
  			 QueryServiceConfig config = new QueryServiceConfig(new File("./etc/config.xml"));
  			 Map<String, Query_service> qrs = config.listAvailableServices();
  			 Set entries = qrs.entrySet();
  			 Iterator iterator = entries.iterator();
  		     while (iterator.hasNext()) {
  		       Map.Entry entry = (Map.Entry)iterator.next();
  		       Query_service info = config.getQueryServiceInfo(entry.getKey().toString());      
  		       if((info.getIdentifier_prefix())!=null)
  		       {
	  		       if (info.getIdentifier_prefix().equalsIgnoreCase(identifier_prefix))
	  		       {		  		    		   
	  		    	   resourceName = info.getName();
	  		    	   if(info.getName().equalsIgnoreCase("openMDR"))
	  		    	   {
	  		    		   publicId = MDRId;
	  		    	   }
	  		       }
  		       }

  		     }	 
  	  	} 
  		catch (Exception e) {
             LOG.error("QueryServiceManager: " + e);
         }
  		
		// query grid service and return DataElements Array
		LOG.debug("Running the Grid Service Client Now...");
		try {
			if (publicId != null) {

				MDRQueryClient client = new MDRQueryClient(mdrQueryURL);
				Query query = new Query();
				query.setId(publicId);
				query.setVersion(version);
				LOG.debug("\tFinding Concepts for CDE PublicId: " + publicId);
				query.setResource(resourceName);
				query.setNumResults(100);
				ResultSet results = client.query(query);
				dataElement = results.getDataElement();
				
				PrintWriter pw = new PrintWriter(System.out);
				Utils.serializeObject(results, results.getTypeDesc()
						.getXmlType(), pw);						
				
				if (dataElement!=null)
				{
					for (int numDataElement=0;numDataElement<dataElement.length;numDataElement++)
					{
						ObjectClass[] objClass = dataElement[numDataElement].getObjectClass();
						for (int numObjectClass=0;numObjectClass<objClass.length;numObjectClass++)
						{
							ConceptRef[] conRef  = objClass[numObjectClass].getConceptCollection().getConceptRef();
							for (int numConRef=0;numConRef<conRef.length;numConRef++)
							{
								vectorConceptRef.addElement(conRef[numConRef]);
								setConceptRef.add(conRef[numConRef]);
								System.out.print(conRef[numConRef].getName());
							}
						
						}
						
						Property[] propClass = dataElement[numDataElement].getProperty();
						for (int numPropertyClass=0;numPropertyClass<propClass.length;numPropertyClass++)
						{
							ConceptRef[] conRef  = propClass[numPropertyClass].getConceptCollection().getConceptRef();
							for (int numConRef=0;numConRef<conRef.length;numConRef++)
							{
								vectorConceptRef.addElement(conRef[numConRef]);
								setConceptRef.add(conRef[numConRef]);

							}
						
						}
						
					    Values valuesClass = dataElement[numDataElement].getValues();
				        if(valuesClass.getEnumerated()!=null){
						    ValidValue[] validValue  = valuesClass.getEnumerated().getValidValue();
						    
						    for (int numvalidValues=0;numvalidValues<validValue.length;numvalidValues++)
						    {
						    	validValues.add(validValue[numvalidValues]);
						    }
				        }
					    
					}
				    listConceptRef.addAll(setConceptRef);
					}			
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return listConceptRef;
	}

	public String getRegistrationAuthority() {
		return this.identifier_prefix;
	}


	public String getPublicId() {
		return this.publicId;
	}

	public String getVersion() {
		return this.version;
	}

	
	  public static void main(String[] args) {
		MDRUtils mdrparser = new MDRUtils();
		mdrparser.idTokenizer("cabio.nci.nih.gov_2436860_1.0");
		//MDRUtils mdrparser = new MDRUtils("cagrid.org_55c515b8-06d2-4c2d-9010-f6f9ed974ae8_0.1"); 
		
		List<ConceptRef> listConceptRef = new LinkedList<ConceptRef>();
		listConceptRef = mdrparser.getConceptRefs();
		for (int l=0;l<listConceptRef.size();l++)
        {
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getId());
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getName());
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getDefinition());
        }
		
	}
	  
  /**
     * This method queries the MDR and returns the Valid Values
	 * @return 
     * 	
     * @return 	List ValidValues
     */	
  public  List<ValidValue> getValidValues() {	

		DataElement dataElement[]=null;
		Vector<ConceptRef> vectorConceptRef = new Vector<ConceptRef>();
		Set<ConceptRef> setConceptRef = new HashSet<ConceptRef>();
        List<ConceptRef> listConceptRef = new LinkedList<ConceptRef>();
        Properties properties = new Properties(); 
        List<ValidValue> listValidValues = new LinkedList<ValidValue>();
        Set<ValidValue> validValues = new HashSet<ValidValue>();
	  	try {
	  		FileInputStream fin = new FileInputStream("./mdrQuery.properties");
	  		properties.load(fin); 
		  	mdrQueryURL = properties.getProperty("mdrQueryUrl"); 
	  	} 
	  	
  		catch(FileNotFoundException fnf)
	  	{
  			System.out.println("File : mdrQuery.properties Not Found!! Please check for the file!!!!");
  			System.out.println("Using default mdrQueryUrl : File : http://localhost:8080/wsrf/services/cagrid/MDRQuery ");
  			mdrQueryURL = "http://localhost:8080/wsrf/services/cagrid/MDRQuery";
	  	}
  		catch (IOException e) {  
  			System.out.println("Some IO Exception occurred. Please again!!!!");
  			System.out.println("Using default mdrQueryUrl : File : http://localhost:8080/wsrf/services/cagrid/MDRQuery ");
  			mdrQueryURL = "http://localhost:8080/wsrf/services/cagrid/MDRQuery";
  		}
  		try 
  		{
  			 QueryServiceConfig config = new QueryServiceConfig(new File("./etc/config.xml"));
  			 Map<String, Query_service> qrs = config.listAvailableServices();
  			 Set entries = qrs.entrySet();
  			 Iterator iterator = entries.iterator();
  		     while (iterator.hasNext()) {
  		       Map.Entry entry = (Map.Entry)iterator.next();
  		       Query_service info = config.getQueryServiceInfo(entry.getKey().toString());      
  		       if((info.getIdentifier_prefix())!=null)
  		       {
	  		       if (info.getIdentifier_prefix().equalsIgnoreCase(identifier_prefix))
	  		       {		  		    		   
	  		    	   resourceName = info.getName();
	  		    	   if(info.getName().equalsIgnoreCase("openMDR"))
	  		    	   {
	  		    		   publicId = MDRId;
	  		    	   }
	  		       }
  		       }

  		     }	 
  	  	} 
  		catch (Exception e) {
             LOG.error("QueryServiceManager: " + e);
         }
  		
		// query grid service and return DataElements Array
		LOG.debug("Running the Grid Service Client Now...");
		try {
			if (publicId != null) {

				MDRQueryClient client = new MDRQueryClient(mdrQueryURL);
				Query query = new Query();
				query.setId(publicId);
				query.setVersion(version);
				LOG.debug("\tFinding Concepts for CDE PublicId: " + publicId);
				query.setResource(resourceName);
				query.setNumResults(100);
				ResultSet results = client.query(query);
				dataElement = results.getDataElement();
				
				PrintWriter pw = new PrintWriter(System.out);
				Utils.serializeObject(results, results.getTypeDesc()
						.getXmlType(), pw);						
				
				if (dataElement!=null)
				{
					for (int numDataElement=0;numDataElement<dataElement.length;numDataElement++)
					{
						ObjectClass[] objClass = dataElement[numDataElement].getObjectClass();
						for (int numObjectClass=0;numObjectClass<objClass.length;numObjectClass++)
						{
							ConceptRef[] conRef  = objClass[numObjectClass].getConceptCollection().getConceptRef();
							for (int numConRef=0;numConRef<conRef.length;numConRef++)
							{
								vectorConceptRef.addElement(conRef[numConRef]);
								setConceptRef.add(conRef[numConRef]);
								System.out.print(conRef[numConRef].getName());
							}
						
						}
						
						Property[] propClass = dataElement[numDataElement].getProperty();
						for (int numPropertyClass=0;numPropertyClass<propClass.length;numPropertyClass++)
						{
							ConceptRef[] conRef  = propClass[numPropertyClass].getConceptCollection().getConceptRef();
							for (int numConRef=0;numConRef<conRef.length;numConRef++)
							{
								vectorConceptRef.addElement(conRef[numConRef]);
								setConceptRef.add(conRef[numConRef]);

							}
						
						}
						
					    Values valuesClass = dataElement[numDataElement].getValues();
				        if(valuesClass.getEnumerated()!=null){
						    ValidValue[] validValue  = valuesClass.getEnumerated().getValidValue();
						    System.out.println("Printing valid value lenght:"+validValue.length);
						   
						    for (int numvalidValues=0;numvalidValues<validValue.length;numvalidValues++)
						    {
						    	validValues.add(validValue[numvalidValues]);

						    }
				        }
					    
					}
				    listValidValues.addAll(validValues);
					}			
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return listValidValues;
	}
}
