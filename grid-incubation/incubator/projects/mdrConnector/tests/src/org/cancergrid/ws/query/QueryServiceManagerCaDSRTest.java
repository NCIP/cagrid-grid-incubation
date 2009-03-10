package org.cancergrid.ws.query;

import org.cancergrid.schema.query.Query;
import org.cancergrid.ws.query.QueryServiceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryServiceManagerCaDSRTest
{
	private static org.cancergrid.ws.query.QueryServiceManager qsm;
	private static String caDSR ="caDSR";
	
	@Before
	public void createQueryServiceManager()
	{
		 qsm = new QueryServiceManager();
	}

            @Test
	public void querycaDSRValidResponse()
	{
		org.cancergrid.services.query.QueryDocument queryDoc = org.cancergrid.services.query.QueryDocument.Factory.newInstance();
		Query query = queryDoc.addNewQuery().addNewQuery();
		query.setResource(caDSR);
		query.setTerm("*blood*");
		query.setNumResults(5);
		org.cancergrid.services.query.QueryResponseDocument response = qsm.query(queryDoc);
		Assert.assertTrue("Invalid response format: " + response.toString(), response.validate());
	}

	@Test
	public void queryStringcaDSRValidResponse() throws Exception
	{
		org.cancergrid.services.query.QueryStringDocument queryDoc = org.cancergrid.services.query.QueryStringDocument.Factory.newInstance();
		Query query = queryDoc.addNewQueryString().addNewQuery();
		query.setResource(caDSR);
		query.setTerm("*cancer*");
		query.setNumResults(5);
		org.cancergrid.services.query.QueryStringResponseDocument response = qsm.queryString(queryDoc);
		org.cancergrid.schema.result_set.ResultSetDocument results = org.cancergrid.schema.result_set.ResultSetDocument.Factory.parse(response.getQueryStringResponse().getReturn());

		Assert.assertTrue("Invalid response format: " + response.toString(), results.validate());
	}
}
