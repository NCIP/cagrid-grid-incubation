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

package org.cancergrid.ws.cache;

/**
 * This is a Java interface which specify the caching operation signature required by 
 * implementation of cache providers for CancerGrid web service.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cancergrid.org">CancerGrid Consortium</a>)
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
