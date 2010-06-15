package org.cagrid.redcap.cql.test;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.globus.gsi.GlobusCredential;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

/*
 * See ReadMe file for instructions
 * about how to run this class
 * against the service
 */
public class QueryRunner {

	private static final String DORIAN_URL = "https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian";

	public static void main(String[] args) {
        // the URL of the service
        
        String url = args[0];
        // the name of a CQL query XML document
        String queryFilename = args[1];
        String userId = args[2];
        String password = args[3];
        
        QueryRunner queryRunner = new QueryRunner();
        try {
        	GlobusCredential credential = queryRunner.authenticate(DORIAN_URL,DORIAN_URL,userId,password);
			ProxyUtil.saveProxyAsDefault(credential);
			DataServiceClient client = new DataServiceClient(url,credential);
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
    
    public GlobusCredential authenticate(String dorianURL, String authenticationServiceURL, String userId,
	        String password) throws Exception {
	    	GlobusCredential credential = null;
    	try{
    		BasicAuthentication auth = new BasicAuthentication();
    		auth.setUserId(userId);
	        auth.setPassword(password);
	        AuthenticationClient authClient = new AuthenticationClient(authenticationServiceURL);
	        SAMLAssertion saml = authClient.authenticate(auth);
	        CertificateLifetime lifetime = new CertificateLifetime();
	        lifetime.setHours(12);
	        GridUserClient dorian = new GridUserClient(dorianURL);
	        credential = dorian.requestUserCertificate(saml, lifetime);
	        System.out.println(":::::::LOGIN SUCCESSFULL:::::::");
    	}catch(Exception apf){
    		apf.printStackTrace();
    		System.out.println("Error during authentication"+apf);
    		System.exit(1);
    	}
	    return credential;
	}
}