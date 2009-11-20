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
