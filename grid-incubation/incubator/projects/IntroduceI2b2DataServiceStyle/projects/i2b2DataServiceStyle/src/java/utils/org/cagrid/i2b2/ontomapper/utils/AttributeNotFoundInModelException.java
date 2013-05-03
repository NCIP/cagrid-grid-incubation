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
