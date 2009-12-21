package gov.nih.nci.cagrid.metadata;


import gov.nih.nci.cagrid.common.Utils;

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
	private static final Log LOG = LogFactory.getLog(MDRUtils.class);   
	   
//	ArrayList<DataElement> dataElementCollection = new ArrayList();
//	ArrayList<ConceptCollection> conceptCollection = new ArrayList();

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
     * 	
     * @return 	org.cancergrid.schema.result_set.DataElement
     */
	public DataElement[] getDataElements() {	
//		ArrayList<String> conceptArr = new ArrayList<String>();
//		ArrayList<String> valueDomainConceptArr = new ArrayList<String>();
		DataElement dataElement[]=null;
		
		// query grid service and return DataElements Array
		LOG.debug("Running the Grid Service Client Now...");
		try {
			if (publicId != null) {
				MDRQueryClient client = new MDRQueryClient(
						"http://localhost:8090/wsrf/services/cagrid/MDRQuery");
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

				
				if (dataElement!=null)
				{
					System.out.println("ConceptCollection:");
					for (int i=0;i<dataElement.length;i++)
					{
						dataElementCollection.add(dataElement[i]);
						ConceptCollection cc = dataElement[i].getConceptCollection();
						ConceptRef[] crf = cc.getConceptRef();
							for (int j = 0; j < crf.length; j++) {
							System.out.println("Id: "+crf[j].getId());
							System.out.println("Name: "+crf[j].getName());
							System.out.println("Definition: "+crf[j].getDefinition());
							conceptArr.add(crf[j].getId());
						}						
					}
					System.out.println("PropertyConceptCodes: "+conceptArr); 
					System.out.println("ConceptCollection for Value Domain:");
					for (int i = 0; i < dataElement.length; i++) {
						Values v = dataElement[i].getValues();
						if (v.getEnumerated()!=null)
						{
							if (v.getEnumerated().getValidValue().length > 0) {
							ValidValue[] vv = v.getEnumerated().getValidValue();
								for (int k = 0; k < vv.length; k++) {
								ConceptCollection cc = vv[k]
										.getConceptCollection();
								ConceptRef[] crf = cc.getConceptRef();
								for (int j = 0; j < crf.length; j++) {
									valueDomainConceptArr.add(crf[j].getId());
								}
							}
						}
							System.out.println("ObjectClassPropertyConceptCodes: "+valueDomainConceptArr); 
						}
						else
							  System.out.println("No ObjectClassPropertyConceptCodes found!"); 
					}
				}
*/				
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return dataElement;
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
		MDRUtils mdrparser = new MDRUtils("US-NCICB-CACORE-CADSR-2179683-2.0",
				"caDSR"); // has NO enumerated values
		// MDRUtils mdrparser = new
		// MDRUtils("US-NCICB-CACORE-CADSR-2436860-1.0", "caDSR"); //has
		// enumerated values

		System.out.println("Input String: US-NCICB-CACORE-CADSR-2436860-1.0");
		DataElement de[] = mdrparser.getDataElements();
		/*
		if (de != null) {
			System.out.println("ConceptCollection:");
			
			for (int i = 0; i < de.length; i++) {
				ConceptCollection cc = de[i].getConceptCollection();
				ConceptRef[] crf = cc.getConceptRef();
				for (int j = 0; j < crf.length; j++) {
					System.out.println("Id: " + crf[j].getId());
					System.out.println("Name: " + crf[j].getName());
					System.out.println("Definition: " + crf[j].getDefinition());
				}
			}
		}
		*/
	}
}
