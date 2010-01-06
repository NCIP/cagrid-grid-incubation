package gov.nih.nci.cagrid.metadata;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mdrq.client.MDRQueryClient;
import org.cancergrid.schema.query.Query;
import org.cancergrid.schema.result_set.ConceptRef;
import org.cancergrid.schema.result_set.DataElement;
import org.cancergrid.schema.result_set.ObjectClass;
import org.cancergrid.schema.result_set.Property;
import org.cancergrid.schema.result_set.ResultSet;

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
	public String countryName;
	public String organizationName;
	public String softwareProject;
	public String registryName;
	public String publicId;
	public String version;
	public String mdrQueryURL;
	private static final Log LOG = LogFactory.getLog(MDRUtils.class);   
	   
	public MDRUtils(String mdrURN, String resourceName) {
		MDRId = mdrURN;

		if (MDRId != null) {
			StringTokenizer st = new StringTokenizer(MDRId, "-");
			int tokenCount = st.countTokens();
			if (tokenCount == 6) {
				this.countryName = st.nextToken();
				this.organizationName = st.nextToken();
				this.softwareProject = st.nextToken();
				this.registryName = st.nextToken();
				this.publicId = st.nextToken();
				this.version = st.nextToken();
				LOG.debug("\tDataElement String: " + mdrURN);
			} else {
				LOG.debug("\tThe input MDR Id should comprise of: CountryName-OrganizationName-SoftwareProject-RegistryName-PublicId-Version");
				LOG.debug("\tExample: US-NCICB-CACORE-CADSR-2404655-1.0");
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

		// query grid service and return DataElements Array
		LOG.debug("Running the Grid Service Client Now...");
		try {
			if (publicId != null) {

				MDRQueryClient client = new MDRQueryClient(mdrQueryURL);
				Query query = new Query();
				query.setId(publicId);
				query.setVersion(version);
				LOG.debug("\tFinding Concepts for CDE PublicId: " + publicId);
				query.setResource("caDSR");
				query.setNumResults(100);
				ResultSet results = client.query(query);
				dataElement = results.getDataElement();
				/*
				PrintWriter pw = new PrintWriter(System.out);
				Utils.serializeObject(results, results.getTypeDesc()
						.getXmlType(), pw);						
				*/
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
					}
					
				    listConceptRef.addAll(setConceptRef);
				    /*
				    for (int l=0;l<listConceptRef.size();l++)
			        {
						System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getId());
						System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getName());
						System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getDefinition());
			        }
			        */					
					}			
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return listConceptRef;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public String getOrganizationName() {
		return this.organizationName;
	}

	public String getSoftwareProjectName() {
		return this.softwareProject;
	}

	public String getRegistryName() {
		return this.registryName;
	}

	public String getPublicId() {
		return this.publicId;
	}

	public String getVersion() {
		return this.version;
	}

	
	  public static void main(String[] args) {
		//MDRUtils mdrparser = new MDRUtils("US-NCICB-CACORE-CADSR-2179683-2.0","caDSR"); 
		// has NO enumerated values
		
		MDRUtils mdrparser = new MDRUtils("US-NCICB-CACORE-CADSR-2436860-1.0", "caDSR"); 
		//has enumerated values
		//US-NCICB-CACORE-CADSR-2436860-1.0 Participant Identifier
		
		System.out.println("Input String: US-NCICB-CACORE-CADSR-2436860-1.0");
		List<ConceptRef> listConceptRef = new LinkedList<ConceptRef>();
		listConceptRef = mdrparser.getConceptRefs();
		for (int l=0;l<listConceptRef.size();l++)
        {
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getId());
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getName());
			System.out.println("\t"+((ConceptRef)listConceptRef.get(l)).getDefinition());
        }
	}
}
