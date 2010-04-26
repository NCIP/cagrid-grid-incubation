package org.cagrid.iso21090.model.tools;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLAssociationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLClassCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelUmlGeneralizationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.xmi.XMIConstants;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class ISOSupportDomainModelGenerator {
    
    // logical model package contains our data model
    public static final String LOGICAL_MODEL_PACKAGE_PREFIX = "Logical View.Logical Model.";
    
    // regex for package excludes
    public static final String DEFAULT_PACKAGE_EXCLUDE_REGEX_LEAVE_ISO = ".*?java.*,.*?[V|v]alue.?[D|d]omain.*";
    public static final String DEFAULT_PACKAGE_EXCLUDE_REGEX = ".*?java.*,.*?[V|v]alue.?[D|d]omain.*,.*?iso21090.*";
    public static final String ISO_PACKAGE_REGEX = ".*?iso21090.*";
    
    // cadsr ID tag values
    public static final String XMI_TAG_CADSR_DE_ID = "CADSR_DE_ID";
    public static final String XMI_TAG_CADSR_DE_VERSION = "CADSR_DE_VERSION";

    private XmiInOutHandler handler = null;
    private String projectShortName = null;
    private String projectLongName = null;
    private String projectDescription = null;
    private String projectVersion = null;
    private String packageExcludeRegex = null;
    private float attributeVersion = 1.0f;


    public ISOSupportDomainModelGenerator(HandlerEnum xmiType) {
        this.handler = XmiHandlerFactory.getXmiHandler(xmiType);
    }


    public String getProjectShortName() {
        return projectShortName;
    }


    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }


    public String getProjectLongName() {
        return projectLongName;
    }


    public void setProjectLongName(String projectLongName) {
        this.projectLongName = projectLongName;
    }


    public String getProjectDescription() {
        return projectDescription;
    }


    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }


    public String getPackageExcludeRegex() {
        return packageExcludeRegex;
    }


    public void setPackageExcludeRegex(String packageExcludeRegex) {
        this.packageExcludeRegex = packageExcludeRegex;
    }


    public float getAttributeVersion() {
        return attributeVersion;
    }


    public void setAttributeVersion(float attributeVersion) {
        this.attributeVersion = attributeVersion;
    }
    
    
    public DomainModel generateDomainModel(String xmiFilename) throws XmiException, IOException {
        Pattern excludePattern = Pattern.compile(DEFAULT_PACKAGE_EXCLUDE_REGEX);
        Pattern isoPattern = Pattern.compile(ISO_PACKAGE_REGEX);
        handler.load(xmiFilename);
        UMLModel umlModel = handler.getModel();
        DomainModel domain = new DomainModel();
        List<UMLClass> umlClasses = getAllClassesInModel(umlModel);
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLClass> domainClasses = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLClass>();
        for (UMLClass clazz : umlClasses) {
            // TODO: filter by package name exclude regex
            // CAVEAT: have to turn "attributes" that are ISO types into Associations.  Have fun!
            if (clazz.getPackage() == null) {
                System.out.println("Skipping class with null package: " + clazz.getName());
            } else {
                String fullPackageName = getFullPackageName(clazz);
                // filter out anything not in the logical model and matching the exclude regex
                if (!fullPackageName.startsWith(LOGICAL_MODEL_PACKAGE_PREFIX) 
                    || excludePattern.matcher(fullPackageName).matches()) {
                    System.out.println("Excluding class: " + fullPackageName + "." + clazz.getName());
                } else {
                    // basic class info
                    gov.nih.nci.cagrid.metadata.dataservice.UMLClass c = new gov.nih.nci.cagrid.metadata.dataservice.UMLClass();
                    String strippedPackageName = fullPackageName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    c.setPackageName(strippedPackageName);
                    c.setClassName(clazz.getName());
                    // ISO types aren't targetable; everything else is by default
                    c.setAllowableAsTarget(!isoPattern.matcher(strippedPackageName).matches());
                    // attributes
                    List<UMLAttribute> umlAttribs = clazz.getAttributes();
                    List<gov.nih.nci.cagrid.metadata.common.UMLAttribute> attribs = new ArrayList<gov.nih.nci.cagrid.metadata.common.UMLAttribute>(
                        umlAttribs.size());
                    for (UMLAttribute attrib : umlAttribs) {
                        gov.nih.nci.cagrid.metadata.common.UMLAttribute a = new gov.nih.nci.cagrid.metadata.common.UMLAttribute();
                        a.setName(attrib.getName());
                        a.setDataTypeName(attrib.getDatatype().getName());
                        // look at tagged values for the concept code info
                        annotateAttribute(a, attrib.getTaggedValues());
                        // keep the attribute
                        attribs.add(a);
                    }
                    c.setUmlAttributeCollection(new UMLClassUmlAttributeCollection(
                        attribs.toArray(new gov.nih.nci.cagrid.metadata.common.UMLAttribute[0])));
                    c.setId(String.valueOf(clazz.hashCode()));
                    domainClasses.add(c);
                }
            }
        }
        domain.setExposedUMLClassCollection(
            new DomainModelExposedUMLClassCollection(
                domainClasses.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLClass[0])));
        
        // generalizations
        List<UMLGeneralization> umlGeneralizations = umlModel.getGeneralizations();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization> generalizations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization>();
        for (UMLGeneralization gen : umlGeneralizations) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization g = 
                new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization();
            g.setSubClassReference(new UMLClassReference(String.valueOf(gen.getSubtype().hashCode())));
            g.setSuperClassReference(new UMLClassReference(String.valueOf(gen.getSupertype().hashCode())));
            generalizations.add(g);
        }
        domain.setUmlGeneralizationCollection(
            new DomainModelUmlGeneralizationCollection(
                generalizations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization[0])));
        
        // associations
        List<UMLAssociation> umlAssociations = umlModel.getAssociations();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation> domainAssociations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation>();
        for (UMLAssociation assoc : umlAssociations) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation a = 
                new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
            List<UMLAssociationEnd> ends = assoc.getAssociationEnds();
            a.setBidirectional(ends.size() == 2); // ?
            // TODO: association ends
            domainAssociations.add(a);
        }
        domain.setExposedUMLAssociationCollection(
            new DomainModelExposedUMLAssociationCollection(
                domainAssociations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation[0])));
        
        return domain;
    }
    
    
    private List<UMLClass> getAllClassesInModel(UMLModel model) {
        List<UMLClass> classes = new LinkedList<UMLClass>();
        List<UMLPackage> packages = new ArrayList<UMLPackage>();
        packages.addAll(model.getPackages());
        for (int i = 0; i < packages.size(); i++) {
            UMLPackage pack = packages.get(i);
            packages.addAll(pack.getPackages());
            classes.addAll(pack.getClasses());
        }
        return classes;
    }
    
    
    private String getFullPackageName(UMLClass clazz) {
        StringBuffer buf = new StringBuffer();
        UMLPackage pack = clazz.getPackage();
        boolean first = true;
        while (pack != null) {
            if (!first) {
                buf.insert(0, '.');   
            }
            first = false;
            buf.insert(0, pack.getName());
            pack = pack.getParent();
        }
        return buf.toString();
    }
    
    
    private void annotateAttribute(
        gov.nih.nci.cagrid.metadata.common.UMLAttribute currentAttribute, 
        Collection<UMLTaggedValue> taggedValues) {
        // if a CADSR_DE_ID is provided, prefer it over the
        // autogenerated EA ID.  EA IDs will all be converted to be 
        // negative values so they're out of the way of valid
        // caDSR IDs
        for (UMLTaggedValue taggedValue : taggedValues) {
            String name = taggedValue.getName();
            String value = taggedValue.getValue();
            if ("ea_guid".equals(name) 
                && currentAttribute.getPublicID() == 0) {
                long idValue = Long.MIN_VALUE + Math.abs(value.hashCode());
                currentAttribute.setPublicID(idValue);
            } else if (XMI_TAG_CADSR_DE_ID.equals(name)) {
                currentAttribute.setPublicID(Long.parseLong(value));
            } else if (XMI_TAG_CADSR_DE_VERSION.equals(name)) {
                currentAttribute.setVersion(Float.parseFloat(value));
            } else if (XMIConstants.XMI_TAG_DESCRIPTION.equals(name)) {
                // set the attribute's description
                currentAttribute.setDescription(value);
            } else if (name != null && 
                (name.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_CODE)
                || name.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_CODE)
                || name.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_PREFERRED_NAME)
                || name.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME)
                || (name.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION) 
                    && !name.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION_SOURCE))
                || (name.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION) 
                    && !name.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE)))) {
                // add semantic metadata to the UML attribute
                int order = 0;
                char c = name.charAt(name.length() - 1);
                if (Character.isDigit(c)) {
                    order = Integer.parseInt(String.valueOf(c));
                }
                
                SemanticMetadata[] currentMetadata = currentAttribute.getSemanticMetadata();
                List<SemanticMetadata> orderedMetadata = new ArrayList<SemanticMetadata>();
                Collections.addAll(orderedMetadata, currentMetadata != null ? currentMetadata : new SemanticMetadata[0]);
                
                // grow the ordered metadata to the appropriate size if needed
                int size = orderedMetadata.size();
                if (size <= order) {
                    for (int i = orderedMetadata.size(); i <= order; i++) {
                        orderedMetadata.add(new SemanticMetadata());
                    }
                }

                SemanticMetadata sm = orderedMetadata.get(order);
                if (name.indexOf(XMIConstants.XMI_TAG_CONCEPT_CODE) != -1) {
                    sm.setOrder(Integer.valueOf(order));
                    sm.setConceptCode(value);
                } else if (name.indexOf(XMIConstants.XMI_TAG_PREFERRED_NAME) != -1) {
                    sm.setConceptName(value);
                } else if (name.indexOf(XMIConstants.XMI_TAG_CONCEPT_DEFINITION) != -1) {
                    sm.setConceptDefinition(value);
                }
            }
        }
    }


    public static void main(String[] args) {
        ISOSupportDomainModelGenerator generator = new ISOSupportDomainModelGenerator(HandlerEnum.EADefault);
        try {
            System.out.println("generating model");
            DomainModel model = generator.generateDomainModel("test/resources/sdk.xmi");
            System.out.println("serializing");
            StringWriter writer = new StringWriter();
            MetadataUtils.serializeDomainModel(model, writer);
            System.out.println(writer.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
