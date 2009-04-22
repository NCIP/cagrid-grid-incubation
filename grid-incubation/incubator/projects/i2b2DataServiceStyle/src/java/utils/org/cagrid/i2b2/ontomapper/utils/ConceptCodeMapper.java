package org.cagrid.i2b2.ontomapper.utils;

import java.util.List;

/**
 * ConceptCodeMapper
 * Maps Java class names and attributes from a domain
 * model to concept codes.
 * 
 * @author David
 */
public interface ConceptCodeMapper {

    public List<String> getConceptCodesForClass(String className) 
        throws ClassNotFoundInModelException;
    
    
    public List<String> getConceptCodesForAttribute(String className, String attributeName) 
        throws ClassNotFoundInModelException, AttributeNotFoundInModelException;
}
