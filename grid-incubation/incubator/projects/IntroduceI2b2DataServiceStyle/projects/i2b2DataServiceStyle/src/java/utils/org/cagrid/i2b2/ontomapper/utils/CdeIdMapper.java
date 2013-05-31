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

import java.util.Map;

/**
 * CdeIdMapper
 * 
 * Maps Java class names + attributes from a domain
 * model to CDE id Values
 * 
 * @author David
 */
public interface CdeIdMapper {
    
    public String getProjectShortName();
    
    
    public String getProjectVersion();
    

    /**
     * Gets a map of attribute names to CDE ids from the model
     * which are associated with the given class
     * 
     * @param className
     *      The name of the class to get concept codes for
     * @return
     *      The map of attributes and CDEs. <note>This may be empty, but <b>NOT NULL</b></note>
     * @throws ClassNotFoundInModelException
     *      Thrown if no class by the className is found in the model.
     */
    public Map<String, Long> getCdeIdsForClass(String className) 
        throws ClassNotFoundInModelException;
    
    
    /**
     * Gets the CDE from the model which is associated with the 
     * given attribute of the given class
     * 
     * @param className
     *      The name of the class which contains the attribute
     * @param attributeName
     *      The name of the attribute to get concept codes for
     * @return
     *      The CDE id.  <note>This may be null if no CDE id is found</note>
     * @throws ClassNotFoundInModelException
     *      Thrown if no class by the className is found in the model.
     * @throws AttributeNotFoundInModelException
     *      Thrown if no attribute by the attributeName is found in the 
     *      given class in the model.
     */
    public Long getCdeIdForAttribute(String className, String attributeName) 
        throws ClassNotFoundInModelException, AttributeNotFoundInModelException;
}
