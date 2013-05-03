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
package edu.emory.cci.cqlCsm.cqlCsmCql;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import edu.emory.cci.cqlCsm.CsmDatabaseAccess;
import edu.emory.cci.cqlCsm.Filter;
import edu.emory.cci.domainModel.DomainModelAccessor;
import edu.emory.cci.util.DeepCopier;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

/**
 * Implementation of the {@link CqlPreprocessor} interface for adding
 * constraints to CQL queries that reflect CSM authorization policies governing
 * which objects a named user can access.
 * <p>
 * This is thread-safe.
 * </p>
 * 
 * @author Mark Grand
 */
public class CqlCsmPreprocessor implements CqlPreprocessor {
    private static final Logger myLogger = LogManager.getLogger(CqlCsmPreprocessor.class);

    // This is a cache that associates authorized values for attributes of a
    // class with the name of the class. The data structure for this cache is
    // created upon entry into preprocess() and discarded before exiting
    // preprocess(). We access this cache through a ThreadLocal to avoid the
    // inconvenience of having to explicitly pass it into every method that
    // preprocess() directly or indirectly calls.
    private final ThreadLocal<Map<String, AuthorizedAttributeValues>> threadLocalCache = new ThreadLocal<Map<String, AuthorizedAttributeValues>>();

    private final DomainModelAccessor dma;

    /**
     * Public constructor for production use
     */
    public CqlCsmPreprocessor() {
        this(new DomainModelAccessor());
    }

    /**
     * Constructor for unit testing.
     * 
     * @param dma
     *            The DomainModelAccessor to use.
     */
    CqlCsmPreprocessor(DomainModelAccessor dma) {
        this.dma = dma;
        // Call CsmDatabaseAccess.getInstance() so we fail early if there is a configuration problem.
        CsmDatabaseAccess.getInstance();
        myLogger.info("CQL preprocessor initialized.");
        
    }

    /**
     * Return the set of value strings associated with the named attribute of
     * named class. If the named attribute does not have one of these values and
     * there is a CSM authorization policy limiting the user's access to objects
     * associated with the names attribute, then the named user is not
     * authorized to read objects that are associated with the attribute.
     * 
     * @param User
     *            The user that this query is for.
     * @param className
     *            That name of the class that the named attribute is an
     *            attribute of.
     * @param attributeName
     *            The name of the attribute in question.
     * @return A possibly empty set of values
     */
    private Set<String> fetchAuthorizedAttributeValues(String user, String className, String attributeName) {
        Map<String, AuthorizedAttributeValues> classAttributeCache = threadLocalCache.get();
        AuthorizedAttributeValues attributeCache = classAttributeCache.get(className);
        if (attributeCache == null) {
            attributeCache = new AuthorizedAttributeValues();
            classAttributeCache.put(className, attributeCache);
        }
        TreeSet<String> valueSet = attributeCache.getAuthorizedValues(attributeName);
        if (valueSet == null) {
            valueSet = CsmDatabaseAccess.getInstance().getAuthorizedAttributeValues(user, className, attributeName);
            attributeCache.set(attributeName, valueSet);
        }
        return valueSet;
    }

    /**
     * Pre-process/re-write/transform a given CQL query to include constraints
     * implied by CSM authorization policies so that it will not fetch or
     * acknowledge the existence of objects that the user is not authorized to
     * read.
     * 
     * @param query
     *            The query to be pre-processed.
     * @param user
     *            The user identity for which authorization policies should be
     *            applied.
     * @see edu.emory.cci.cqlCsm.cqlCsmCql.CqlPreprocessor#preprocess(gov.nih.nci.cagrid.cqlquery.CQLQuery,
     *      java.lang.String)
     */
    public CQLQuery preprocess(CQLQuery query, String user) {
        if (myLogger.isInfoEnabled()) {
            myLogger.info("preprocessing query for user: " + user);
        }
        System.out.println("Preprocessor input is\n" + serializeQueryToString(query));
        CQLQuery preprocessedQuery;
        try {
            preprocessedQuery = (CQLQuery) DeepCopier.deepCopy(query);
        } catch (IOException e) {
            String msg = "Error occurred while copying CQL query";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        threadLocalCache.set(new HashMap<String, AuthorizedAttributeValues>());
        try {
            processTarget(preprocessedQuery.getTarget(), user);
        } finally {
            threadLocalCache.set(null);
        }
        System.out.println("Preprocessor output is\n" + serializeQueryToString(preprocessedQuery));
        return preprocessedQuery;
    }

    /**
     * Pre-process/re-write/transform a given CQL target to include constraints
     * implied by CSM authorization policies so that it will not fetch or
     * acknowledge the existence of objects that the user is not authorized to
     * read.
     * 
     * @param target
     *            The target of a CQL query
     * @param user
     *            The user identity for which authorization policies should be
     *            applied.
     */
    private void processTarget(Object target, String user) {
        String className = target.getName();
        myLogger.debug("Processing target " + className);
        if (!dma.isExposedClass(className)) {
            String[] knownClassNames = dma.getUMLClassNames();
            String msg = "Target class for query is not a known class: " + className + "\n known class names are:"
                    + Arrays.toString(knownClassNames);
            RuntimeException excp = new RuntimeException(msg);
            excp.fillInStackTrace();
            myLogger.error(msg, excp);
            throw excp;
        }
        Iterator<Filter> classFilters = filtersForClass(className);
        try {
            addFilterConstraintsToObject(className, classFilters, target, user);
            if (target.getAssociation() != null) {
                processAssociation(className, target.getAssociation(), user);
            }
            // Attributes are irrelevant to CSM pre-processing, so ignore them.
            if (target.getGroup() != null) {
                processGroup(className, target.getGroup(), user);
                
                 // PATCH : This block of code will fix the issue when there is only one attribute inside a Group i.e invalid cal query => Author : Nadir
                
                if(target.getGroup().getAttribute().length == 1 && target.getGroup().getAssociation() == null && target.getGroup().getGroup() == null)
                {
                	Attribute attr = target.getGroup().getAttribute(0);
                	target.setGroup(null);
                	target.setAttribute(attr);
                }
                
               //------------------------------------------------------------------------------------------------------------------------------------------------
            }
        } catch (NoAuthorizationException e) {
            // This exception means that the user has no authorization to access
            // the target class of the query, so as an optimization we replace
            // the actual query with a simple query that returns no objects.
            target.setAssociation(null);
            target.setAttribute(null);
            
            String[] targetAttributeNames = dma.getAttributeNames(className);
            String targetAttributeName;
            if (targetAttributeNames.length > 0) {
                targetAttributeName = targetAttributeNames[0]; 
            } else {
                targetAttributeName = CsmDatabaseAccess.getInstance().getCsmProperties().getDefaultTargetAttributeName();
                if (targetAttributeName.length() == 0) {
                    throw new RuntimeException("Not authorized", e);
                }
            }
            target.setGroup(alwaysFalseGroup(targetAttributeName));
        }
    }

    /**
     * Pre-process/re-write/transform a given CQL group to include constraints
     * implied by CSM authorization policies so that it will not fetch or
     * acknowledge the existence of objects that the user is not authorized to
     * read.
     * 
     * @param target
     *            The target of a CQL query
     * @param user
     *            The user identity for which authorization policies should be
     *            applied.
     */
    private void processGroup(String parentClassName, Group group, String user) throws NoAuthorizationException {
        // The authorization constraints on a group are the constraints on its
        // children. A group does not have any of its own authorization
        // constraints.
        Association[] associations = group.getAssociation();
        if (group.getLogicRelation().equals(LogicalOperator._AND)) {
            // AND group
            if (associations != null) {
                for (int i = 0; i < associations.length; i++) {
                    processAssociation(parentClassName, associations[i], user);
                }
            }
            // Attributes are irrelevant to CSM pre-processing, so ignore them.
            if (group.getGroup() != null) {
                for (int i = 0; i < group.getGroup().length; i++) {
                    processGroup(parentClassName, group.getGroup(i), user);
                }
            }
        } else {
            // OR group
            if (associations != null) {
                for (int i = 0; i < associations.length; i++) {
                    try {
                        processAssociation(parentClassName, associations[i], user);
                    } catch (NoAuthorizationException e) {
                        // Handle children that will always be false by removing
                        // them from this OR group.
                        associations = dropArrayElement(associations, i);
                        group.setAssociation(associations.length > 0 ? associations : null);
                        i -= 1;
                    }
                }
            }
            // Attributes are irrelevant to CSM pre-processing, so ignore them.
            Group[] groups = group.getGroup();
            if (groups != null) {
                for (int i = 0; i < groups.length; i++) {
                    try {
                        processGroup(parentClassName, groups[i], user);
                    } catch (NoAuthorizationException e) {
                        // Handle children that will always be false by removing
                        // them from this OR group.
                        groups = dropArrayElement(group.getGroup(), i);
                        group.setGroup(groups.length > 0 ? groups : null);
                        i -= 1;
                    }
                }
            }
            // If this group has no children, it must be because the user in not
            // authorized to see any of them. Therefore the user is not
            // authorized to see the group.
            if ((group.getAssociation() == null || group.getAssociation().length == 0)
                    && (group.getAttribute() == null || group.getAttribute().length == 0)
                    && (group.getGroup() == null || group.getGroup().length == 0)) {
                throw new NoAuthorizationException();
            }
        }

    }

    /**
     * Return an array whose length is one less than the given array that has
     * the same content as the given array exception for the element at the
     * specified index.
     * 
     * @param oldArray
     *            The larger array to be copied.
     * @param ndx
     *            The index of the element not to copy.
     * @return the shorter array.
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] dropArrayElement(T[] oldArray, int ndx) {
        int newLength = oldArray.length - 1;
        T[] newArray = (T[]) Array.newInstance(oldArray.getClass().getComponentType(), newLength);
        for (int i = 0; i < ndx; i++) {
            newArray[i] = oldArray[i];
        }
        for (int i = ndx; i < newLength; i++) {
            newArray[i] = oldArray[i + 1];
        }
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] concatenateToArray(T[] array, T obj) {
        if (array == null) {
            array = (T[]) Array.newInstance(obj.getClass(), 1);
            array[0] = obj;
            return array;
        }
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = obj;
        return newArray;
    }

    /**
     * Pre-process/re-write/transform a given CQL target to include constraints
     * implied by CSM authorization policies so that it will not fetch or
     * acknowledge the existence of objects that the user is not authorized to
     * read.
     * 
     * @param fromClassName
     *            The name of the class on the from end of the association.
     * 
     * @param association
     *            The CQL association to process The user identity for which
     *            authorization policies should be applied.
     */
    private void processAssociation(String fromClassName, Association association, String user) throws NoAuthorizationException {
        String toClassName = associationToClass(fromClassName, association.getRoleName());
        Iterator<Filter> classFilters = filtersForClass(toClassName);
        addFilterConstraintsToObject(fromClassName, classFilters, association, user);
        if (association.getAssociation() != null) {
            processAssociation(toClassName, association.getAssociation(), user);
        }
        // Attributes are irrelevant to CSM pre-processing, so ignore them.
        if (association.getGroup() != null) {
            processGroup(toClassName, association.getGroup(), user);
        }
    }

    /**
     * Add constraints to the given object (target or association) that enforce
     * the given filters for the given user.
     * 
     * @param fromClassName
     *            The name of the class that the filter is being applied to.
     * @param classFilters
     *            Filter objects that describe constraints on which objects the
     *            user is authorized to access.
     * @param obj
     *            The target to apply these filters to
     * @param user
     *            the user for which the filters are being applied.
     * @throws NoAuthorizationException
     *             If it can be determined while pre-processing that the named
     *             user is not authorized to access any instances of the class
     *             that is the target of a filter.
     */
    private void addFilterConstraintsToObject(String fromClassName, Iterator<Filter> classFilters, Object obj, String user)
            throws NoAuthorizationException {
        if (classFilters.hasNext()) { // If there are any filters
            // First ensure that the object's child is an "AND" group.
            Group andGroup;
            Group childGroup = obj.getGroup();
            if (childGroup != null && childGroup.getLogicRelation().equals(LogicalOperator.AND)) {
                // If the child is an AND group then use it.
                andGroup = childGroup;
            } else {
                // If no child AND group then insert an AND group between this
                // object and its children.
                Association[] associations = (obj.getAssociation() == null ? null : new Association[] { obj.getAssociation() });
                Attribute[] attributes = (obj.getAttribute() == null ? null : new Attribute[] { obj.getAttribute() });
                Group[] groups = (childGroup == null ? null : new Group[] { obj.getGroup() });
                andGroup = new Group(associations, attributes, groups, LogicalOperator.AND);
                obj.setGroup(andGroup);
                obj.setAssociation(null);
                obj.setAttribute(null);
            }
            do {
                Filter thisFilter = classFilters.next();
                Set<String> valueSet = fetchAuthorizedAttributeValues(user, thisFilter.getTargetClassName(), thisFilter.getAttributeName());
                Object cqlObject = filterToCql(fromClassName, thisFilter, valueSet);
                if (cqlObject.getAssociation() != null) {
                    Association[] associations = concatenateToArray(andGroup.getAssociation(), cqlObject.getAssociation());
                    andGroup.setAssociation(associations);
                }
                if (cqlObject.getAttribute() != null) {
                    Attribute[] attributes = concatenateToArray(andGroup.getAttribute(), cqlObject.getAttribute());
                    andGroup.setAttribute(attributes.length > 0 ? attributes : null);
                }
                if (cqlObject.getGroup() != null) {
                    Group[] groups = concatenateToArray(andGroup.getGroup(), cqlObject.getGroup());
                    andGroup.setGroup(groups.length > 0 ? groups : null);
                }
            } while (classFilters.hasNext());
        }
    }

    /**
     * Create an Attribute, Association or Group that implements the given CSM
     * filter for the given set of values.
     * 
     * @param fromClassName
     *            The name of the class that the filter is being applied to.
     * @param thisFilter
     *            The CSM filter to translate into CQL.
     * @param valueSet
     *            The set of values that are authorized for the filter.
     * @return a CQL object that has as its child the created Attribute,
     *         Association or Group.
     * @throws NoAuthorizationException
     *             if the value set is empty
     */
    private Object filterToCql(String fromClassName, Filter thisFilter, Set<String> valueSet) throws NoAuthorizationException {
        if (valueSet.isEmpty()) {
            // No values means no authorization.
            throw new NoAuthorizationException();
        }
        String[] associationPath = thisFilter.getAssociationPath();
        if (valueSet.size() == 1) { // single value
            Attribute valueAttribute = new Attribute(thisFilter.getAttributeName(), Predicate.EQUAL_TO, valueSet.iterator().next());
            if (associationPath.length == 0) { // One value, no associations
                return new Object(null, valueAttribute, null, null);
            } else { // One value through associations
                Association assoc = new Association(associationPath[associationPath.length - 1]);
                assoc.setAttribute(valueAttribute);
                assoc = realizeFilterCQL(fromClassName, thisFilter, associationPath, assoc);
                return new Object(assoc, null, null, null);
            }
        } else { // multiple values
            Attribute[] valueAttributes = new Attribute[valueSet.size()];
            int g = 0;
            Iterator<String> valueIterator = valueSet.iterator();
            do {
                valueAttributes[g] = new Attribute(thisFilter.getAttributeName(), Predicate.EQUAL_TO, valueIterator.next());
                g += 1;
            } while (valueIterator.hasNext());
            Group valueGroup = new Group(null, valueAttributes, null, LogicalOperator.OR);
            if (associationPath.length == 0) { // multiple values, no
                // associations
                return new Object(null, null, valueGroup, null);
            } else { // Multiple values through associations.
                Association assoc = new Association(associationPath[associationPath.length - 1]);
                assoc.setGroup(valueGroup);
                assoc = realizeFilterCQL(fromClassName, thisFilter, associationPath, assoc);
                return new Object(assoc, null, null, null);
            }
        }
    }

    /**
     * @param fromClassName
     *            The name of the class that the filter is being applied to.
     * @param thisFilter
     * @param associationPath
     * @param assoc
     * @return
     */
    private Association realizeFilterCQL(String fromClassName, Filter thisFilter, String[] associationPath, Association assoc) {
        for (int i = associationPath.length - 2; i >= 0; i--) {
            String associationName = associationPath[i];
            Association parentAssociation = new Association(associationName);
            parentAssociation.setAssociation(assoc);
            assoc = parentAssociation;
        }
        setAssociationNameToTargetClass(fromClassName, assoc, thisFilter);
        return assoc;
    }

    private void setAssociationNameToTargetClass(String fromClassName, Association assoc, Filter thisFilter) {
        if (assoc != null) {
            String toClassName = dma.getAssociatedClass(fromClassName, assoc.getRoleName());
            if (toClassName == null) {
                String msg = "Configured CSM filter is inconsistent with object model. Association path is \""
                        + thisFilter.getAssociationPathString() + "\". Class " + fromClassName
                        + " has no association leading from it with the role name \"" + assoc.getRoleName() + "\"";
                myLogger.error(msg);
                throw new RuntimeException(msg);
            }
            assoc.setName(toClassName);
            setAssociationNameToTargetClass(toClassName, assoc.getAssociation(), thisFilter);
        }
    }

    /**
     * Given a class and a role name that identifies an association, return the
     * name of the class on the other end of the association.
     * 
     * @param fromClassName
     *            The name of the class that is know to be connected to the
     *            association.
     * @param roleName
     *            The role name that identifies the association.
     * @return the name of the class on the other end of the association.
     * @throws RuntimeException
     *             if the class on the other end of the association cannot be
     *             determined.
     */
    private String associationToClass(String fromClassName, String roleName) {
        String toClassName = dma.getAssociatedClass(fromClassName, roleName);
        if (toClassName == null) {
            String msg;
            if (dma.isExposedClass(fromClassName)) {
                msg = "Unknown association " + roleName + " from class " + fromClassName;
            } else {
                msg = "Query references unknown class: " + fromClassName;
            }
            myLogger.warn(msg);
            throw new RuntimeException(msg);
        }
        return toClassName;
    }

    /**
     * Return filters for the named class and its superclasses.
     * 
     * @param className
     *            The name of the class to get filters for.
     * @return filters for the named class and its superclasses.
     */
    private Iterator<Filter> filtersForClass(String className) {
        CsmDatabaseAccess cda = CsmDatabaseAccess.getInstance();
        return cda.getFiltersForClass(className).iterator();
    }

    /**
     * Return a CQL group that contains logic that references a named attribute
     * and is always false.
     * 
     * @param attributeName
     *            The name of teh attribute to refer to.
     * @return A group that will always be false.
     */
    private Group alwaysFalseGroup(String attributeName) {
        return new Group(null, alwaysFalseAttributes(attributeName), null, LogicalOperator.AND);
    }

    private Attribute[] alwaysFalseAttributes(String attributeName) {
        Attribute nullAttribute = new Attribute(attributeName, Predicate.IS_NULL, null);
        Attribute notNullAttribute = new Attribute(attributeName, Predicate.IS_NOT_NULL, null);
        return new Attribute[] { nullAttribute, notNullAttribute };
    }

    /**
     * This checked exception is thrown by methods responsible for adding
     * constraints to a part of a CQL query. Methods that catch this exception
     * are prepared to re-write the part of the CQL query they are responsible
     * for so that it does not match anything. This is done as an optimization
     * to avoid issuing queries for classes for which ther are no instances that
     * the user is allowed to see.
     * 
     * @author Mark Grand
     */
    private class NoAuthorizationException extends Exception {
        private static final long serialVersionUID = -6194554886924816593L;

        NoAuthorizationException() {
            super();
        }
    }

    private String serializeQueryToString(CQLQuery query) {
        Writer outputWriter = new StringWriter();
        try {
            ObjectSerializer.serialize(outputWriter, query, new javax.xml.namespace.QName("cql"));
        } catch (SerializationException e) {
            myLogger.error("Error serializing query for logging", e);
        }
        return outputWriter.toString();
    }
}
