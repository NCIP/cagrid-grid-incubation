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
