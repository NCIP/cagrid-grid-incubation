/**
 * Copyright (c) 2005-2008 OpenMDR Consortium <http://www.cagrid.org/>
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

package org.cancergrid.ws.config;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cancergrid.schema.config.Cache_provider;
import org.cancergrid.schema.config.Config;
import org.cancergrid.schema.config.Query_service;


/**
 * Utility class for reading configuration information from an XML file
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a
 *         href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 */
public class QueryServiceConfig {
    /**
     * Log4J Logger
     */
    private static Logger LOG = Logger.getLogger(QueryServiceConfig.class);

    /**
     * Loaded configuration content
     */
    private org.cancergrid.schema.config.Config config = null;

    /**
     * List of query services information
     */
    private Map<String, Query_service> qrs = null;

    /**
     * List of cache providers information
     */
    private Map<String, Cache_provider> cps = null;


    /**
     * Constructor. Load configuration from given config file.
     * 
     * @param configFile
     *            config file
     */
    public QueryServiceConfig(String configFile) {
        loadConfig(configFile);
    }
    
    /**
     * Constructor. Load configuration from given config file.
     * 
     * @param configFile
     *            config file
     */
    public QueryServiceConfig(File configFile) {
        loadConfig(configFile);
    }

    /**
     * Initialise/load all configuration information
     */
    private void loadConfig(String configFile) {
        try {
            Reader reader = new InputStreamReader(getResourceAsStream(configFile));
            config = (Config) Utils.deserializeObject(reader, Config.class);
            loadQueryServiceInfo();
            loadCacheProviderInfo();
        } catch (Exception ioe) {
            LOG.error("Fail to read config file: " + configFile + "\n" + ioe);
            ioe.printStackTrace();
        }
    }
    
    /**
     * Initialise/load all configuration information
     */
    private void loadConfig(File configFile) {
        try {
            Reader reader = new FileReader(configFile);
            config = (Config) Utils.deserializeObject(reader, Config.class);
            loadQueryServiceInfo();
            loadCacheProviderInfo();
        } catch (Exception ioe) {
            LOG.error("Fail to read config file: " + configFile + "\n" + ioe);
            ioe.printStackTrace();
        }
    }


    /**
     * Load query services information from config file.
     */
    private void loadQueryServiceInfo() {
        qrs = new Hashtable<String, Query_service>();
        Query_service[] list = config.getResources().getQuery_service();
        for (int i = 0; i < list.length; i++) {
            qrs.put(list[i].getName(), list[i]);
        }
    }


    /**
     * Load cache providers information from config file.
     */
    private void loadCacheProviderInfo() {
        cps = new Hashtable<String, Cache_provider>();
        if (getConfig().getCache_config() != null) {
            Cache_provider[] list = config.getCache_config().getCache_provider();
            for (int i = 0; i < list.length; i++) {
                cps.put(list[i].getName(), list[i]);
            }
        }
    }


    /**
     * Return a list of available services declared in the configuration file
     * 
     * @return a list of available services
     */
    public java.util.Map<String, Query_service> listAvailableServices() {
        return qrs;
    }


    /**
     * Utility method to read resources from classpath
     * 
     * @param resource
     *            resource to read
     * @return InptStream to the given resource
     */
    public InputStream getResourceAsStream(String resource) {
        LOG.debug("Loading resources config from: " + resource);
        return getClass().getClassLoader().getResourceAsStream(resource);
    }


    /**
     * Get current configuration
     * 
     * @return current configuration
     */
    public Config getConfig() {
        return config;
    }


    /**
     * Get the information of a single named query service
     * 
     * @param name
     *            query service information to return
     * @return query service information
     */
    public Query_service getQueryServiceInfo(String name) {
        return qrs.get(name);
    }


    /**
     * Get the information of a single named cache provider
     * 
     * @param name
     *            cache provider information to return
     * @return cache provider information
     */
    public Cache_provider getCacheProviderInfo(String name) {
        return cps.get(name);
    }


}
