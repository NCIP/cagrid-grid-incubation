package org.cagrid.iso21090.model.tools;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLAssociationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLClassCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelUmlGeneralizationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.xmi.XMIConstants;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociable;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLInterface;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
    
    private static Log LOG = LogFactory.getLog(ISOSupportDomainModelGenerator.class);

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
        return packageExcludeRegex != null ? packageExcludeRegex : DEFAULT_PACKAGE_EXCLUDE_REGEX_LEAVE_ISO;
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
        Pattern isoPattern = Pattern.compile(ISO_PACKAGE_REGEX);
        LOG.debug("Loading XMI from " + xmiFilename);
        handler.load(xmiFilename);
        LOG.debug("Loading complete, parsing model");
        UMLModel umlModel = handler.getModel();
        DomainModel domain = new DomainModel();
        domain.setProjectShortName(getProjectShortName());
        domain.setProjectVersion(getProjectVersion());
        domain.setProjectLongName(getProjectLongName());
        domain.setProjectDescription(getProjectDescription());
        List<UMLClass> umlClasses = getAllClassesInModel(umlModel);
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLClass> domainClasses = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLClass>();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation> domainAssociations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation>();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization> generalizations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization>();
        for (UMLClass clazz : umlClasses) {
            if (clazz.getPackage() == null) {
                LOG.debug("Skipping class with null package: " + clazz.getName());
            } else {
                String fullPackageName = getFullPackageName(clazz);
                // filter out anything not in the logical model and matching the exclude regex
                if (shouldExcludePackage(fullPackageName)) {
                    LOG.debug("Excluding class " + fullPackageName + "." + clazz.getName());
                    System.out.println("Excluding class " + fullPackageName + "." + clazz.getName());
                } else {
                    // basic class info
                    String strippedPackageName = fullPackageName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    boolean isIsoClass = !isoPattern.matcher(strippedPackageName).matches();
                    String shortClassName = isIsoClass ? cleanUpIsoClassName(clazz.getName()) : clazz.getName();
                    LOG.debug("Creating model class " + strippedPackageName + "." + shortClassName);
                    System.out.println("Creating model class " + strippedPackageName + "." + shortClassName);
                    gov.nih.nci.cagrid.metadata.dataservice.UMLClass c = new gov.nih.nci.cagrid.metadata.dataservice.UMLClass();
                    c.setPackageName(strippedPackageName);
                    c.setClassName(shortClassName);
                    c.setId(String.valueOf(clazz.hashCode()));
                    // ISO types aren't targetable; everything else is by default
                    LOG.debug("Class " + (isIsoClass ? "is" : "is not") + " targetable");
                    c.setAllowableAsTarget(isIsoClass);
                    // attributes
                    List<UMLAttribute> umlAttribs = clazz.getAttributes();
                    List<gov.nih.nci.cagrid.metadata.common.UMLAttribute> attribs = 
                        new ArrayList<gov.nih.nci.cagrid.metadata.common.UMLAttribute>(umlAttribs.size());
                    for (UMLAttribute attrib : umlAttribs) {
                        LOG.debug("Creating class attribute " + attrib.getName());
                        System.out.println("Creating class attribute " + attrib.getName());
                        // determine the data type of the attribute
                        UMLDatatype rawAttributeDatatype = attrib.getDatatype();
                        // sometimes, a user just types in the name of the datatype and it doesn't
                        // really reference a UMLClass instance in the model.  Even though this is
                        // an error, the SDK has a heuristic to deal with it, so we do too.
                        UMLDatatype attributeDatatype = deriveFullClass(rawAttributeDatatype, umlClasses);
                        if (attributeDatatype == null) {
                            // no class could be found with our heuristic!
                            LOG.warn("NO ATTRIBUTE DATATYPE COULD BE INFERED.  FALLING BACK TO " + rawAttributeDatatype.getName());
                            System.out.println("NO ATTRIBUTE DATATYPE COULD BE INFERED.  FALLING BACK TO " + rawAttributeDatatype.getName());
                            attributeDatatype = rawAttributeDatatype;
                        }
                        String attributeDatatypeName = attributeDatatype.getName();
                        if (attributeDatatype instanceof UMLClass) {
                            String rawAttribTypePackage = getFullPackageName((UMLClass) attributeDatatype);
                            String datatypeClassname = null;
                            if (isoPattern.matcher(rawAttribTypePackage).matches()) {
                                datatypeClassname = cleanUpIsoClassName(attributeDatatype.getName());
                            } else {
                                datatypeClassname = attributeDatatype.getName();
                            }
                            attributeDatatypeName = 
                                rawAttribTypePackage.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length()) + 
                                    "." + datatypeClassname;
                        }
                        LOG.debug("Attribute datatype determined to be " + attributeDatatypeName);
                        System.out.println("Attribute datatype determined to be " + attributeDatatypeName);
                        // CAVEAT: have to turn "attributes" that are ISO types into unidirectional Associations.
                        if (isoPattern.matcher(attributeDatatypeName).matches()) {
                            LOG.debug("Attribute datatype is complex.  This will be modeled as a unidirectional Association");
                            System.out.println("Attribute datatype is complex.  This will be modeled as a unidirectional Association");
                            gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation isoAssociation = 
                                new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
                            isoAssociation.setBidirectional(false);
                            UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
                            sourceEdge.setMaxCardinality(1);
                            sourceEdge.setMinCardinality(0);
                            sourceEdge.setRoleName(attrib.getName());
                            sourceEdge.setUMLClassReference(new UMLClassReference(String.valueOf(attributeDatatype)));
                            isoAssociation.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(sourceEdge));
                            UMLAssociationEdge targetEdge = new UMLAssociationEdge();
                            targetEdge.setMaxCardinality(1);
                            targetEdge.setMinCardinality(0);
                            targetEdge.setRoleName(attrib.getName());
                            targetEdge.setUMLClassReference(new UMLClassReference(c.getId()));
                            isoAssociation.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(targetEdge));
                            domainAssociations.add(isoAssociation);
                        } else {
                            LOG.debug("Attribute datatype is simple.");
                            gov.nih.nci.cagrid.metadata.common.UMLAttribute a = 
                                new gov.nih.nci.cagrid.metadata.common.UMLAttribute();
                            a.setName(attrib.getName());
                            a.setDataTypeName(attributeDatatypeName);
                            a.setVersion(getAttributeVersion());
                            // look at tagged values for the concept code info
                            annotateAttribute(a, attrib.getTaggedValues());
                            // keep the attribute
                            attribs.add(a);
                        }
                    }
                    c.setUmlAttributeCollection(new UMLClassUmlAttributeCollection(
                        attribs.toArray(new gov.nih.nci.cagrid.metadata.common.UMLAttribute[0])));
                    domainClasses.add(c);
                    // associations of this class
                    Set<UMLAssociation> umlAssociations = clazz.getAssociations();
                    for (UMLAssociation assoc : umlAssociations) {
                        gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation a = 
                            new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
                        boolean bidirectional = associationIsBidirectional(clazz, assoc);
                        a.setBidirectional(bidirectional);
                        // FIXME: this is creating self-associations.  Why?
                        UMLClass targetClass = (UMLClass) assoc.getAssociationEnds().get(0).getUMLElement();
                        // source
                        // the 0th edge describes the relationship from the perspective of THIS class
                        UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
                        // the max and min cardinality of the associated datatype
                        sourceEdge.setMaxCardinality(assoc.getAssociationEnds().get(0).getHighMultiplicity());
                        sourceEdge.setMinCardinality(assoc.getAssociationEnds().get(0).getLowMultiplicity());
                        // the role name (i.e. name of the variable in our class)
                        sourceEdge.setRoleName(assoc.getAssociationEnds().get(0).getRoleName());
                        // create a reference to the associated datatype
                        sourceEdge.setUMLClassReference(new UMLClassReference(String.valueOf(targetClass.hashCode())));
                        // target
                        UMLAssociationEdge targetEdge = new UMLAssociationEdge();
                        targetEdge.setMaxCardinality(assoc.getAssociationEnds().get(1).getHighMultiplicity());
                        targetEdge.setMinCardinality(assoc.getAssociationEnds().get(1).getLowMultiplicity());
                        targetEdge.setRoleName(assoc.getAssociationEnds().get(1).getRoleName());
                        targetEdge.setUMLClassReference(new UMLClassReference(c.getId()));
                        a.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(sourceEdge));
                        a.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(targetEdge));
                        domainAssociations.add(a);
                    }
                    System.out.println("----");
                }
            }
        }
        
        // generalizations
        List<UMLGeneralization> umlGeneralizations = umlModel.getGeneralizations();
        for (UMLGeneralization gen : umlGeneralizations) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization g = 
                new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization();
            g.setSubClassReference(new UMLClassReference(String.valueOf(gen.getSubtype().hashCode())));
            g.setSuperClassReference(new UMLClassReference(String.valueOf(gen.getSupertype().hashCode())));
            generalizations.add(g);
        }
        
        // build the model
        domain.setExposedUMLClassCollection(
            new DomainModelExposedUMLClassCollection(
                domainClasses.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLClass[0])));
        domain.setExposedUMLAssociationCollection(
            new DomainModelExposedUMLAssociationCollection(
                domainAssociations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation[0])));
        domain.setUmlGeneralizationCollection(
            new DomainModelUmlGeneralizationCollection(
                generalizations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization[0])));
        
        return domain;
    }
    
    
    private boolean associationIsBidirectional(UMLClass sourceClass, UMLAssociation association) {
        boolean bidirectional = false;
        String sourceRoleName = association.getAssociationEnds().get(0).getRoleName();
        UMLAssociationEnd sourceEnd = association.getAssociationEnds().get(0);
        UMLAssociable target = sourceEnd.getUMLElement();
        Set<UMLAssociation> associationsOfTarget = null;
        if (target instanceof UMLClass) {
            associationsOfTarget = ((UMLClass) target).getAssociations();
        } else if (target instanceof UMLInterface) {
            associationsOfTarget = ((UMLInterface) target).getAssociations();
        }
        for (UMLAssociation targetAssociation : associationsOfTarget) {
            // see if the association comes back to the source class
            UMLAssociable possibleReturnTarget = targetAssociation.getAssociationEnds().get(0).getUMLElement();
            String possibleReturnTargetRoleName = targetAssociation.getAssociationEnds().get(0).getRoleName();
            bidirectional = (possibleReturnTarget == sourceClass && possibleReturnTargetRoleName.equals(sourceRoleName));
            if (bidirectional) {
                break;
            }
        }
        return bidirectional;
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
    
    
    private UMLClass deriveFullClass(UMLDatatype datatype, List<UMLClass> searchClasses) {
        UMLClass determinedClass = null;
        if (datatype instanceof UMLClass) {
            determinedClass = (UMLClass) datatype;
        } else {
            String name = datatype.getName();
            Pattern isoPattern = Pattern.compile(ISO_PACKAGE_REGEX);
            UMLClass candidate = null;
            UMLClass isoCandidate = null;
            for (UMLClass c : searchClasses) {
                if (name.equals(c.getName())) {
                    String packName = getFullPackageName(c);
                    if (!shouldExcludePackage(packName)) {
                        if (isoPattern.matcher(packName).matches()) {
                            isoCandidate = c;
                        } else {
                            candidate = c;
                        }
                    }
                }
            }
            if (isoCandidate != null) {
                determinedClass = isoCandidate;
            } else if (candidate != null) {
                determinedClass = candidate;
            }
        }
        return determinedClass;
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
    
    
    private boolean shouldExcludePackage(String packageName) {
        boolean exclude = false;
        exclude = !packageName.startsWith(LOGICAL_MODEL_PACKAGE_PREFIX);
        if (!exclude) {
            // check regexes
            StringTokenizer regexTokenizer = new StringTokenizer(getPackageExcludeRegex(), ",");
            while (!exclude && regexTokenizer.hasMoreTokens()) {
                exclude = Pattern.matches(regexTokenizer.nextToken(), packageName);
            }
        }
        return exclude;
    }
    
    
    /**
     * Cleans up the ISO class name in the model to match
     * the Java class name in the common project
     * 
     * eg1) II -> Ii
     * eg2) EN.ON - >EnOn
     * 
     * @param isoClassName
     * @return
     */
    private String cleanUpIsoClassName(String isoClassName) {
        StringTokenizer tok = new StringTokenizer(isoClassName, ".");
        StringBuffer cleaned = new StringBuffer();
        while (tok.hasMoreTokens()) {
            String part = tok.nextToken();
            cleaned.append(part.charAt(0));
            if (part.length() != 1) {
                cleaned.append(part.toLowerCase().substring(1));
            }
        }
        return cleaned.toString();
    }


    public static void main(String[] args) {
        ISOSupportDomainModelGenerator generator = new ISOSupportDomainModelGenerator(HandlerEnum.EADefault);
        try {
            System.out.println("generating model");
            DomainModel model = generator.generateDomainModel("test/resources/sdk-new.xmi");
            System.out.println("serializing");
            FileWriter writer = new FileWriter("sample-domain-model.xml");
            MetadataUtils.serializeDomainModel(model, writer);
            System.out.println(writer.toString());
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
