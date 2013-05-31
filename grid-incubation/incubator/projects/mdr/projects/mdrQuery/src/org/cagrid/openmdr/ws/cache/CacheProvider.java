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

import java.util.Date;

/**
 * This class implements CacheOperation and features common to all cache providers 
 * for OpenMDR web service.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 * 
 * @see CacheOperation
 */
public abstract class CacheProvider implements CacheOperation
{
	/**
	 * How long the cache item is considered valid
	 */
	private long cachePeriod = -1; //never expires
	
	public long getCachePeriod() {
		return cachePeriod;
	}

	public void setCachePeriod(long minutes) {
		this.cachePeriod = minutes;
	}
	
	public boolean isCacheItemExists(String cache_collection_id, String cache_item_id)
	{
		try
		{
			String cacheItem = getCacheData(cache_collection_id, cache_item_id);
			return (cacheItem == null);
		} catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Utility method to check whether specified cache item date has expired.
	 * 
	 * @param cacheDate cache date to check
	 * @return true if expired else false
	 */
	protected boolean expired(Date cacheDate)
	{
		if (cachePeriod == -1)
		{
			return false;
		}
		
		Date now = new Date(); 
		long hours = (now.getTime() - cacheDate.getTime())/(60*1000);
		return (hours > cachePeriod);
	}
	
}
