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
package org.cagrid.cql.translator.cql1.hql320ga;

/**
 * TypesInformationException
 * Thrown when a problem arises retrieving types information
 * 
 * @author David
 */
public class TypesInformationException extends Exception {

    public TypesInformationException(String message) {
        super(message);
    }
    
    
    public TypesInformationException(Exception cause) {
        super(cause);
    }
    
    
    public TypesInformationException(String message, Exception cause) {
        super(message, cause);
    }
}
