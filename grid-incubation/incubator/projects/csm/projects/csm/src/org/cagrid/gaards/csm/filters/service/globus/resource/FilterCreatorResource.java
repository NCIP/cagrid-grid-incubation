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
package org.cagrid.gaards.csm.filters.service.globus.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cagrid.gaards.csm.filters.service.tools.HibernateHelper;
import org.cagrid.gaards.csm.service.CSMConfiguration;
import org.globus.wsrf.ResourceException;
import org.hibernate.SessionFactory;

import edu.emory.cci.domainModel.DomainModelAccessor;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.exceptions.CSException;

/**
 * The implementation of this FilterCreatorResource type.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class FilterCreatorResource extends FilterCreatorResourceBase {
    private static final Logger myLogger = LogManager.getLogger(FilterCreatorResource.class);

    private String hibernateConf = null;
    File beansJar = null;
    File ormJar = null;
    private DomainModelAccessor domainModelAccessor = null;
    Long applicationID = null;
    SessionFactory factory = null;
    boolean usingHibernate = false;

    public void setApplicationID(Long id) {
        this.applicationID = id;
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Set application ID to " + id);
        }
    }

    public Long getApplicationID() {
        return this.applicationID;
    }

    public void setHibernateConfigData(String hibernateConf) throws Exception {
        this.usingHibernate = true;
        this.hibernateConf = hibernateConf;
        myLogger.info("Received hibernate configuration data.");
    }

    public void setApplicationBeansJar(byte[] data) throws Exception {
        if (data != null) {
            myLogger.info("Received applications beans jar");
            this.usingHibernate = true;
            beansJar = new File(CSMConfiguration.getConfiguration().getEtcDirectoryPath() + File.separator + (String) getID()
                    + "-beans.jar");
            FileOutputStream writer = new FileOutputStream(beansJar);
            writer.write(data);
            writer.close();
        }
    }

    public void setApplicationORMJar(byte[] data) throws Exception {
        if (data != null) {
            myLogger.info("Received ORM jar");
            this.usingHibernate = true;
            ormJar = new File(CSMConfiguration.getConfiguration().getEtcDirectoryPath() + File.separator + (String) getID() + "-orm.jar");
            FileOutputStream writer = new FileOutputStream(ormJar);
            writer.write(data);
            writer.close();
        }
    }

    /**
     * If the filtering will be driven by a domain model then this method is
     * passed to domain model to use.
     * 
     * @param model
     *            The domain model to use.
     */
    public void setDomainModel(DomainModel model) {
        myLogger.info("Received domain model");
        usingHibernate = false;
        domainModelAccessor = new DomainModelAccessor(model);
    }

    public void init() throws Exception {
        if (this.usingHibernate) {
            myLogger.debug("Initializing hibernate for domain model");
            List<File> fileArray = new ArrayList<File>();
            if (beansJar != null) {
                fileArray.add(beansJar);
            }
            if (ormJar != null) {
                fileArray.add(ormJar);
            }
            factory = HibernateHelper.loadSessionFactory(hibernateConf, fileArray);
        }
    }

    /**
     * Return an array of the names of all the classes that the domain model
     * exposes.
     * 
     * @throws Exception
     *             if there is a problem.
     */
    public String[] getClassNames() throws Exception {
        if (usingHibernate) {
            List<String> classNamesL = getAllClassNamesFromHibernate();
            String[] classNames = new String[classNamesL.size()];
            int i = 0;
            for (Iterator<String> iterator = classNamesL.iterator(); iterator.hasNext();) {
                String string = (String) iterator.next();
                classNames[i++] = string;
            }
            return classNames;
        } else {
            String[] allClassNames = domainModelAccessor.getUMLClassNames();
            List<String> usefulClassNames = new ArrayList<String>(allClassNames.length);
            for (String thisClass : allClassNames) {
//                if (classHasDirectOrIndirectAttributes(thisClass, new HashSet<String>())) {
                    usefulClassNames.add(thisClass);
//                }
            }
            if (myLogger.isDebugEnabled()) {
                String msg = "Getting class names from domain model. Found " + allClassNames.length + " raw class names; "
                        + usefulClassNames.size() + " classes that have direct or indirect attributes.";
                myLogger.debug(msg);
            }
            return usefulClassNames.toArray(new String[usefulClassNames.size()]);
        }
    }

    /**
     * @return
     */
    private List<String> getAllClassNamesFromHibernate() {
        return HibernateHelper.getAllClassNames(this.factory);
    }

    /**
     * Return an array containing the names of all the attributes of the named
     * class.
     * 
     * @param className
     *            The name of the class for which to return attribute names.
     * @throws Exception
     *             if there is a problem.
     */
    public String[] getAssociatedAttributes(String className) throws Exception {
        if (usingHibernate) {
            HashMap<?, String> map = getAssociationAttributesFromHibernate(className);
            String[] strings = new String[map.entrySet().size()];
            int i = 0;
            for (Iterator<?> iterator = map.keySet().iterator(); iterator.hasNext();) {
                strings[i++] = (String) map.get(iterator.next());
            }
            return strings;
        } else {
            className = trimNonClassnamePartOfString(className);
            String[] attributeNames = domainModelAccessor.getAttributeNames(className);
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("getting direct attributes of " + className);
            }
            return attributeNames;
        }

    }

    /**
     * @param className
     * @return
     * @throws CSException
     */
    private HashMap<?, String> getAssociationAttributesFromHibernate(String className) throws CSException {
        return HibernateHelper.getAssociatedAttributes(className, this.factory);
    }

    /**
     * Return an array of strings that identify all of the classes associated
     * with the named class and the associations that connect the named class to
     * its associated classes. Each element of the returned array contains a
     * role name that identifies an association that connects that named class
     * to another class and the name of the other class. The format of the
     * strings is
     * 
     * <pre>
     * roleName - className
     * </pre>
     * 
     * The one exception to the format of the strings is an element that will be
     * present if the named class has any attributes. It is a self-reference.
     * Its format is
     * 
     * <pre>
     * className - self
     * </pre>
     * 
     * @param className
     *            The name of the class to get associations for.
     * @throws Exception
     *             if there is a problem.
     */
    public String[] getAssociatedClasses(String className) throws Exception {
        if (usingHibernate) {
            HashMap<?, ?> map = HibernateHelper.getAssociatedClasses(className, this.factory);
            String[] strings = new String[map.entrySet().size()];
            int i = 0;
            for (Iterator<?> iterator = map.keySet().iterator(); iterator.hasNext();) {
                strings[i++] = (String) map.get(iterator.next());
            }
            return strings;

        } else {
            className = trimNonClassnamePartOfString(className);
            Map<String, String> rollNameToAssociatedClassMap = domainModelAccessor.getAssociationsAndClasses(className);
            String[] associatedClassesArray = formatRoleNameClassNamePairs(className, rollNameToAssociatedClassMap);
            if (myLogger.isDebugEnabled()) {
                String msg = "Getting classes asssociated with " + className + " from domain model. Found "
                        + rollNameToAssociatedClassMap.size() + " raw classes and " + associatedClassesArray.length
                        + " classes that have direct or indirect attributes.";
                myLogger.debug(msg);
            }
            return associatedClassesArray;
        }
    }

    /**
     * @param className
     * @return
     */
    private String trimNonClassnamePartOfString(String className) {
        if (className.endsWith(" - self")) {
            return className.substring(0, className.length() - 7);
        } else {
            int index = className.indexOf(" - ");
            if (index >= 0) {
                return className.substring(index + 3);
            }
        }
        return className;
    }

    /**
     * Format the roll name &ndash; target class name pairs in the given map.
     * Each pair is formatted into a string that becomes an element of the
     * returned string array. A self-reference to the given sourceClass may also
     * appear in the returned String array.
     * 
     * @param sourceClass
     *            The name of the class that is on the source end of all the
     *            associations described in the given map.
     * 
     * @param rollNameToAssociatedClassMap
     *            a map whose keys are roll names and whose values are class
     *            names.
     * @return Format the roll name &ndash; class name pairs in the given map.
     *         <p>
     *         For the general case, each pair is formatted into a string that
     *         becomes an element of the returned string array. There is one
     *         additional element that may be added to the returned array to
     *         represent a self-reference. The format of the roll name &ndash;
     *         class name strings is
     * 
     *         <pre>
     * <i>rollName</i> - <i>className</i>
     * </pre>
     * 
     *         The format of the self-reference strings is
     * 
     *         <pre>
     * <i>sourceClassName</i> - self
     * </pre>
     * 
     *         </p>
     *         <p>
     *         The special cases arise for classes that have no attributes
     *         and/or no associations. If the sourceClass has no attributes then
     *         no self-reference will be in the returned string array. If a
     *         target class named in the map has neither associations nor
     *         attributes, then the role name &ndash; class name pairs that
     *         refer to them are left out of the returned String array. If a
     *         target class does not have attributes but does have associations,
     *         it will only be include in the returned array if the any of the
     *         classes it is associated with have attributes or they are
     *         directly or indirectly associated with a class that has
     *         attributes.
     *         </p>
     */
    private String[] formatRoleNameClassNamePairs(String sourceClass, Map<String, String> rollNameToAssociatedClassMap) {
        List<String> formattedList = new ArrayList<String>(rollNameToAssociatedClassMap.size() + 1);
        if (domainModelAccessor.classHasAttributes(sourceClass)) {
            formattedList.add(sourceClass + " - self");
        }
        HashSet<String> visitedSet = new HashSet<String>();
        visitedSet.add(sourceClass);
        Set<Map.Entry<String, String>> associationClassPairSet = rollNameToAssociatedClassMap.entrySet();
        for (Map.Entry<String, String> thisPair : associationClassPairSet) {
            String className = thisPair.getValue();
            visitedSet.add(className);
//            if (classHasDirectOrIndirectAttributes(className, cloneSet(visitedSet))) {
                formattedList.add(thisPair.getKey() + " - " + className);
//            }
        }
        return formattedList.toArray(new String[formattedList.size()]);
    }

//    @SuppressWarnings("unchecked")
//    private Set<String> cloneSet(HashSet<String> visitedSet) {
//        return (Set<String>) visitedSet.clone();
//    }

    /**
     * Return true if the named class has any attributes or if any of the
     * classes that it is directly or indirectly associated with but are not in
     * visited set have attributes.
     * 
     * @param className
     *            The name of the class to check for direct or indirect
     *            attributes.
     * @param visitedSet
     *            A set of classes that have already been visited.
     */
//    private boolean classHasDirectOrIndirectAttributes(String className, Set<String> visitedSet) {
//        if (domainModelAccessor.classHasAttributes(className)) {
//            return true;
//        }
//        Map<String, String> rollNameToAssociatedClassMap = domainModelAccessor.getAssociationsAndClasses(className);
//        Collection<String> classSet = rollNameToAssociatedClassMap.values();
//        for (String targetClass : classSet) {
//            if (!visitedSet.contains(targetClass)) {
//                visitedSet.add(targetClass);
//                if (classHasDirectOrIndirectAttributes(targetClass, visitedSet)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public FilterClause createFilterClause(String classname, String[] filters, String attribute, String classAlias, String attributeAlias)
            throws Exception {

        String filterChain = makeFilterChain(filters);
        FilterClause filter = new FilterClause();

        filter.setClassName(classname);
        filter.setFilterChain(filterChain);
        filter.setTargetClassAttributeName(attribute);
        filter.setTargetClassAttributeAlias(attributeAlias);
        filter.setTargetClassAlias(classAlias);
        String lastFilter = filters[filters.length - 1];
        filter.setTargetClassName(lastFilter);
        if (this.usingHibernate) {
            String attributeType = HibernateHelper.getAssociatedAttributeType(lastFilter.substring(0, lastFilter.indexOf(" - ")),
                    attribute, factory);
            filter.setTargetClassAttributeType(attributeType);
            filter.setGeneratedSQLForGroup(HibernateHelper.getGeneratedSQL(filter, factory, true));
            filter.setGeneratedSQLForUser(HibernateHelper.getGeneratedSQL(filter, factory, false));
        } else {
            filter.setTargetClassAttributeType("*");
        }
        if (myLogger.isInfoEnabled()) {
            String msg = "Creating new filter clause: className=\"" + filter.getClassName() + "\"; filterChain=\""
                    + filter.getFilterChain() + "\"; targetClassAttributeName=\"" + filter.getTargetClassAttributeName()
                    + "\"; targetClassAttributeAlias=\"" + filter.getTargetClassAttributeAlias() + "\"; targetClassAlias=\""
                    + filter.getTargetClassAlias() + "\"; targetClassName=\"" + filter.getTargetClassName()
                    + "\"; targetClassAttributeType=\"" + filter.getTargetClassAttributeType() + "\";";
            myLogger.info(msg);
        }
        return filter;
    }

    /**
     * Assemble the array of filters into a filter chain string. 
     * 
     * @param filters
     * @return a comma-separated string.
     */
    private String makeFilterChain(String[] filters) {
        if (filters.length == 0) {
            throw new RuntimeException("Fiter array has length zero!");
        }
        if (filters.length == 1) {
            return filters[0];
        }
        StringBuilder builder = new StringBuilder(filters[0].trim());
        for (int i = 1; i < filters.length; i++) {
            builder.append(",");
            builder.append(filters[i].trim());
        }
        return builder.toString();
    }

    @Override
    public void remove() throws ResourceException {
        super.remove();
        if (beansJar != null) {
            this.beansJar.delete();
        }
        if (ormJar != null) {
            this.ormJar.delete();
        }
    }
}
