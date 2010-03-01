package org.cagrid.redcap.cql.test;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

/*
 * See ReadMe file for instructions
 * about how to run this class
 * against the service
 */
public class QueryRunner {

    public static void main(String[] args) {
        // the URL of the service
        
        String url = args[0];
        // the name of a CQL query XML document
        String queryFilename = args[1];
        try {
            
            DataServiceClient client = new DataServiceClient(url);
            
            
            StringBuffer queryText = Utils.fileToStringBuffer(new File(queryFilename));
            System.out.println("Query:");
            System.out.println(queryText);
            
            CQLQuery query = (CQLQuery) Utils.deserializeObject(new StringReader(queryText.toString()), CQLQuery.class);
            
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