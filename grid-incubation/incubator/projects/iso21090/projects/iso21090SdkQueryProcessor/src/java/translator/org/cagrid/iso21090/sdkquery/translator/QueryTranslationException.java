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
package org.cagrid.iso21090.sdkquery.translator;

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
