/**
 * Copyright (c) 2005-2008 CancerGrid Consortium <http://www.cancergrid.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,  
 * and to permit persons to whom the Software is furnished to do so, subject to the 
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package org.cancergrid.ws.query;

import org.apache.log4j.Logger;
import org.cancergrid.schema.query.Query;
import org.cancergrid.ws.util.HttpContentReader;

/**
 * Query service that works with web services the uses REST interface
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cancergrid.org">CancerGrid Consortium</a>)
 * @version 1.0
 * 
 * @see org.cancergrid.ws.query.QueryService
 */

public class RestQueryService extends QueryService
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(RestQueryService.class);
	
	public RestQueryService() 
	{
		super(QueryMode.CDE);
		initService();
	}

	public RestQueryService(java.net.URL wsURL) 
	{
		super(QueryMode.CDE);
		this.serviceUrl = wsURL;
		initService();
	}
	
	@Override
	public void initService() 
	{
		
	}
	
	@Override
	protected String executeQuery(Query query) 
	{
		try {
			//Construct the request to query REST service
			org.cancergrid.schema.query.QueryDocument qrDoc = org.cancergrid.schema.query.QueryDocument.Factory.newInstance();
			qrDoc.setQuery(query);
			LOG.debug("Query: " + qrDoc);
			String queryString = transform.applyTemplates(qrDoc.toString(), requestSequence);
			LOG.debug("Query String: " + queryString);
			return HttpContentReader.getHttpContent(queryString);
		} catch (Exception e)
		{
			LOG.error("RestQueryService.executeQuery: "+e);
			return null;
		}
	}
}
