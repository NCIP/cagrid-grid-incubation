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
/**
 */

package org.cagrid.openmdr.ws.cache;

import java.util.*;

import org.apache.log4j.Logger;
import org.cagrid.openmdr.ws.config.QueryServiceConfig;

/**
 * Utility class for managing cache providers
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 *
 */
public class CacheManager 
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(CacheManager.class);
			
	/**
	 * Collection of cache providers
	 */
	protected Map<String, CacheProvider> cacheProviders;
	
	/**
	 * Configuration info
	 */
	protected QueryServiceConfig config = null;
	
	/**
	 * Initialize
	 */
	public CacheManager(QueryServiceConfig config)
	{
		this.config = config;
		cacheProviders = new Hashtable<String, CacheProvider>();
	}
	
	/**
	 * Check whether the query result for specified search term is available in cache 
	 * @param cache_collection_id id of the collection to search
	 * @param cache_item_id id of the cache item to search
	 * @return cache data from matching term or null is not found
	 */
	public String checkCache(String cache_provider, String cache_collection_id, String cache_item_id)
	{
		CacheProvider cp = getProvider(cache_provider);
		if (cp == null)
		{
			return null;
		}
		
		return cp.getCacheData(cache_collection_id, cache_item_id);
	}
	
	/**
	 * Insert/update cache data 
	 * @param cache_collection_id id of the collection to use
	 * @param cache_item_id id of the cache item to use
	 * @param content content to cache
	 */
	public void addCacheItem(String cache_provider, String cache_collection_id, String cache_item_id, String content)
	{
		CacheProvider cp = getProvider(cache_provider);
		if (cp == null)
		{
			return;
		}
		//content.replace("<?xml version=\"1.0\" ?>", "");
		if (cp.isCacheItemExists(cache_collection_id, cache_item_id))
		{
			cp.storeCacheData(cache_collection_id, cache_item_id, content, true);
		} else 
		{
			cp.storeCacheData(cache_collection_id, cache_item_id, content, false);
		}
	}
	
	/**
	 * Get the named cache provider. If not available in current collection, 
	 * try to create a new instance by getting the classname from configuration 
	 * file.
	 * 
	 * @param name name of the cache provider.
	 * @return the named cache provider, if found.
	 */
	protected CacheProvider getProvider(String name)
	{
		if (!cacheProviders.containsKey(name))
		{
			try {
				CacheProvider cp = (CacheProvider)Class.forName(config.getCacheProviderInfo(name).get_class()).newInstance();
				long cachePeriod = config.getCacheProviderInfo(name).getCache_period(); 
				cp.setCachePeriod(cachePeriod);
				cacheProviders.put(name, cp);
			} catch (Exception e) 
			{
				LOG.error("Fail to find provider: "+e);
				return null;
			}
		}
		return cacheProviders.get(name);
	}
}
