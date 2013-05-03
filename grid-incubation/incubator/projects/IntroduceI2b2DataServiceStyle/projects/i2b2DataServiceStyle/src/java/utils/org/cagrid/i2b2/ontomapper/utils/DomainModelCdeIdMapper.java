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

import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.util.HashMap;
import java.util.Map;

/**
 * DomainModelConceptCodeMapper
 * Maps concept codes by reading a caGrid domain model
 * @author David
 *
 */
public class DomainModelCdeIdMapper implements CdeIdMapper {
    
    private Map<String, Map<String, Long>> classAttributeCdeIds = null;
    private String projectShortName = null;
    private String projectVersion = null;
    
    public DomainModelCdeIdMapper(DomainModel model) {
        classAttributeCdeIds = new HashMap<String, Map<String,Long>>();
        initialize(model);
    }
    
    
    private void initialize(DomainModel model) {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        for (UMLClass clazz : classes) {
            Map<String, Long> attributeIds = new HashMap<String, Long>();
            String fqClassName = getFullyQualifiedClassName(clazz);
            UMLAttribute[] attributes = clazz.getUmlAttributeCollection().getUMLAttribute();
            if (attributes != null) {
                for (UMLAttribute attrib : attributes) {
                    attributeIds.put(attrib.getName(), Long.valueOf(attrib.getPublicID()));
                }
            }
            classAttributeCdeIds.put(fqClassName, attributeIds);
        }
        projectShortName = model.getProjectShortName();
        projectVersion = model.getProjectVersion();
    }
    
    
    public String getProjectShortName() {
        return projectShortName;
    }
    
    
    public String getProjectVersion() {
        return projectVersion;
    }
    

    public Long getCdeIdForAttribute(String className, String attributeName) 
        throws ClassNotFoundInModelException, AttributeNotFoundInModelException {
        if (!classAttributeCdeIds.containsKey(className)) {
            throw new ClassNotFoundInModelException("Class " + className + 
            " not found in domain model!");
        }
        Map<String, Long> attributeCdes = classAttributeCdeIds.get(className);
        if (!attributeCdes.containsKey(attributeName)) {
            throw new AttributeNotFoundInModelException(
                "Attribute " + attributeName + " of class " +
                className + " not found in domain model!");
        }
        // CDE is allowed to be null, but attribute name has to exist
        Long cde = attributeCdes.get(attributeName);
        return cde;
    }


    public Map<String, Long> getCdeIdsForClass(String className) 
        throws ClassNotFoundInModelException {
        if (!classAttributeCdeIds.containsKey(className)) {
            throw new ClassNotFoundInModelException("Class " + className + 
            " not found in domain model!");
        }
        return classAttributeCdeIds.get(className);
    }

    
    private String getFullyQualifiedClassName(UMLClass clazz) {
        if (clazz.getPackageName() == null || clazz.getPackageName().length() == 0) {
            return clazz.getClassName();
        }
        return clazz.getPackageName() + "." + clazz.getClassName();
    }
}
