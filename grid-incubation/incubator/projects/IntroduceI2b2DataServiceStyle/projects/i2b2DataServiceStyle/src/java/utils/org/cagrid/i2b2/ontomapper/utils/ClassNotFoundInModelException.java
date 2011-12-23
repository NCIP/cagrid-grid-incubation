package org.cagrid.i2b2.ontomapper.utils;

/**
 * ClassNotFoundInModelException
 * Indicates a class was not found in the data model
 * 
 * @author David
 */
public class ClassNotFoundInModelException extends Exception {

    public ClassNotFoundInModelException(String message) {
        super(message);
    }
    
    
    public ClassNotFoundInModelException(Exception cause) {
        super(cause);
    }
    
    
    public ClassNotFoundInModelException(String message, Exception cause) {
        super(message, cause);
    }
}
