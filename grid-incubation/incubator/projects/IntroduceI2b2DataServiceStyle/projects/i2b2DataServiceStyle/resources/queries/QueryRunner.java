/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

public class QueryRunner {

    public static void main(String[] args) {
        // the URL of the service
        String url = "http://localhost:8080/wsrf/services/cagrid/I2B2Demo";
        
        // the name of a CQL query XML document
        String queryFilename = "queries/Investigators.xml";
        // String queryFilename = "/queries/HealthCareProviders.xml";
        // String queryFilename = "/queries/StudyNctNumber.xml";
        
        try {
            // create the standard data service client
            DataServiceClient client = new DataServiceClient(url);
            
            // load the query
            StringBuffer queryText = Utils.fileToStringBuffer(new File(queryFilename));
            System.out.println("Query:");
            System.out.println(queryText);
            
            // deserialize the query into a CQL object
            CQLQuery query = (CQLQuery) Utils.deserializeObject(
                new StringReader(queryText.toString()), CQLQuery.class);
            
            // invoke the data service with the query
            System.out.println("Querying");
            CQLQueryResults results = client.query(query);
            
            // iterate the results as XML
            Iterator<?> iter = new CQLQueryResultsIterator(results, true);
            while (iter.hasNext()) {
                System.out.println(iter.next());
                if (iter.hasNext()) {
                    System.out.println();
                }
            }
            
            // done
            System.out.println("Done");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
