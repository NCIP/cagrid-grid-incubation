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
 */

package org.cagrid.openmdr.ws.query;

import org.cancergrid.schema.query.Query;
import org.cancergrid.schema.result_set.ResultSet;

/**
 * This is a Java interface which specify the Query Operation signature common
 * to all implementation of OpenMDR web service interface for querying vocabulary
 * and meta-data services.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 */

public interface QueryOperation
{
	/**
	 * This operation query metadata
	 * @param query information required to generate a query request to terminology/metadata services
	 * @return query results
	 */
	public ResultSet query(Query query);
}
