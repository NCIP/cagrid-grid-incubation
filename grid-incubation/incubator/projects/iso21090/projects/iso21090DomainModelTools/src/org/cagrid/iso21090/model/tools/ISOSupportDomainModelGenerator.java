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
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralizable;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    public static final String JAVA_PACKAGE_REGEX = ".*?java.*";
    public static final Pattern ISO_PATTERN = Pattern.compile(ISO_PACKAGE_REGEX);
    public static final Pattern JAVA_PATTERN = Pattern.compile(JAVA_PACKAGE_REGEX);
    
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
        Map<String, gov.nih.nci.cagrid.metadata.dataservice.UMLClass> domainClasses = 
            new HashMap<String, gov.nih.nci.cagrid.metadata.dataservice.UMLClass>();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation> domainAssociations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation>();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization> domainGeneralizations = 
            new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization>();
        // add the pre-created IVL class
        LOG.debug("Creating initial class list");
        UMLPackage isoPackage = findIsoPackage(umlModel.getPackages());
        IvlUmlClass ivlClass = new IvlUmlClass(isoPackage);
        umlClasses.add(ivlClass);
        // have to pre-create all the classes so I can properly create associations w/ refs between them
        for (UMLClass clazz : umlClasses) {
            if (clazz.getPackage() == null) {
                LOG.debug("Skipping class with null package: " + clazz.getName());
            } else {
                String fullPackageName = getFullPackageName(clazz);
                // filter out anything not in the logical model and matching the exclude regex
                if (shouldExcludePackage(fullPackageName)) {
                    LOG.debug("Excluding class " + fullPackageName + "." + clazz.getName());
                } else if (clazz.getName().startsWith("IVL<")) {
                    LOG.debug("Skipping " + clazz.getName() + " in favor of a single Ivl class");
                } else {
                    // basic class info
                    String strippedPackageName = fullPackageName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    boolean isIsoClass = ISO_PATTERN.matcher(strippedPackageName).matches();
                    String shortClassName = isIsoClass ? cleanUpIsoClassName(clazz.getName()) : clazz.getName();
                    LOG.debug("Creating model class " + strippedPackageName + "." + shortClassName);
                    gov.nih.nci.cagrid.metadata.dataservice.UMLClass c = new gov.nih.nci.cagrid.metadata.dataservice.UMLClass();
                    c.setPackageName(strippedPackageName);
                    c.setClassName(shortClassName);
                    c.setId(String.valueOf(clazz.hashCode()));
                    // ISO types aren't targetable; everything else is by default
                    LOG.debug("Class " + (isIsoClass ? "is" : "is not") + " targetable");
                    c.setAllowableAsTarget(!isIsoClass);
                    domainClasses.put(c.getPackageName() + "." + c.getClassName(), c);
                }
            }
        }
        
        // process through the classes again
        LOG.debug("Reprocessing classes to figure out attributes");
        for (UMLClass clazz : umlClasses) {
            if (clazz.getPackage() == null) {
                LOG.debug("Skipping class with null package: " + clazz.getName());
            } else {
                String fullPackageName = getFullPackageName(clazz);
                // filter out anything not in the logical model and matching the exclude regex
                if (shouldExcludePackage(fullPackageName)) {
                    LOG.debug("Excluding class " + fullPackageName + "." + clazz.getName());
                } else if (clazz.getName().startsWith("IVL<")) {
                    LOG.debug("Skipping " + clazz.getName() + " in favor of a single Ivl class");
                } else {
                    // basic class info
                    String strippedPackageName = fullPackageName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    boolean isIsoClass = ISO_PATTERN.matcher(strippedPackageName).matches();
                    String shortClassName = isIsoClass ? cleanUpIsoClassName(clazz.getName()) : clazz.getName();
                    String fullClassName = strippedPackageName + "." + shortClassName;
                    gov.nih.nci.cagrid.metadata.dataservice.UMLClass c = domainClasses.get(fullClassName);
                    
                    // attributes
                    LOG.debug("Processing attributes of the class");
                    List<UMLAttribute> umlAttribs = clazz.getAttributes();
                    List<gov.nih.nci.cagrid.metadata.common.UMLAttribute> attribs = 
                        new ArrayList<gov.nih.nci.cagrid.metadata.common.UMLAttribute>(umlAttribs.size());
                    for (UMLAttribute attrib : umlAttribs) {
                        LOG.debug("Creating class attribute " + attrib.getName());
                        // determine the data type of the attribute
                        UMLDatatype rawAttributeDatatype = attrib.getDatatype();
                        boolean isCollection = attributeTypeRepresentsCollection(rawAttributeDatatype.getName());
                        boolean isGeneric = attributeTypeRepresentsGeneric(rawAttributeDatatype.getName());
                        // TODO: if generic, create an association from the generic to the specific
                        LOG.debug("Attribute " + (isCollection ? "represents" : "does not represent") + " a collection");
                        LOG.debug("Attribute " + (isGeneric ? "represents" : "does not represent") + " a generic");
                        // sometimes, a user just types in the name of the datatype and it doesn't
                        // really reference a UMLClass instance in the model.  Even though this is
                        // an error, the SDK has a heuristic to deal with it, so we do too.
                        UMLDatatype attributeDatatype = deriveRealClass(rawAttributeDatatype, umlClasses);
                        if (attributeDatatype == null) {
                            // no class could be found with our heuristic!
                            LOG.warn("NO ATTRIBUTE DATATYPE COULD BE INFERED.  FALLING BACK TO " + rawAttributeDatatype.getName());
                            attributeDatatype = rawAttributeDatatype;
                        }
                        String attributeDatatypeName = attributeDatatype.getName();
                        if (attributeDatatype instanceof UMLClass) {
                            String rawAttribTypePackage = getFullPackageName((UMLClass) attributeDatatype);
                            String datatypeClassname = null;
                            if (ISO_PATTERN.matcher(rawAttribTypePackage).matches()) {
                                datatypeClassname = cleanUpIsoClassName(attributeDatatype.getName());
                            } else {
                                datatypeClassname = attributeDatatype.getName();
                            }
                            attributeDatatypeName = 
                                rawAttribTypePackage.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length()) + 
                                    "." + datatypeClassname;
                        }
                        LOG.debug("Attribute datatype determined to be " + attributeDatatypeName);
                        // CAVEAT: have to turn "attributes" that are ISO types into unidirectional Associations.
                        if (ISO_PATTERN.matcher(attributeDatatypeName).matches()) {
                            LOG.debug("Attribute datatype is complex.  This will be modeled as a unidirectional Association");
                            gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation isoAssociation = 
                                new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
                            isoAssociation.setBidirectional(false);
                            UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
                            sourceEdge.setMaxCardinality(1);
                            sourceEdge.setMinCardinality(0);
                            sourceEdge.setRoleName(attrib.getName());
                            if (isGeneric) {
                                // get the generic specific type
                                UMLClass specificType = deriveRealClass(
                                    getGenericSpecificType(rawAttributeDatatype.getName()), umlClasses);
                                sourceEdge.setUMLClassReference(new UMLClassReference(String.valueOf(specificType.hashCode())));
                            } else {
                                sourceEdge.setUMLClassReference(new UMLClassReference(c.getId()));
                            }
                            isoAssociation.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(sourceEdge));
                            UMLAssociationEdge targetEdge = new UMLAssociationEdge();
                            targetEdge.setMaxCardinality(isCollection ? -1 : 1);
                            targetEdge.setMinCardinality(0);
                            targetEdge.setRoleName(attrib.getName());
                            targetEdge.setUMLClassReference(new UMLClassReference(String.valueOf(attributeDatatype.hashCode())));
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
                }
            }
        }
        
        // associations
        LOG.debug("Processing associations");
        List<UMLAssociation> umlAssociations = umlModel.getAssociations();
        for (UMLAssociation assoc : umlAssociations) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation a = 
                new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
            boolean bidirectional = associationIsBidirectional(assoc);
            a.setBidirectional(bidirectional);
            // source
            // the 0th edge describes the relationship from the perspective of THIS class
            UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
            // the max and min cardinality of the associated datatype
            sourceEdge.setMaxCardinality(assoc.getAssociationEnds().get(0).getHighMultiplicity());
            sourceEdge.setMinCardinality(assoc.getAssociationEnds().get(0).getLowMultiplicity());
            // the role name (i.e. name of the variable in our class)
            sourceEdge.setRoleName(assoc.getAssociationEnds().get(0).getRoleName());
            // create a reference to the associated datatype
            UMLClass sourceClass = (UMLClass) assoc.getAssociationEnds().get(0).getUMLElement();
            sourceEdge.setUMLClassReference(new UMLClassReference(String.valueOf(sourceClass.hashCode())));
            // target
            UMLAssociationEdge targetEdge = new UMLAssociationEdge();
            targetEdge.setMaxCardinality(assoc.getAssociationEnds().get(1).getHighMultiplicity());
            targetEdge.setMinCardinality(assoc.getAssociationEnds().get(1).getLowMultiplicity());
            targetEdge.setRoleName(assoc.getAssociationEnds().get(1).getRoleName());
            UMLClass targetClass = (UMLClass) assoc.getAssociationEnds().get(1).getUMLElement();
            targetEdge.setUMLClassReference(new UMLClassReference(String.valueOf(targetClass.hashCode())));
            a.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(sourceEdge));
            a.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(targetEdge));
            domainAssociations.add(a);
        }
        
        // generalizations
        List<UMLGeneralization> umlGeneralizations = umlModel.getGeneralizations();
        for (UMLGeneralization gen : umlGeneralizations) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization g = 
                new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization();
            UMLGeneralizable sub = gen.getSubtype();
            String subId = String.valueOf(sub.hashCode());
            if (sub instanceof UMLDatatype) {
                UMLClass derived = deriveRealClass((UMLDatatype) sub, umlClasses);
                if (derived != null) {
                    String fqClassName = getFullPackageName(derived) + "." + derived.getName();
                    if (ISO_PATTERN.matcher(fqClassName).matches()) {
                        fqClassName = getFullPackageName(derived) + "." + cleanUpIsoClassName(derived.getName());
                    }
                    fqClassName = fqClassName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    LOG.debug("Datatype resolved to " + fqClassName);
                    subId = domainClasses.get(fqClassName).getId();
                } else {
                    LOG.error("GENERALIZATION DATATYPE NOT FOUND (" + sub.getName() + ")");
                    LOG.error("SKIPPING THIS GENERALIZATION");
                    continue;
                }
            } else {
                LOG.error("I DONT KNOW WHAT TO DO WITH " + sub.getClass().getName());
                LOG.error("SKIPPING THIS GENERALIZATION");
                continue;
            }
            UMLGeneralizable sup = gen.getSupertype();
            String supId = String.valueOf(sup.hashCode());
            if (sup instanceof UMLDatatype) {
                UMLClass derived = deriveRealClass((UMLDatatype) sup, umlClasses);
                if (derived != null) {
                    String fqClassName = getFullPackageName(derived) + "." + derived.getName();
                    if (ISO_PATTERN.matcher(fqClassName).matches()) {
                        fqClassName = getFullPackageName(derived) + "." + cleanUpIsoClassName(derived.getName());
                    }
                    fqClassName = fqClassName.substring(LOGICAL_MODEL_PACKAGE_PREFIX.length());
                    LOG.debug("Datatype resolved to " + fqClassName);
                    supId = domainClasses.get(fqClassName).getId();
                } else {
                    LOG.error("GENERALIZATION DATATYPE NOT FOUND (" + sup.getName() + ")");
                    LOG.error("SKIPPING THIS GENERALIZATION");
                    continue;
                }
            } else {
                LOG.error("I DONT KNOW WHAT TO DO WITH " + sup.getClass().getName());
                LOG.error("SKIPPING THIS GENERALIZATION");
                continue;
            }
            g.setSubClassReference(new UMLClassReference(subId));
            g.setSuperClassReference(new UMLClassReference(supId));
            domainGeneralizations.add(g);
        }
        
        // build the model
        domain.setExposedUMLClassCollection(
            new DomainModelExposedUMLClassCollection(
                domainClasses.values().toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLClass[0])));
        domain.setExposedUMLAssociationCollection(
            new DomainModelExposedUMLAssociationCollection(
                domainAssociations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation[0])));
        domain.setUmlGeneralizationCollection(
            new DomainModelUmlGeneralizationCollection(
                domainGeneralizations.toArray(
                    new gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization[0])));
        
        return domain;
    }
    
    
    private boolean associationIsBidirectional(UMLAssociation association) {
        boolean bidirectional = false;
        UMLTaggedValue tag = association.getTaggedValue("direction");
        if (tag != null) {
            System.out.println("Association direction: " + tag.getValue());
            bidirectional = "Bi-Directional".equals(tag.getValue()) ||
                "Unspecified".equals(tag.getValue());
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
    
    
    private UMLClass deriveRealClass(UMLDatatype datatype, List<UMLClass> searchClasses) {
        LOG.debug("Deriving real class for datatype " + datatype.getName());
        UMLClass determinedClass = null;
        if (datatype instanceof UMLClass) {
            determinedClass = (UMLClass) datatype;
        } else {
            determinedClass = deriveRealClass(datatype.getName(), searchClasses);
        }
        return determinedClass;
    }
    
    
    private UMLClass deriveRealClass(String shortName, List<UMLClass> searchClasses) {
        LOG.debug("Deriving real UML class for data type " + shortName);
        UMLClass determinedClass = null;
        String name = shortName;
        if (attributeTypeRepresentsCollection(shortName)) {
            name = stripCollectionRepresentation(shortName);
        } else if (attributeTypeRepresentsGeneric(shortName)) {
            name = stripGeneric(shortName);
        }
        UMLClass candidate = null;
        UMLClass javaCandidate = null;
        UMLClass isoCandidate = null;
        for (UMLClass c : searchClasses) {
            if (name.equals(c.getName())) {
                String packName = getFullPackageName(c);
                if (packName.startsWith(LOGICAL_MODEL_PACKAGE_PREFIX)) {
                    // it's a logical model package, please continue processing
                    if (JAVA_PATTERN.matcher(packName).matches()) {
                        javaCandidate = c;
                    } else if (ISO_PATTERN.matcher(packName).matches()) {
                        isoCandidate = c;
                    } else {
                        candidate = c;
                    }
                }
            }
        }
        // prefer the java, then the ISO, then the non-ISO candidate
        if (javaCandidate != null) {
            LOG.debug("Determined class to be a Java type");
            determinedClass = javaCandidate;
        } else if (isoCandidate != null) {
            LOG.debug("Deterined class to be an ISO type");
            determinedClass = isoCandidate;
        } else {
            LOG.debug("Determined class to be of unknown type");
            determinedClass = candidate;
        }
        return determinedClass;
    }
    
    
    private void annotateAttribute(
        gov.nih.nci.cagrid.metadata.common.UMLAttribute currentAttribute, 
        Collection<UMLTaggedValue> taggedValues) {
        LOG.debug("Annotating attribute " + currentAttribute.getName());
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
     * eg3) DSET<II> -> Dset
     * 
     * @param isoClassName
     * @return
     */
    private String cleanUpIsoClassName(String isoClassName) {
        LOG.debug("Cleaning up ISO class name " + isoClassName);
        String clean = null;
        StringTokenizer tok = new StringTokenizer(isoClassName, ".");
        StringBuffer cleaned = new StringBuffer();
        while (tok.hasMoreTokens()) {
            String part = tok.nextToken();
            cleaned.append(part.charAt(0));
            if (part.length() != 1) {
                cleaned.append(part.toLowerCase().substring(1));
            }
        }
        int trimPoint = cleaned.indexOf("<");
        if (trimPoint != -1) {
            clean = cleaned.substring(0, trimPoint);
        } else {
            clean = cleaned.toString();
        }
        LOG.debug("Cleaned up: " + clean);
        return clean;
    }
    
    
    private boolean attributeTypeRepresentsCollection(String datatype) {
        return (datatype.startsWith("Sequence(") || datatype.startsWith("Set("))
            && datatype.endsWith(")");
    }
    
    
    private boolean attributeTypeRepresentsGeneric(String datatype) {
        return datatype.contains("<");
    }
    
    
    private String stripCollectionRepresentation(String name) {
        int start = name.indexOf('(');
        int end = name.indexOf(')', start);
        return name.substring(start + 1, end);
    }
    
    
    private String stripGeneric(String name) {
        int start = name.indexOf('<');
        return name.substring(0, start);
    }
    
    
    private String getGenericSpecificType(String name) {
        int start = name.indexOf('<');
        int end = name.indexOf('>', start);
        return name.substring(start + 1, end);
    }
    
    
    private UMLPackage findIsoPackage(Collection<UMLPackage> packs) {
        for (UMLPackage p : packs) {
            // determine the package name
            StringBuffer buf = new StringBuffer();
            boolean first = true;
            UMLPackage pack = p;
            while (pack != null) {
                if (!first) {
                    buf.insert(0, '.');   
                }
                first = false;
                buf.insert(0, pack.getName());
                pack = pack.getParent();
            }
            if (buf.toString().equals(LOGICAL_MODEL_PACKAGE_PREFIX + "gov.nih.nci.iso21090")) {
                return p;
            } else {
                UMLPackage maybe = findIsoPackage(p.getPackages());
                if (maybe != null) {
                    return maybe;
                }
            }
        }
        return null;
    }


    public static void main(String[] args) {
        ISOSupportDomainModelGenerator generator = new ISOSupportDomainModelGenerator(HandlerEnum.EADefault);
        try {
            System.out.println("generating model");
            DomainModel model = generator.generateDomainModel("test/resources/sdk.xmi");
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
