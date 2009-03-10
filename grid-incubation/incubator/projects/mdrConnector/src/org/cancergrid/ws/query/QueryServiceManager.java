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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Templates;

import org.apache.log4j.Logger;
import org.cancergrid.schema.query.ResourcesDocument;
import org.cancergrid.schema.result_set.ResultSetDocument;
import org.cancergrid.services.query.QueryResponseDocument;
import org.cancergrid.services.query.QueryStringDocument;
import org.cancergrid.ws.cache.CacheManager;
import org.cancergrid.ws.config.QueryServiceConfig;
import org.cancergrid.ws.util.ChainTransformer;

/**
 * QueryServiceManager provides a single access point for invoking registered
 * CancerGrid query services.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cancergrid.org">CancerGrid Consortium</a>)
 * @version 1.0
 * 
 * @see org.cancergrid.ws.query.QueryOperation
 * @see org.cancergrid.ws.query.QueryService
 */

public class QueryServiceManager implements org.cancergrid.services.query.QueryServiceManagerSkeletonInterface 
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(QueryServiceManager.class);
	
	/**
	 * Utility class for handling caching
	 */
	protected CacheManager cacheManager;

	/**
	 * Configuration information for Query Service Manager
	 */
	private QueryServiceConfig config = null;
	
	/**
	 * Available query services listing from config file
	 */
	private Map<String, org.cancergrid.schema.config.QueryService> resources;

	/**
	 * Currently active query services
	 */
	private Map<String, QueryOperation> queryServices;
	
	/**
	 * Initialise resource list
	 * 
	 */
	public QueryServiceManager() {
		config = new QueryServiceConfig("config.xml");
		loadWebServiceInterfaceConfig();
		try {
			cacheManager = new CacheManager(config);
		} catch (Exception e) {
			LOG.error("QueryServiceManager: "+e);
		}
	}
	
	/**
	 * Initialise resource list with custom settings
	 * 
	 * @param config_file custom settings
	 */
	public QueryServiceManager(String config_file) {
		config = new QueryServiceConfig(config_file);
		loadWebServiceInterfaceConfig();
		try {
			cacheManager = new CacheManager(config);
		} catch (Exception e) {
			LOG.error("QueryServiceManager: "+e);
		}
	}

	/**
	 * List available query services
	 * 
	 * @return String array of available query services
	 */
    public org.cancergrid.services.query.ListResourcesAsXmlResponseDocument listResourcesAsXml() 
    {
    	try 
    	{
			Templates template = ChainTransformer.createTemplate(config.getResourceAsStream("stylesheets/config_filter.xsl"));
			String result = ChainTransformer.transform(config.getConfig().toString(), template);
			org.cancergrid.services.query.ListResourcesAsXmlResponseDocument response = org.cancergrid.services.query.ListResourcesAsXmlResponseDocument.Factory.newInstance();
			response.addNewListResourcesAsXmlResponse().addNewReturn().setResources(ResourcesDocument.Factory.parse(result).getResources());
			if (!response.validate())
			{
				throw new Exception("Response did not validate.");
			}
			return response;
		} catch (Exception e) {
			LOG.error("listResourcesAsXml(): "+e);
			return null;
		}
    }
    
    /**
	 * Make query on specified resource and only return specified result range
	 * 
	 * @param resource
	 *            query service to invoke
	 * @param term
	 *            search term
	 * @param startIndex
	 *            start index of query results to return
	 * @param numResults
	 *            number of query results to return
	 * @return output from specified query service
	 */
    @Deprecated
	public String queryRange(String resource, String term, int startIndex, int numResults) 
	{
		return queryBySource(resource, null, term, startIndex, numResults);
	}

	/**
	 * Make query on specified resource and only return specified result range
	 * 
	 * @param resource
	 *            query service to invoke
	 * @param src
	 *            restrict source
	 * @param term
	 *            search term
	 * @param startIndex
	 *            start index of query results to return
	 * @param numResults
	 *            number of query results to return
	 * @return output from specified query service
	 */
    @Deprecated
	public String queryBySource(String resource, String src, String term, int startIndex, int numResults) 
    {
    	org.cancergrid.services.query.QueryStringDocument qDoc = QueryStringDocument.Factory.newInstance();
    	org.cancergrid.schema.query.Query qs = qDoc.addNewQueryString().addNewQuery();
    	qs.setResource(resource);
   		qs.setSrc(src);
    	qs.setTerm(term);
    	qs.setStartIndex(startIndex);
    	qs.setNumResults(numResults);
    	org.cancergrid.services.query.QueryStringResponseDocument response = queryString(qDoc); 
    	if (response != null)
    		return response.toString(); 
		return null;		
	}
    
    /**
     * Provide a single point of entry for general query to vocabulary and metadata services. Return
     * results in string format.
     */

    public org.cancergrid.services.query.QueryStringResponseDocument queryString(org.cancergrid.services.query.QueryStringDocument query) 
    {
   		org.cancergrid.services.query.QueryDocument qomDoc = org.cancergrid.services.query.QueryDocument.Factory.newInstance();
   		org.cancergrid.services.query.QueryDocument.Query qom = qomDoc.addNewQuery();
   		qom.setQuery(query.getQueryString().getQuery());
   		org.cancergrid.services.query.QueryStringResponseDocument response = org.cancergrid.services.query.QueryStringResponseDocument.Factory.newInstance();
   		org.cancergrid.schema.result_set.ResultSetDocument results = org.cancergrid.schema.result_set.ResultSetDocument.Factory.newInstance();
   		QueryResponseDocument omResponseDoc = query(qomDoc);
   		QueryResponseDocument.QueryResponse omResponse = omResponseDoc.getQueryResponse();
   		org.cancergrid.schema.result_set.ResultSet result_set = omResponse.getReturn().getResultSet();
   		results.setResultSet(result_set);
   		response.addNewQueryStringResponse().setReturn(results.toString());
   		return response;
    }

    /**
     * Provide a single point of entry for general query to vocabulary and metadata services.
     */

    public org.cancergrid.services.query.QueryResponseDocument query(org.cancergrid.services.query.QueryDocument query) 
    {
    	org.cancergrid.schema.query.Query qr = query.getQuery().getQuery();
    	org.cancergrid.services.query.QueryResponseDocument response = org.cancergrid.services.query.QueryResponseDocument.Factory.newInstance();
    	org.cancergrid.schema.result_set.ResultSet result_set = response.addNewQueryResponse().addNewReturn().addNewResultSet();
    	
    	if (!qr.isSetStartIndex())
    	{
    		qr.setStartIndex(0);
    	}
    	
    	if (!qr.isSetNumResults())
    	{
    		qr.setNumResults(10);
    	}
    	
    	if (qr.getStartIndex() < 0 || qr.getNumResults() < 1) {
    		result_set.setStatus("Invalid range to query.");
			return response;
		}

		if (!resources.containsKey(qr.getResource())) {
			result_set.setStatus("Resource not found");
			return response;
		}
		
		try {
			QueryOperation queryOp = getQueryService(qr.getResource());
			if (queryOp == null) {
				result_set.setStatus("Unable to query resource: " + qr.getResource());
				return response;
			}

			// Get attributes related to resource
			org.cancergrid.schema.config.QueryService qsInfo = resources.get(qr.getResource());
			
			// Check whether to enable caching
			boolean enableCache = false;
			String cache_provider = null;
			try {
				enableCache = qsInfo.getCache();
				if (enableCache) 
				{
					cache_provider = qsInfo.getCacheProvider();
				}
			} catch (Exception e) {
				enableCache = false;
				cache_provider = null;
			}

			if (cache_provider != null) {
				String cache = cacheManager.checkCache(cache_provider, qr.getResource(), qr.getTerm() + "_" + qr.getStartIndex() + "_" + qr.getNumResults());
				if (cache != null) 
				{
					response.getQueryResponse().getReturn().setResultSet(org.cancergrid.schema.result_set.ResultSetDocument.Factory.parse(cache).getResultSet());
					response.getQueryResponse().getReturn().getResultSet().setStatus("Query complete.");
					return response;
				}
			}
			
			// Query resource
			ResultSetDocument results = queryOp.query(qr);
			
			if (results == null)
			{
				result_set.setStatus("Result not found.");
				return response;
			}

			if (cache_provider != null && results != null) 
			{
				cacheManager.addCacheItem(cache_provider, qr.getResource(), qr.getTerm() + "_" + qr.getStartIndex() + "_" + qr.getNumResults(), results.xmlText());
			}
			results.getResultSet().setStatus("Query complete.");
			response.getQueryResponse().getReturn().setResultSet(results.getResultSet());
//			if (!response.validate())
//			{
//				throw new Exception("Response did not validate.");
//			}
			return response;
		} catch (Exception e) 
		{
			result_set.setStatus("Query fail to complete.");
			LOG.error("query(): " + e);
			return response;
		}
    }

    /**
	 * List available query services
	 * 
	 * @return String array of available query services
	 */
    public org.cancergrid.services.query.ListResourcesResponseDocument listResources() {
    	Set<String> keys = resources.keySet();
		String[] resources = keys.toArray(new String[0]);
		Arrays.sort(resources);
		org.cancergrid.services.query.ListResourcesResponseDocument responseDoc = org.cancergrid.services.query.ListResourcesResponseDocument.Factory.newInstance();
		org.cancergrid.services.query.ListResourcesResponseDocument.ListResourcesResponse response = responseDoc.addNewListResourcesResponse();
		for (String r : resources)
		{
			response.addNewReturn().setStringValue(r);
		}
		return responseDoc;
    }
    
    /**
	 * Return specified query service. Initialize an instance of the query
	 * service if it is not in the active list. Return null if the named query
	 * service is not available.
	 * 
	 * @param resource
	 *            query service to return
	 * @return Return specified query service
	 */
	protected QueryOperation getQueryService(String resource) {
		QueryOperation queryOp = null;
		try {
			// No active resource
			if (queryServices == null) {
				queryServices = new Hashtable<String, QueryOperation>();
			}

			org.cancergrid.schema.config.QueryService qsInfo = resources.get(resource);
			
			// Resource not in active list
			if (!queryServices.containsKey(resource)) {
				Class<?> c = Class.forName(qsInfo.getClass1());
				QueryService qs = (QueryService) c.newInstance();
			
				if (qsInfo.getServiceUrl() != null) {
					qs.setServiceUrl(new java.net.URL(qsInfo.getServiceUrl()));
					qs.initService();
				}

				if (qsInfo.getCategory() != null) {
					org.cancergrid.schema.config.Category.Enum qMode = qsInfo.getCategory();
					if (qMode.equals("CONCEPT"))
						qs.setQueryMode(QueryService.QueryMode.CONCEPT);
					else if (qMode.equals("CDE"))
						qs.setQueryMode(QueryService.QueryMode.CDE);
				}
				
				if (qsInfo.getRequestSequence() != null) {
					String transform = qsInfo.getRequestSequence().trim();
					LOG.debug("Request sequence: "+transform);
					if (!transform.equals("") && (transform.length() != 0))
					{
						java.util.List<String> seq = new java.util.ArrayList<String>(Arrays.asList(transform.split(" ")));
						qs.setRequestSequence(seq, config);
					}
				}

				if (qsInfo.getDigestSequence() != null) {
					String transform = qsInfo.getDigestSequence().trim();
					if (!transform.equals("") && (transform.length() != 0))
					{
						java.util.List<String> seq = new java.util.ArrayList<String>(Arrays.asList(transform.split(" ")));
						qs.setDigestSequence(seq, config);
					}
				}
				
				queryServices.put(resource, qs);
			}

			// Get resource from active list
			queryOp = queryServices.get(resource);
			return queryOp;
		} catch (Exception e) {
			LOG.error("getQueryService" + e);
			return null;
		}
	}

	/**
	 * Load and initialise resources list
	 */
	private void loadWebServiceInterfaceConfig() {
		resources = config.listAvailableServices();
	}
}
