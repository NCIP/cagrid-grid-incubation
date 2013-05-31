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
package edu.emory.cci.cqlCsm.cqlCsmCql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * Interface implemented by classes that pre-processes/re-writes/transforms CQL
 * queries.
 * 
 * @author Mark Grand
 */
public interface CqlPreprocessor {
    /**
     * Pre-process/re-write/transform the given CQL query and return the
     * transformed CQL query.
     * 
     * @param query The query to be pre-processed/re-written/transformed.
     * @param user The identity of the user this is for.
     * @return The pre-processed/re-written/transformed CQL query.
     */
    public CQLQuery preprocess(CQLQuery query, String user);
}
