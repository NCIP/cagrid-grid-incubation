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
package org.cagrid.cql.utilities;


public class QueryConversionException extends Exception {

    public QueryConversionException(String message) {
        super(message);
    }


    public QueryConversionException(Exception ex) {
        super(ex);
    }


    public QueryConversionException(String message, Exception ex) {
        super(message, ex);
    }
}
