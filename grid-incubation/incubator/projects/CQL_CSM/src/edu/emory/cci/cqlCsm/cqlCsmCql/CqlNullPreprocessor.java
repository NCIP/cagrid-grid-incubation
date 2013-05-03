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
package edu.emory.cci.cqlCsm.cqlCsmCql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * This implementation of CqlPreprocessor always returns that same query that it is passed.
 * @author Mark Grand
 */
public class CqlNullPreprocessor implements CqlPreprocessor {

    /* (non-Javadoc)
     * @see edu.emory.cci.cqlCsm.cqlCsmCql.CqlPreprocessor#preprocess(gov.nih.nci.cagrid.cqlquery.CQLQuery, java.lang.String)
     */
    public CQLQuery preprocess(CQLQuery query, String user) {
        return query;
    }
}
