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
package org.cagrid.i2b2.ontomapper.utils;

/**
 * AttributeNotFoundInModelException
 * Indicates an attribute was not found in the data model
 * 
 * @author David
 */
public class AttributeNotFoundInModelException extends Exception {

    public AttributeNotFoundInModelException(String message) {
        super(message);
    }
    
    
    public AttributeNotFoundInModelException(Exception cause) {
        super(cause);
    }
    
    
    public AttributeNotFoundInModelException(String message, Exception cause) {
        super(message, cause);
    }
}
