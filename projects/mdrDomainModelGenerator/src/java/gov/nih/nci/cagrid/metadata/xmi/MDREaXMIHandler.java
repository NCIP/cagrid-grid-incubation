package gov.nih.nci.cagrid.metadata.xmi;


import gov.nih.nci.cagrid.metadata.MDRUtils;

import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cancergrid.schema.result_set.ConceptCollection;
import org.cancergrid.schema.result_set.ConceptRef;
import org.cancergrid.schema.result_set.DataElement;
import org.cancergrid.schema.result_set.ValidValue;
import org.cancergrid.schema.result_set.Values;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
  *  XMIHandler
  *  SAX handler for caCORE SDK 4.0 EA XMI that handles Annotations by MDR -> Domain Model
  *  
  * @author Rakesh Dhaval
  * @author Patrick McConnell
  * @author David Ervin
  * 
  * @created Oct 22, 2007 10:26:25 AM
  * @version $Id: Sdk4EaXMIHandler.java,v 1.5.2.2 2009/02/05 18:58:07 dervin Exp $
 */
class MDREaXMIHandler extends BaseXMIHandler {
    private static final Log LOG = LogFactory.getLog(MDREaXMIHandler.class);   
    
    // state variables
    private UMLAssociationEdge edge;
    private boolean sourceNavigable = false;
    private boolean targetNavigable = false;
    private String pkg = "";
    private boolean handlingAttribute = false;
    private UMLAttribute currentAttribute = null;

    public MDREaXMIHandler(XMIParser parser) {
        super(parser);
    }

    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            int index = pkg.lastIndexOf('.');
            if (index == -1) {
                pkg = "";
            } else {
                pkg = pkg.substring(0, index);
            }
        } else if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
            UMLClass cl = getLastClass();
            cl.setUmlAttributeCollection(
                new UMLClassUmlAttributeCollection(getAttributes()));
            clearAttributeList();
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
            UMLAssociation assoc = getLastAssociation();
            if (sourceNavigable && !targetNavigable) {
                UMLAssociationEdge assocEdge = assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge();
                assoc.getSourceUMLAssociationEdge().setUMLAssociationEdge(
                    assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge());
                assoc.getTargetUMLAssociationEdge().setUMLAssociationEdge(assocEdge);
            }
            assoc.setBidirectional(sourceNavigable && targetNavigable);
        } else if (qName.equals(XMIConstants.XMI_UML_ATTRIBUTE)) {
            addAttribute(currentAttribute);
            currentAttribute = null;
            handlingAttribute = false;
        }

        clearChars();
    }


    @Override
    public void startElement(
        String uri, String localName, String qName, Attributes atts) throws SAXException {
        clearChars();

        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            handlePackage(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
            handleClass(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_ATTRIBUTE)) {
            handleAttribute(atts);
        } else if (qName.equals(Sdk4EaXMIConstants.XMI_UML_CLASSIFIER)) {
            handleClassifier(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
            // start the new association
            UMLAssociation ass = new UMLAssociation();
            addAssociation(ass);
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION_END)) {
            handleAssociationEnd(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_MULTIPLICITY_RANGE) && edge != null) {
            handleMultiplicity(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_GENERALIZATION)) {
            handleGeneralization(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_TAGGED_VALUE)) {
            handleTag(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_DATA_TYPE)) {
            handleDataType(atts);
        } else if (qName.equals(XMIConstants.XMI_FOUNDATION_CORE_CLASSIFIER)) {
            if (!handlingAttribute) {
                LOG.info("Ignoring " + XMIConstants.XMI_FOUNDATION_CORE_CLASSIFIER);
            } else {
                getLastAttribute().setDataTypeName(atts.getValue(XMIConstants.XMI_IDREF));
            }
        }
    }

    
    // ------------------
    // XMI type handlers
    // ------------------
    
    
    private void handleDataType(Attributes atts) {
        getTypeTable().put(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE), 
            atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
    }
    
    
    private void handleGeneralization(Attributes atts) {
        UMLGeneralization gen = new UMLGeneralization();
        String subId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_CHILD);
        String superId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_PARENT);
        if (subId == null) {
            subId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_SUBTYPE);
        }
        if (superId == null) {
            superId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_SUPERTYPE);
        }
        gen.setSubClassReference(new UMLClassReference(subId));
        gen.setSuperClassReference(new UMLClassReference(superId));
        addGeneralization(gen);
    }
    
    
    private void handleMultiplicity(Attributes atts) {
        edge.setMinCardinality(Integer.parseInt(
            atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_LOWER)));
        edge.setMaxCardinality(Integer.parseInt(
            atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_UPPER)));
    }
    
    
    private void handleAssociationEnd(Attributes atts) {
        // get the most recently found association
        UMLAssociation assoc = getLastAssociation();
        boolean isNavigable = "true".equals(atts.getValue(XMIConstants.XMI_UML_ASSOCIATION_IS_NAVIGABLE));

        edge = new UMLAssociationEdge();
        if (assoc.getSourceUMLAssociationEdge() == null) {
            assoc.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(edge));
            sourceNavigable = isNavigable;
        } else {
            assoc.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(edge));
            targetNavigable = isNavigable;
        }
        edge.setRoleName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        edge.setUMLClassReference(new UMLClassReference(atts.getValue(XMIConstants.XMI_TYPE_ATTRIBUTE)));
    }
    
    
    private void handleAttribute(Attributes atts) {
        handlingAttribute = true;
        currentAttribute = new UMLAttribute();
        currentAttribute.setName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        currentAttribute.setVersion(getParser().attributeVersion);
    }
    
    
    private void handleClassifier(Attributes atts) {
        if (handlingAttribute) {
            String typeIdRef = atts.getValue(XMIConstants.XMI_IDREF);
            currentAttribute.setDataTypeName(typeIdRef);
        }
    }
       
    private void handleClass(Attributes atts) {
        UMLClass cl = new UMLClass();
        cl.setClassName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        cl.setId(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE));
        cl.setPackageName(pkg);
        cl.setProjectName(getParser().projectShortName);
        cl.setProjectVersion(getParser().projectVersion);
        addClass(cl);
    }
 
    private void handlePackage(Attributes atts) {
        String name = atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        if (!name.equals(XMIConstants.XMI_LOGICAL_VIEW) 
            && !name.equals(XMIConstants.XMI_LOGICAL_MODEL)
            && !name.equals(XMIConstants.XMI_DATA_MODEL)) {
            if (!pkg.equals("")) {
                pkg += ".";
            }
            pkg += atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        }
    }
    
    private void handleAttributeTag(String tag, String value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling tag for attribute");
            LOG.debug("\ttag=" + tag);
            LOG.debug("\tvalue=" + value);
        }
        if (handlingAttribute && "ea_guid".equals(tag)) {
            currentAttribute.setPublicID(value.hashCode());
        } else if (tag.equals(XMIConstants.XMI_TAG_DESCRIPTION)) {
            // set the attribute's description
            LOG.debug("Setting attribute's description: " + value);
            currentAttribute.setDescription(value);
        } else if (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_PREFERRED_NAME)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME)
            || (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION_SOURCE))
            || (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE))) {
        	LOG.debug("\tConceptCode tag original:"+tag+"; Public Id original: "+ String.valueOf(currentAttribute.getPublicID())+"; Value original: "+ value);            
        	addSemanticMetadata(tag, String.valueOf(currentAttribute.getPublicID()), value);
        } else if (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CDE_REF)) {
        	ArrayList<String> conceptArr = new ArrayList<String>();
    		ArrayList<String> valueDomainConceptArr = new ArrayList<String>();
        	MDRUtils mdrutils = new MDRUtils(value,"caDSR");
        	DataElement[] de=mdrutils.getDataElements();
			
			if (de!=null)
			{
			/*
				for (int i=0;i<de.length;i++)
				{
					/*
					ConceptCollection cc = de[i].getConceptCollection();
					ConceptRef[] crf = cc.getConceptRef();
					for (int j = 0; j < crf.length; j++) 
					{
						addSemanticMetadata(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_CODE, Long.toString(currentAttribute.getPublicID()), crf[j].getId(),j);
			        	addSemanticMetadata(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_PREFERRED_NAME, Long.toString(currentAttribute.getPublicID()), crf[j].getName(),j);
			        	addSemanticMetadata(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION, Long.toString(currentAttribute.getPublicID()), crf[j].getDefinition(),j);
			        	conceptArr.add(crf[j].getId());
					}
					*/						
				}
				
				//LOG.debug("\tPropertyConceptCodes=" + conceptArr);
				/*
				for (int i = 0; i < de.length; i++) {
					Values v = de[i].getValues();
					
					if (v.getEnumerated()!=null)
					{
						if (v.getEnumerated().getValidValue().length > 0) 
						{
							ValidValue[] vv = v.getEnumerated().getValidValue();
							for (int k = 0; k < vv.length; k++) {
							ConceptCollection cc = vv[k]
									.getConceptCollection();
							ConceptRef[] crf = cc.getConceptRef();
							for (int j = 0; j < crf.length; j++) 
							{
								valueDomainConceptArr.add(crf[j].getId());
							}
						}
					}
						System.out.println("ConceptCollection for Value Domain(ObjectClassPropertyConceptCodes): "+valueDomainConceptArr); 
					}
					else
						  System.out.println("No ObjectClassPropertyConceptCodes found!"); 
						  
				}
				*/
			}
    }
    
    
    private void handleClassTag(String tag, String value, String modelElement) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling tag for class");
            LOG.debug("\ttag=" + tag);
            LOG.debug("\tvalue=" + value);
            LOG.debug("\tmodelElement=" + modelElement);
        }
        if (tag.equals(XMIConstants.XMI_TAG_DESCRIPTION)) {
            // class description
            UMLClass refedClass = getClassById(modelElement);
            if (refedClass != null) {
                refedClass.setDescription(value);
            }
        } 
        else if (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_PREFERRED_NAME)
            || tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_PREFERRED_NAME)
            || (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION_SOURCE))
            || (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION_SOURCE))) {
            addSemanticMetadata(tag, modelElement, value);
        }
    }
    private void handleTag(Attributes atts) {
        String tag = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_TAG);
        String modelElement = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_MODEL_ELEMENT);
        String value = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_VALUE);
      if (handlingAttribute) {
            handleAttributeTag(tag, value);
        } else {
            handleClassTag(tag, value, modelElement);
        }
    }  
    //---------------------
    // general helpers
    //---------------------
    

    private int getSemanticMetadataOrder(String tag) {
        char c = tag.charAt(tag.length() - 1);
        if (Character.isDigit(c)) {
            return Integer.parseInt(String.valueOf(c));
        }
        return 0;
    }


    private void addSemanticMetadata(String tag, String elementId, String value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding semantic metadata");
            UMLClass clazz = getClassById(elementId);
            UMLAttribute attr = getAttributeById(elementId);
            if (clazz != null) {
                LOG.debug("\tReferenced Class: " + clazz.getPackageName() 
                    + "." + clazz.getClassName());
            } else if (attr != null) {
                LOG.debug("\tReferenced Attribute: " + attr.getName() + " : " 
                    + attr.getPublicID() + " : " + attr.getDataTypeName());
            }
        }
        int order = getSemanticMetadataOrder(tag);

        List<SemanticMetadata> smList = getSemanticMetadataTable().get(elementId);
        if (smList == null) {
            getSemanticMetadataTable().put(elementId, 
                smList = new ArrayList<SemanticMetadata>(2));
        }

        int size = smList.size();
        if (size <= order) {
            for (int i = smList.size(); i <= order; i++) {
                smList.add(new SemanticMetadata());
            }
        }

        SemanticMetadata sm = smList.get(order);
        if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_CODE) != -1) {
            sm.setOrder(Integer.valueOf(order));
            sm.setConceptCode(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_PREFERRED_NAME) != -1) {
            sm.setConceptName(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_DEFINITION) != -1) {
            sm.setConceptDefinition(value);
        }
    }
  

    private void addSemanticMetadata(String tag, String elementId, String value, int orderValue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding semantic metadata for MDR DataElements");
            UMLClass clazz = getClassById(elementId);
            UMLAttribute attr = getAttributeById(elementId);
            if (clazz != null) {
                LOG.debug("\tReferenced Class: " + clazz.getPackageName() 
                    + "." + clazz.getClassName());
            } else if (attr != null) {
                LOG.debug("\tReferenced Attribute: " + attr.getName() + " : " 
                    + attr.getPublicID() + " : " + attr.getDataTypeName());
            }
        }
        int order = orderValue;

        List<SemanticMetadata> smList = getSemanticMetadataTable().get(elementId);
        if (smList == null) {
            getSemanticMetadataTable().put(elementId, 
                smList = new ArrayList<SemanticMetadata>(2));
        }

        int size = smList.size();
        if (size <= order) {
            for (int i = smList.size(); i <= order; i++) {
                smList.add(new SemanticMetadata());
            }
        }

        SemanticMetadata sm = smList.get(order);
        if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_CODE) != -1) {
            sm.setOrder(Integer.valueOf(order));
            sm.setConceptCode(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_PREFERRED_NAME) != -1) {
            sm.setConceptName(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_DEFINITION) != -1) {
            sm.setConceptDefinition(value);
        }
    }
    
}