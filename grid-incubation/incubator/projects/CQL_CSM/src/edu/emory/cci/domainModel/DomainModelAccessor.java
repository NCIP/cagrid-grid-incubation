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
/**
 */
package edu.emory.cci.domainModel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.axis.MessageContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;

import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

/**
 * Convenience class to allow CQL processors to access and navigate a caGRID
 * service's domain model.
 * 
 * @author Mark Grand
 */
public class DomainModelAccessor {
    private static Logger myLogger = LogManager.getLogger(DomainModelAccessor.class);

    private Map<String, UMLClass> classDictionary = new HashMap<String, UMLClass>();
    private Map<String, String> subclassToSuperclassMap = new HashMap<String, String>();
    private Map<String, Map<String, String>> classNameToAssociationRoleToClassNameMap = new HashMap<String, Map<String, String>>();

    /**
     * Construct an instance that finds its domain model through the current
     * MessageContext.
     */
    public DomainModelAccessor() {
        this(fetchDomainModel());
    }

    public DomainModelAccessor(DomainModel model) {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        Map<String, String> idToNameMap = new HashMap<String, String>();
        for (UMLClass umlClass : classes) {
            String packageName = umlClass.getPackageName();
            String qualifiedClassName;
            if (packageName == null) {
                qualifiedClassName = umlClass.getClassName().trim();
            } else {
                String trimmedPackageName = packageName.trim();
                if (trimmedPackageName.length() == 0) {
                    qualifiedClassName = umlClass.getClassName().trim();
                } else {
                    qualifiedClassName = trimmedPackageName + "." + umlClass.getClassName().trim();
                }
            }
            classDictionary.put(qualifiedClassName, umlClass);
            idToNameMap.put(umlClass.getId(), qualifiedClassName);
            classNameToAssociationRoleToClassNameMap.put(qualifiedClassName, new HashMap<String, String>());
        }
        processUMLAssociations(model, idToNameMap);
        processUMLGeneralizations(model, idToNameMap);
    }

    /**
     * Compile UML Associations from a domain model into a map.
     * 
     * @param model
     * @param idToNameMap
     */
    private void processUMLAssociations(DomainModel model, Map<String, String> idToNameMap) {
        UMLAssociation[] associationArray = model.getExposedUMLAssociationCollection().getUMLAssociation();
        if (associationArray != null) {
            for (int i = 0; i < associationArray.length; i++) {
                try {
                    UMLAssociation thisAssociation = associationArray[i];
                    UMLAssociationEdge sourceEdge = thisAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge();
                    String sourceClassName = resolveRefidToClassName(idToNameMap, sourceEdge);
                    UMLAssociationEdge targetEdge = thisAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge();
                    String targetClassName = resolveRefidToClassName(idToNameMap, targetEdge);
                    String targetRoleName = targetEdge.getRoleName();
                    classNameToAssociationRoleToClassNameMap.get(sourceClassName).put(targetRoleName, targetClassName);
                    if (thisAssociation.isBidirectional()) {
                        String sourceRoleName = sourceEdge.getRoleName();
                        classNameToAssociationRoleToClassNameMap.get(targetClassName).put(sourceRoleName, sourceClassName);
                    }
                } catch (Exception e) {
                    String msg = "Error in UML association:";
                    myLogger.warn(msg, e);
                }
            }
        }
    }

    /**
     * Compile UML generalizations in a domain model into a map.
     * 
     * @param model
     * @param idToNameMap
     */
    private void processUMLGeneralizations(DomainModel model, Map<String, String> idToNameMap) {
        UMLGeneralization[] generalizations = model.getUmlGeneralizationCollection().getUMLGeneralization();
        if (generalizations != null) {
            for (int i = 0; i < generalizations.length; i++) {
                UMLGeneralization thisGeneralization = generalizations[i];
                String subclassId = thisGeneralization.getSubClassReference().getRefid();
                String superclassId = thisGeneralization.getSuperClassReference().getRefid();
                try {
                    subclassToSuperclassMap.put(resolveRefidToClassName(idToNameMap, subclassId), resolveRefidToClassName(idToNameMap,
                            superclassId));
                } catch (Exception e) {
                    String msg = "Error in UML generalization:";
                    myLogger.warn(msg, e);
                }
            }
        }
    }

    /**
     * Return the name of the class the an end of an association is connect to.
     * 
     * @param idToNameMap
     *            a map from refid's to class names.
     * @param sourceEdge
     *            an end of an association.
     * @return the name of the class that is referred to by the given
     *         UMLAssociationEdge.
     * @throws Exception
     *             if there is a problem such as no class name corresponds to
     *             the given refid.
     */
    private String resolveRefidToClassName(Map<String, String> idToNameMap, UMLAssociationEdge sourceEdge) throws Exception {
        return resolveRefidToClassName(idToNameMap, sourceEdge.getUMLClassReference().getRefid());
    }

    /**
     * Return the name of the class that corresponds to the given refid.
     * 
     * @param idToNameMap
     *            a map from refid's to class names.
     * @param refid
     *            the refid to resolve to a class name.
     * @return the name of the class that is referred to by the given refid.
     * @throws Exception
     *             if there is a problem such as no class name corresponds to
     *             the given refid.
     */
    private String resolveRefidToClassName(Map<String, String> idToNameMap, String refid) throws Exception {
        String className = idToNameMap.get(refid);
        if (className == null) {
            throw new Exception("refid " + refid + " in domain model is not defined by any XML element.");
        }
        return className;
    }

    /**
     * Return the domain model for the caGRID service that this is part of.
     */
    static DomainModel fetchDomainModel() {
        ResourceContext ctx;
        try {
            MessageContext msgContext = MessageContext.getCurrentContext();
            if (msgContext == null) {
                String msg = "Unable to determine message context!";
                myLogger.error(msg);
                throw new RuntimeException(msg);
            }

            ctx = ResourceContext.getResourceContext(msgContext);
        } catch (ResourceContextException e) {
            String msg = "Could not get ResourceContext";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        Resource serviceBaseResource;
        try {
            serviceBaseResource = ctx.getResource();
        } catch (Exception e) {
            String msg = "Problem getting serviceBaseResource";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        Method domainModelMethod;
        try {
            domainModelMethod = serviceBaseResource.getClass().getMethod("getDomainModel");
        } catch (SecurityException e) {
            String msg = "Security policies are preventing access to the getDomainModel() method of the serviceBaseResource.";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (NoSuchMethodException e) {
            String msg = "The serviceBaseResource has no getDomainModel() method.";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        try {
            return (DomainModel) domainModelMethod.invoke(serviceBaseResource);
        } catch (Exception e) {
            String msg = "Problem getting the service's data model.";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Return true if the named class is an exposed class in the domain model
     * 
     * @param name
     *            The name of the UML class to find
     * @return true if the class is found; otherwise false.
     */
    public boolean isExposedClass(String name) {
        return classDictionary.containsKey(name);
    }

    /**
     * Return true if the named class is allowed to be the target of a CQL
     * query.
     * 
     * @param name
     *            The name of the UML class to find.
     * @return true if the named class is found in the domain model and is
     *         allowed to be the target of a query; otherwise false.
     */
    public boolean isAllowableAsTarget(String name) {
        UMLClass namedClass = classDictionary.get(name);
        return namedClass != null && namedClass.isAllowableAsTarget();
    }

    /**
     * Return the names of the exposed UML classes in the domain model. This is
     * intended for use in generating error messages.
     * 
     * @return The names of the exposed UML classes in the domain model. The
     *         names are sorted in ascending order.
     */
    public String[] getUMLClassNames() {
        Set<String> nameSet = classDictionary.keySet();
        String[] names = nameSet.toArray(new String[nameSet.size()]);
        Arrays.sort(names);
        return names;
    }

    /**
     * Return true if the named class has any attributes.
     * 
     * @param className
     *            The name of the UML class.
     * @return true if the named class is an exposed class in the data model and
     *         it has any attributes.
     */
    public boolean classHasAttributes(String className) {
        UMLClass namedClass = classDictionary.get(className);
        if (namedClass == null) {
            return false;
        }
        while (true) {
            UMLClassUmlAttributeCollection attributeCollection = namedClass.getUmlAttributeCollection();
            if (attributeCollection != null) {
                UMLAttribute[] attributes = attributeCollection.getUMLAttribute();
                if (attributes.length > 0) {
                    return true;
                }
            }
            String superclass = subclassToSuperclassMap.get(className);
            if (superclass == null) {
                return false;
            }
            className = superclass;
            namedClass = classDictionary.get(className);
        }
    }

    /**
     * Return true if the named class has the named attribute.
     * 
     * @param className
     *            The name of the UML class.
     * @param attributeName
     *            The name of the Attribute.
     * @return true if the named class is an exposed class in the data model and
     *         it has an attribute with the given name.
     */
    public boolean classHasAttribute(String className, String attributeName) {
        UMLClass namedClass = classDictionary.get(className);
        if (namedClass == null) {
            return false;
        }
        while (true) {
            UMLClassUmlAttributeCollection attributeCollection = namedClass.getUmlAttributeCollection();
            if (attributeCollection != null) {
                UMLAttribute[] attributes = attributeCollection.getUMLAttribute();
                for (int i = 0; i < attributes.length; i++) {
                    if (attributes[i].getName().equals(attributeName)) {
                        return true;
                    }
                }
            }
            String superclass = subclassToSuperclassMap.get(className);
            if (superclass == null) {
                return false;
            }
            className = superclass;
            namedClass = classDictionary.get(className);
        }
    }

    /**
     * Return the names of the attributes of the named class.
     * 
     * @param className
     *            The name of the UML class.
     * @return The names of the attributes of the named UML class. The names are
     *         sorted in ascending order. If there is no class with the
     *         specified name, an empty array is returned.
     */
    public String[] getAttributeNames(String className) {
        UMLClass namedClass = classDictionary.get(className);
        if (namedClass == null) {
            return new String[0];
        }
        Set<String> attributeNameSet = new HashSet<String>();
        while (true) {
            UMLClassUmlAttributeCollection attributeCollection = namedClass.getUmlAttributeCollection();
            if (attributeCollection != null) {
                UMLAttribute[] attributes = attributeCollection.getUMLAttribute();
                for (int i = 0; i < attributes.length; i++) {
                    attributeNameSet.add(attributes[i].getName());
                }
            }
            String superclass = subclassToSuperclassMap.get(className);
            if (superclass == null) {
                break;
            }
            className = superclass;
            namedClass = classDictionary.get(className);
        }
        String[] names = new String[attributeNameSet.size()];
        names = attributeNameSet.toArray(names);
        Arrays.sort(names);
        return names;
    }

    /**
     * Return the name of the class that is associated with the named class
     * through an association identified by the named role.
     * 
     * @param className
     *            The name of the class.
     * @param associationRoleName
     *            The role name.
     * @return The name of the associated class or null if there is no such
     *         class.
     */
    public String getAssociatedClass(String className, String associationRoleName) {
        Map<String, String> roleToClassNameMap = classNameToAssociationRoleToClassNameMap.get(className);
        if (roleToClassNameMap == null) {
            String superclass = subclassToSuperclassMap.get(className);
            if (superclass != null) {
                return getAssociatedClass(superclass, associationRoleName);
            }
            return null;
        }
        String associatedClass = roleToClassNameMap.get(associationRoleName);
        if (associatedClass == null) {
            String superclass = subclassToSuperclassMap.get(className);
            if (superclass != null) {
                return getAssociatedClass(superclass, associationRoleName);
            }
            return null;
        } else {
            return associatedClass;
        }
    }

    /**
     * Return a map whose keys are the role names that identify all of the
     * associations leading away from the names class and whose values are the
     * class that each association leads to.
     * 
     * @param className
     *            The name of the class to get associations for.
     * @return the map described above. If the named class is not found in the
     *         domain model then the returned Map is empty.
     */
    public Map<String, String> getAssociationsAndClasses(String className) {
        Map<String, String> associationMap = new HashMap<String, String>();
        getAssociationsAndClassesHelper(className, associationMap);
        return associationMap;
    }

    /**
     * Recursive helper method for getAssociationAndClasses(). Populates the
     * given map with the associations leading from the named class. It includes
     * associations from superclasses in a way that causes role names from
     * superclasses to be shadowed by role names from subclasses.
     * 
     * @param className
     *            The class to return associations for.
     * @param associationMap
     *            The map to be populated.
     */
    private void getAssociationsAndClassesHelper(String className, Map<String, String> associationMap) {
        String superclass = subclassToSuperclassMap.get(className);
        if (superclass != null) {
            getAssociationsAndClassesHelper(superclass, associationMap);
        }
        Map<String, String> associations = classNameToAssociationRoleToClassNameMap.get(className);
        if (associations != null) {
            associationMap.putAll(associations);
        }
    }
}
