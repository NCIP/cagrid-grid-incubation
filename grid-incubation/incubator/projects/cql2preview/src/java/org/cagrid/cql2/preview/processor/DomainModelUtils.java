package org.cagrid.cql2.preview.processor;

import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/** 
  *  DomainModelUtils
  *  Utilities for walking and manipulating a DomainModel
  * 
  * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
  * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
  * 
  * @created Jun 14, 2006 
  * @version $Id: DomainModelUtils.java,v 1.1 2008/04/02 14:46:19 dervin Exp $
 */
public class DomainModelUtils {

	private static Map<DomainModel, Map<String, UMLClass>> domainRefedClasses = 
        new HashMap<DomainModel, Map<String, UMLClass>>();
	
	/**
	 * Return the UMLClass from the DomainModel's exposed UML Classes if the
	 * UMLClass with an id exists which is equal to the refid of the
	 * UMLClassReference. In all other cases, null is returned.
	 * 
	 * @param model
	 *            the DomainModel to look in
	 * @param reference
	 *            the UMLClassReference to the UMLClass to look for in the model
	 * @return null or the referenced UMLClass
	 */
	public static UMLClass getReferencedUMLClass(DomainModel model, UMLClassReference reference) {
		if (model == null) {
			return null;
		}
		Map<String, UMLClass> refedClasses = domainRefedClasses.get(model);
		if (refedClasses == null) {
			// populate class references for this model
			UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
			refedClasses = new HashMap<String, UMLClass>(classes.length);
			for (int i = 0; i < classes.length; i++) {
				UMLClass clazz = classes[i];
				refedClasses.put(clazz.getId(), clazz);
			}
		}
		UMLClass refedClass = refedClasses.get(reference.getRefid());
		return refedClass;
	}
	
	
	/**
	 * Gets all superclasses of the specified UMLCLass
	 * 
	 * @param model
	 * 		The domain model to search for superclasses
	 * @param umlClass
	 * 		The class to retrieve superclasses of
	 * @return
	 * 		All superclasses of the specified UMLClass
	 */	
	public static UMLClass[] getAllSuperclasses(DomainModel model, UMLClass umlClass) {
		return getAllSuperclasses(model, umlClass.getPackageName() + "." + umlClass.getClassName());
	}
	
	
	/**
	 * Gets all superclasses of the specified UMLCLass
	 * 
	 * @param model
	 * 		The domain model to search for superclasses
	 * @param className
	 * 		The name of the class to retrieve superclasses of
	 * @return
	 * 		All superclasses of the named class
	 */
	public static UMLClass[] getAllSuperclasses(DomainModel model, String className) {
		Set<UMLClass> supers = getSuperclasses(model, className);
		UMLClass[] classes = new UMLClass[supers.size()];
		supers.toArray(classes);
		return classes;
	}
    
    
    /**
     * Gets the UMLClassReference for a class in a domain model
     * 
     * @param model
     *      The domain model
     * @param className
     *      The fully qualified name of the class
     * @return
     *      The UMLClassReference, or null if no class was found
     */
    public static UMLClassReference getClassReference(DomainModel model, String className) {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        for (UMLClass c : classes) {
            if (getQualifiedClassname(c).equals(className)) {
                return new UMLClassReference(c.getId());
            }
        }
        return null;
    }
    
    
    public static String getQualifiedClassname(UMLClass clazz) {
        if (clazz.getPackageName() != null) {
            return clazz.getPackageName() + "." + clazz.getClassName();
        }
        return clazz.getClassName();
    }
	
	
	private static Set<UMLClass> getSuperclasses(DomainModel model, String className) {
		UMLGeneralization[] generalization = model.getUmlGeneralizationCollection().getUMLGeneralization();
		Set<UMLClass> superclasses = new HashSet<UMLClass>();
		// find all generalizations where subclass is the class in question,
		// then get the superclasses from each
		for (int i = 0; generalization != null && i < generalization.length; i++) {
			UMLClassReference subClassRef = generalization[i].getSubClassReference();
			UMLClassReference superClassRef = generalization[i].getSuperClassReference();
			if (subClassRef != null && superClassRef != null) {
				UMLClass subclass = getReferencedUMLClass(model, subClassRef);
				if ((subclass.getPackageName() + "." + subclass.getClassName()).equals(className)) {
					UMLClass superclass = getReferencedUMLClass(model, superClassRef);
					superclasses.add(superclass);
					// get superclasses of the superclass
					superclasses.addAll(getSuperclasses(model, superclass.getPackageName() + "." + superclass.getClassName()));
				}
			}
		}
		return superclasses;
	}
}
