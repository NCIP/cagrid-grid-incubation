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

    /**
     * Gets a list of concept codes from the model
     * which are associated with the given class
     * @param className
     *      The name of the class to get concept codes for
     * @return
     *      The list of concept codes.  <note>This list may be empty, but <b>NOT NULL</b></note>
     * @throws ClassNotFoundInModelException
     *      Thrown if no class by the className is found in the model.
     */
    public List<String> getConceptCodesForClass(String className) 
        throws ClassNotFoundInModelException;
    
    
    /**
     * Gets a list of concept codes from the model
     * which are associated with the given attribute 
     * of the given class
     * 
     * @param className
     *      The name of the class which contains the attribute
     * @param attributeName
     *      The name of the attribute to get concept codes for
     * @return
     *      The list of concept codes.  <note>This list may be empty, but <b>NOT NULL</b></note>
     * @throws ClassNotFoundInModelException
     *      Thrown if no class by the className is found in the model.
     * @throws AttributeNotFoundInModelException
     *      Thrown if no attribute by the attributeName is found in the 
     *      given class in the model.
     */
    public List<String> getConceptCodesForAttribute(String className, String attributeName) 
        throws ClassNotFoundInModelException, AttributeNotFoundInModelException;
}
