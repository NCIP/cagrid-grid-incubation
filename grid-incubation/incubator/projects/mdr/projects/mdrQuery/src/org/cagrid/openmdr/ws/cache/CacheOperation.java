/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
/**
 */

package org.cagrid.openmdr.ws.cache;

/**
 * This is a Java interface which specify the caching operation signature required by 
 * implementation of cache providers for OpenMDR web service.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 */
public interface CacheOperation 
{
	
	/**
	 * Store/update cache item
	 * 
	 * @param cache_collection_id the location/collection to search
	 * @param cache_item_id id of the cache item
	 * @param content content to cache
	 * @param overwrite flag whether to overwrite existing cache item with same cache_item_id
	 */
	public void storeCacheData(String cache_collection_id, String cache_item_id, String content, boolean overwrite);
	
	/**
	 * Get the specified cache item
	 * 
	 * @param cache_collection_id the location/collection to search
	 * @param cache_item_id id of the cache item
	 * @return content of the cache item
	 */
	public String getCacheData(String cache_collection_id, String cache_item_id);
	
	/**
	 * Check if specified cache item is available in cache
	 * 
	 * @param cache_collection_id the location/collection to search
	 * @param cache_item_id id of the cache item
	 * @return if found return true else false
	 */
	public boolean isCacheItemExists(String cache_collection_id, String cache_item_id);
	
	/**
	 * Set how long the data should be cache
	 * @param minutes how long data should be cache in minutes
	 */
	public void setCachePeriod(long minutes);
	
	/**
	 * Get how long the data is cache.
	 */
	public long getCachePeriod();
}
