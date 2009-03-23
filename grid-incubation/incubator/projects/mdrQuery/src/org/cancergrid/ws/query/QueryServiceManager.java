/**
 * Copyright (c) 2005-2008 CancerGrid Consortium <http://www.cancergrid.org/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or
 * substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS",
 * WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cancergrid.ws.query;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cancergrid.schema.config.Query_service;
import org.cancergrid.schema.query.Query;
import org.cancergrid.schema.result_set.ResultSet;
import org.cancergrid.ws.cache.CacheManager;
import org.cancergrid.ws.config.QueryServiceConfig;


/**
 * QueryServiceManager provides a single access point for invoking registered
 * CancerGrid query services.
 * 
 * @see org.cancergrid.ws.query.QueryOperation
 * @see org.cancergrid.ws.query.QueryService
 */

public class QueryServiceManager {
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
    private Map<String, Query_service> resources;

    /**
     * Currently active query services
     */
    private Map<String, QueryOperation> queryServices;
    
    private File transformTemplatesDir = null;


    /**
     * Initialise resource list with custom settings
     * 
     * @param config_file
     *            custom settings
     */
    public QueryServiceManager(File transformTemplatesDir, String config_file) {
        config = new QueryServiceConfig(config_file);
        this.transformTemplatesDir = transformTemplatesDir;
        loadWebServiceInterfaceConfig();
        try {
            cacheManager = new CacheManager(config);
        } catch (Exception e) {
            LOG.error("QueryServiceManager: " + e);
        }
    }
    
    /**
     * Initialise resource list with custom settings
     * 
     * @param config_file
     *            custom settings
     */
    public QueryServiceManager(File transformTemplatesDir, File config_file) {
        this.transformTemplatesDir = transformTemplatesDir;
        config = new QueryServiceConfig(config_file);
        loadWebServiceInterfaceConfig();
        try {
            cacheManager = new CacheManager(config);
        } catch (Exception e) {
            LOG.error("QueryServiceManager: " + e);
        }
    }

    public QueryServiceConfig getQueryServiceConfig(){
        return config;
    }

    /**
     * Provide a single point of entry for general query to vocabulary and
     * metadata services.
     */
    public ResultSet query(Query qr) {

        ResultSet result_set = new ResultSet();

        if (qr.getStartIndex() == null) {
            qr.setStartIndex(0);
        }

        if (qr.getNumResults() == null) {
            qr.setNumResults(10);
        }

        if (qr.getStartIndex() < 0 || qr.getNumResults() < 1) {
            result_set.setStatus("Invalid range to query.");
            return result_set;
        }

        if (!resources.containsKey(qr.getResource())) {
            result_set.setStatus("Resource not found");
            return result_set;
        }

        try {
            QueryOperation queryOp = getQueryService(qr.getResource());
            if (queryOp == null) {
                result_set.setStatus("Unable to query resource: " + qr.getResource());
                return result_set;
            }

            // Get attributes related to resource
            org.cancergrid.schema.config.Query_service qsInfo = resources.get(qr.getResource());

            // Check whether to enable caching
            boolean enableCache = false;
            String cache_provider = null;
            try {
                enableCache = qsInfo.isCache();
                if (enableCache) {
                    cache_provider = qsInfo.getCache_provider();
                }
            } catch (Exception e) {
                enableCache = false;
                cache_provider = null;
            }

            if (cache_provider != null) {
                String cache = cacheManager.checkCache(cache_provider, qr.getResource(), qr.getTerm() + "_"
                    + qr.getStartIndex() + "_" + qr.getNumResults());
                if (cache != null) {
                    result_set = (ResultSet) Utils.deserializeObject(new StringReader(cache), ResultSet.class);
                    result_set.setStatus("Query complete.");
                    return result_set;
                }
            }

            // Query resource
            ResultSet results = queryOp.query(qr);

            if (results == null) {
                result_set.setStatus("Result not found.");
                return result_set;
            }

            if (cache_provider != null && results != null) {
                StringWriter writer = new StringWriter();
                Utils.serializeObject(results, results.getTypeDesc().getXmlType(), writer);
                cacheManager.addCacheItem(cache_provider, qr.getResource(), qr.getTerm() + "_" + qr.getStartIndex()
                    + "_" + qr.getNumResults(), writer.toString());
            }
            results.setStatus("Query complete.");
            return results;

        } catch (Exception e) {
            result_set.setStatus("Query fail to complete.");
            LOG.error("query(): " + e);
            return result_set;
        }
    }


    public org.cancergrid.schema.config.Query_service getQueryServiceInfo(String resource) {
        return resources.get(resource);
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

            org.cancergrid.schema.config.Query_service qsInfo = resources.get(resource);

            // Resource not in active list
            if (!queryServices.containsKey(resource)) {
                Class<?> c = Class.forName(qsInfo.get_class());
                Constructor con = c.getConstructor(new Class[] {File.class});
                QueryService qs = (QueryService)con.newInstance(new Object [] {this.transformTemplatesDir});

                if (qsInfo.getServiceUrl() != null) {
                    qs.setServiceUrl(new java.net.URL(qsInfo.getServiceUrl()));
                    qs.initService();
                }

                if (qsInfo.getCategory() != null) {
                    org.cancergrid.schema.config.Category qMode = qsInfo.getCategory();
                    if (qMode.equals("CONCEPT"))
                        qs.setQueryMode(QueryService.QueryMode.CONCEPT);
                    else if (qMode.equals("CDE"))
                        qs.setQueryMode(QueryService.QueryMode.CDE);
                }

                if (qsInfo.getRequestSequence() != null) {
                    String transform = qsInfo.getRequestSequence().trim();
                    LOG.debug("Request sequence: " + transform);
                    if (!transform.equals("") && (transform.length() != 0)) {
                        java.util.List<String> seq = new java.util.ArrayList<String>(Arrays
                            .asList(transform.split(" ")));
                        qs.setRequestSequence(seq, config);
                    }
                }

                if (qsInfo.getDigestSequence() != null) {
                    String transform = qsInfo.getDigestSequence().trim();
                    if (!transform.equals("") && (transform.length() != 0)) {
                        java.util.List<String> seq = new java.util.ArrayList<String>(Arrays
                            .asList(transform.split(" ")));
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


    public static void main(String[] args) {
        try {
            QueryServiceManager manager = new QueryServiceManager(new File("etc/stylesheets"),new File("etc/config.xml"));
            Query_service info = manager.getQueryServiceInfo("caDSR-ObjectClass");
            Query query = new Query();
            query.setTerm("protein");
            //query.setId("CL047630");
            query.setResource(info.getName());
            ResultSet rs = manager.query(query);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
