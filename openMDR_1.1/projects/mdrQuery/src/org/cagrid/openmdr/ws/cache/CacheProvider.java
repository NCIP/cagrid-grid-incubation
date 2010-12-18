/**
 * Copyright (c) 2005-2008 OpenMDR Consortium <http://www.cagrid.org/>
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
