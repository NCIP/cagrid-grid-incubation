package org.cagrid.i2b2.ontomapper.utils;

import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DomainModelConceptCodeMapper
 * Maps concept codes by reading a caGrid domain model
 * @author David
 *
 */
public class DomainModelConceptCodeMapper implements ConceptCodeMapper {
    
    private Map<String, List<String>> classConceptCodes = null;
    private Map<String, List<String>> attributeConceptCodes = null;
    
    public DomainModelConceptCodeMapper(DomainModel model) {
        classConceptCodes = new HashMap<String, List<String>>();
        attributeConceptCodes = new HashMap<String, List<String>>();
        initialize(model);
    }
    
    
    private void initialize(DomainModel model) {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        for (UMLClass clazz : classes) {
            String fqClassName = getFullyQualifiedClassName(clazz);
            List<String> classCodes = new LinkedList<String>();
            SemanticMetadata[] clazzSemantics = clazz.getSemanticMetadata();
            if (clazzSemantics != null) {
                for (SemanticMetadata md : clazzSemantics) {
                    classCodes.add(md.getConceptCode());
                }
            }
            classConceptCodes.put(fqClassName, classCodes);
            UMLAttribute[] attributes = clazz.getUmlAttributeCollection().getUMLAttribute();
            if (attributes != null) {
                for (UMLAttribute attrib : attributes) {
                    String fqAttribName = getFullyQualifiedAttributeName(clazz, attrib);
                    List<String> attributeCodes = new LinkedList<String>();
                    SemanticMetadata[] attribSemantics = attrib.getSemanticMetadata();
                    if (attribSemantics != null) {
                        for (SemanticMetadata md : attribSemantics) {
                            attributeCodes.add(md.getConceptCode());
                        }
                    }
                    attributeConceptCodes.put(fqAttribName, attributeCodes);
                }
            }
        }
    }
    

    public List<String> getConceptCodesForAttribute(String className, String attributeName) 
        throws ClassNotFoundInModelException, AttributeNotFoundInModelException {
        List<String> conceptCodes = attributeConceptCodes.get(className + "." + attributeName);
        if (conceptCodes == null) {
            throw new AttributeNotFoundInModelException(
                "Attribute " + attributeName + " of class " +
                className + " not found in domain model!");
        }
        return conceptCodes;
    }


    public List<String> getConceptCodesForClass(String className) 
        throws ClassNotFoundInModelException {
        List<String> conceptCodes = classConceptCodes.get(className);
        if (conceptCodes == null) {
            throw new ClassNotFoundInModelException("Class " + className + 
                " not found in domain model!");
        }
        return conceptCodes;
    }

    
    private String getFullyQualifiedClassName(UMLClass clazz) {
        if (clazz.getPackageName() == null || clazz.getPackageName().length() == 0) {
            return clazz.getClassName();
        }
        return clazz.getPackageName() + "." + clazz.getClassName();
    }
    
    
    private String getFullyQualifiedAttributeName(UMLClass clazz, UMLAttribute attrib) {
        return getFullyQualifiedClassName(clazz) + "." + attrib.getName();
    }
}
