package org.cagrid.cql.translator.cql1.hql320ga;

/**
 * QueryTranslationException
 * Thrown when an error occurs during query translation
 * 
 * @author David
 */
public class QueryTranslationException extends Exception {

    public QueryTranslationException(String message) {
        super(message);
    }
    
    
    public QueryTranslationException(Exception cause) {
        super(cause);
    }
    
    
    public QueryTranslationException(String message, Exception cause) {
        super(message, cause);
    }
}
