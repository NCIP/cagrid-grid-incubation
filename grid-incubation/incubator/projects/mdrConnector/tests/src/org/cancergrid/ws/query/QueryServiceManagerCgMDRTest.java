package org.cancergrid.ws.query;

import org.cancergrid.schema.query.Query;
import org.cancergrid.ws.query.QueryServiceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryServiceManagerCgMDRTest
{
	private static org.cancergrid.ws.query.QueryServiceManager qsm;
	private static String caDSR ="caDSR";
	private static String EVS = "EVS-DescLogicConcept";
            private static String cgMDR = "cgMDR";
	
	@Before
	public void createQueryServiceManager()
	{
		 qsm = new QueryServiceManager();
	}

	@Test
	public void listResourcesAsXmlValidResponse()
	{
		org.cancergrid.services.query.ListResourcesAsXmlResponseDocument response = qsm.listResourcesAsXml();
		Assert.assertTrue("Invalid response format: " + response.toString(), response.validate());
	}

	@Test
	public void queryMDRValidResponse()
	{
		org.cancergrid.services.query.QueryDocument queryDoc = org.cancergrid.services.query.QueryDocument.Factory.newInstance();
		Query query = queryDoc.addNewQuery().addNewQuery();
		query.setResource(cgMDR);
		query.setTerm("cancer");
		query.setNumResults(5);
		org.cancergrid.services.query.QueryResponseDocument response = qsm.query(queryDoc);
		Assert.assertTrue("Invalid response format: " + response.toString(), response.validate());
	}
	
	@Test
	public void queryStringMDRValidResponse() throws Exception
	{
		org.cancergrid.services.query.QueryStringDocument queryDoc = org.cancergrid.services.query.QueryStringDocument.Factory.newInstance();
		Query query = queryDoc.addNewQueryString().addNewQuery();
		query.setResource(cgMDR);
		query.setTerm("cancer");
		query.setNumResults(5);
		org.cancergrid.services.query.QueryStringResponseDocument response = qsm.queryString(queryDoc);
		org.cancergrid.schema.result_set.ResultSetDocument results = org.cancergrid.schema.result_set.ResultSetDocument.Factory.parse(response.getQueryStringResponse().getReturn());
		
		Assert.assertTrue("Invalid response format: " + response.toString(), results.validate());
	}
	
	@Test
	public void queryStringCLSValidResponse() throws Exception
	{
		org.cancergrid.services.query.QueryStringDocument queryDoc = org.cancergrid.services.query.QueryStringDocument.Factory.newInstance();
		Query query = queryDoc.addNewQueryString().addNewQuery();
		query.setResource("cgMDR-with-Classification");
		query.setTerm("*");
		query.setSrc("http://www.cancergrid.org/ontologies/data-element-classification#144B849A3");
		query.setNumResults(5);
		org.cancergrid.services.query.QueryStringResponseDocument response = qsm.queryString(queryDoc);
		org.cancergrid.schema.result_set.ResultSetDocument results = org.cancergrid.schema.result_set.ResultSetDocument.Factory.parse(response.getQueryStringResponse().getReturn());
		
		Assert.assertTrue("Invalid response format: " + response.toString(), results.validate());
	}
	
	@Test
	public void queryCLSValidResponse()
	{
		org.cancergrid.services.query.QueryDocument queryDoc = org.cancergrid.services.query.QueryDocument.Factory.newInstance();
		Query query = queryDoc.addNewQuery().addNewQuery();
		query.setResource("cgMDR-Classification-Schemes");
		query.setTerm("cancer");
		org.cancergrid.services.query.QueryResponseDocument response = qsm.query(queryDoc);
		Assert.assertTrue("Invalid response format: " + response.toString(), response.validate());
	}
	
	@Test
	public void queryCLSTreeValidResponse()
	{
		org.cancergrid.services.query.QueryDocument queryDoc = org.cancergrid.services.query.QueryDocument.Factory.newInstance();
		Query query = queryDoc.addNewQuery().addNewQuery();
		query.setResource("cgMDR-Classification-Tree");
		query.setTerm("http://iaaa.unizar.es/thesaurus/cancergrid-mdr-classification");
		org.cancergrid.services.query.QueryResponseDocument response = qsm.query(queryDoc);
		Assert.assertTrue("Invalid response format: " + response.toString(), response.validate());
	}
	
}
