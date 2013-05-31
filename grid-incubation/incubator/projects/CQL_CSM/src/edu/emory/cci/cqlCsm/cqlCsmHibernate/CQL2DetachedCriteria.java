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
/**
 */
package edu.emory.cci.cqlCsm.cqlCsmHibernate;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.emory.cci.cqlCsm.CsmDatabaseAccess;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.sdkquery4.processor.RoleNameResolver;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.dao.orm.translator.gridCQL.DomainTypesInformationUtil;
import gov.nih.nci.system.util.ClassCache;

/**
 * Convert a CQL query into a hibernate detached query that is subjected to
 * constraints implied by CSM tables.
 * <p>
 * The interpretation of the security data in the CSM tables differs from the
 * CSM API in these respects:
 * <ul>
 * <li>Filters for both users and groups are applied. This means that users will
 * be considered authorized to access data if the filters for either individual
 * users or for groups are satisfied. The CSM API will consider either user
 * filters or group filters but not both.</li>
 * <li>Filters are included in the query for all classes involved in the query,
 * not just the target class. This prevents queries that probe for the existence
 * of data that the user is not authorized to retrieve. The CSM API only applies
 * filters that are associated with the target class.</li>
 * </ul>
 * <p>
 * Like the CSM API, if there are no filters associated with a class, then no
 * authorization is required to access the contents of the corresponding table.
 * 
 * @author Mark Grand
 */
class CQL2DetachedCriteria {
    private static final Logger myLogger = LogManager.getLogger(CQL2DetachedCriteria.class);

    // The presumed property name for an objects id (property corresponding to
    // the primary key.
    private static final String ID = "id";

    // maps a CQL predicate to the method that implements the equivalent
    // hibernate criteria restriction.
    private Map<Predicate, CriteriaPredicate> predicateRestrictions = null;

    // A counter used to ensure that SQL aliases are unique, even if the same
    // association is traversed more then once.
    private volatile int aliasCounter = 0;
    
    /**
     * Per Thread map from strings of the form alias.property to a corresponding alias.
     * @see CQL2DetachedCriteria#aliasForAssociation(DetachedCriteria, String, String)
     */
    private ThreadLocal<Map<String, String>> subEntityToAliasMap = new ThreadLocal<Map<String,String>>();

    private DomainTypesInformationUtil typesInfoUtil = null;
    private RoleNameResolver roleNameResolver = null;
    private boolean caseInsensitive;

    /**
     * Constructor
     * 
     * @param predicateRestrictions
     * @param roleNameResolver
     * @param caseInsensitive
     * @throws CSConfigurationException
     *             If there is a configuration problem.
     */
    public CQL2DetachedCriteria(ClassCache cache, RoleNameResolver roleNameResolver, boolean caseInsensitive) throws ApplicationException {
        super();
        this.typesInfoUtil = new DomainTypesInformationUtil(cache);
        this.roleNameResolver = roleNameResolver;
        this.caseInsensitive = caseInsensitive;
        initPredicateValues();
    }

    /**
     * Create mapping from CQL predicate names to method objects that wrap the
     * corresponding static method in hibernate's
     * <tt>org.hibernate.criterion.Restrictions</tt> class.
     */
    private void initPredicateValues() {
        predicateRestrictions = new HashMap<Predicate, CriteriaPredicate>();
        try {
            predicateRestrictions.put(Predicate.EQUAL_TO, new CriteriaPredicate(false, Restrictions.class.getMethod(
                    "eq", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.GREATER_THAN, new CriteriaPredicate(false, Restrictions.class
                    .getMethod("gt", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.GREATER_THAN_EQUAL_TO, new CriteriaPredicate(false, Restrictions.class
                    .getMethod("ge", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.LESS_THAN, new CriteriaPredicate(false, Restrictions.class.getMethod(
                    "lt", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.LESS_THAN_EQUAL_TO, new CriteriaPredicate(false, Restrictions.class
                    .getMethod("le", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.LIKE, new CriteriaPredicate(false, Restrictions.class.getMethod("like",
                    String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.NOT_EQUAL_TO, new CriteriaPredicate(false, Restrictions.class
                    .getMethod("ne", String.class, java.lang.Object.class)));
            predicateRestrictions.put(Predicate.IS_NOT_NULL, new CriteriaPredicate(true, Restrictions.class.getMethod(
                    "isNotNull", String.class)));
            predicateRestrictions.put(Predicate.IS_NULL, new CriteriaPredicate(true, Restrictions.class.getMethod(
                    "isNull", String.class)));
        } catch (SecurityException e) {
            String msg = "Security problem initializing predicate restrictions";
            myLogger.fatal(msg, e);
            throw new RuntimeException(msg, e);
        } catch (NoSuchMethodException e) {
            String msg = "Problem initializing predicate restrictions";
            myLogger.fatal(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Converts CQL to a hibernate DetachedCriteria
     * 
     * @param query
     *            The query to convert
     * @param userId
     *            A string that identifies a login_id value in the CSM_USER
     *            table.
     * @param sess
     *            The hibernate session to use.
     * @return A parameterized HQL Query representing the CQL query
     * @throws QueryProcessingException
     */
    public DetachedCriteria convertToDetachedCriteria(CQLQuery query, String userId, Session sess)
            throws QueryProcessingException {
        if (myLogger.isDebugEnabled()){
            try {
                StringWriter outputWriter = new StringWriter();
                outputWriter.write("Query is:\n");
                ObjectSerializer.serialize(outputWriter, query, new javax.xml.namespace.QName("cql"));
                outputWriter.flush();
                myLogger.debug(outputWriter.toString());
            } catch (SerializationException e) {
                myLogger.warn("Error occured while trying to format CQL query for logging", e);
            }
        }
        boolean hasSubclasses = targetHasSubclasses(query);

        // begin processing at the target level
        DetachedCriteria criteriaQuery;
        try {
            subEntityToAliasMap.set(new HashMap<String, String>());
            criteriaQuery = processTarget(query.getTarget(), hasSubclasses, userId, sess);
        } finally {
            subEntityToAliasMap.set(null);
        }

        // apply query modifiers
        if (query.getQueryModifier() != null) {
            handleQueryModifier(query.getQueryModifier(), criteriaQuery);
        } else {
            // select only unique objects
            criteriaQuery.setProjection(Projections.distinct(Projections.property(ID)));
        }
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Processed query is: " + criteriaQuery.toString());
        }
        return criteriaQuery;
    }

    private boolean targetHasSubclasses(CQLQuery query) {
        List<String> subclasses = typesInfoUtil.getSubclasses(query.getTarget().getName());
        boolean hasSubclasses = subclasses != null && subclasses.size() > 0;
        if (myLogger.isDebugEnabled()) {
            myLogger.debug(query.getTarget().getName()
                    + (hasSubclasses ? " has " + subclasses.size() + " subclasses" : " has no subclasse"));
        }
        return hasSubclasses;
    }

    /**
     * Applies query modifiers to the HQL query
     * 
     * @param mods
     *            The modifiers to apply
     * @param criteriaQuery
     *            The criteria query to apply the modifications to
     */
    private void handleQueryModifier(QueryModifier mods, DetachedCriteria criteriaQuery) {
        if (mods.isCountOnly()) {
            if (mods.getDistinctAttribute() != null) {
                criteriaQuery.setProjection(Projections.countDistinct(mods.getDistinctAttribute()));
            } else {
                criteriaQuery.setProjection(Projections.countDistinct(ID));
            }
        } else {
            if (mods.getDistinctAttribute() != null) {
                criteriaQuery.setProjection(Projections.distinct(Projections.property(mods.getDistinctAttribute())));
            } else if (mods.getAttributeNames().length == 0) {
                criteriaQuery.setProjection(Projections.distinct(Projections.property(ID)));
            } else {
                ProjectionList attributeNames = Projections.projectionList();
                for (int i = 0; i < mods.getAttributeNames().length; i++) {
                    attributeNames.add(Projections.property(mods.getAttributeNames(i)));
                }
                criteriaQuery.setProjection(attributeNames);
            }
        }
    }

    /**
     * Processes the target object of a CQL query
     * 
     * @param target
     *            The target of a CQL query
     * @param avoidSubclasses
     *            A flag to indicate the target has subclasses, which we should
     *            not return
     * @param loginName
     *            The string that identifies the user.
     * @param sess
     *            The hibernate session to use.
     * @throws QueryProcessingException
     */
    private DetachedCriteria processTarget(Object target, boolean avoidSubclasses, String loginName, Session sess)
            throws QueryProcessingException {
        String className = target.getName();
        myLogger.debug("Processing target " + className);

        // Fetch the names of the CSM groups that the user belongs to.
        String groupNames = userGroupsString(sess, loginName);

        // start the query
        DetachedCriteria criteriaQuery = DetachedCriteria.forEntityName(className);
        Junction logic = Restrictions.conjunction();
        if (target.getAssociation() != null) {
            logic.add(processAssociation(target.getAssociation(), criteriaQuery, target, sess, loginName, groupNames,
                    CriteriaSpecification.ROOT_ALIAS));
        }
        if (target.getAttribute() != null) {
            logic.add(processAttribute(target.getAttribute(), criteriaQuery, target, CriteriaSpecification.ROOT_ALIAS));
        }
        if (target.getGroup() != null) {
            logic.add(processGroup(target.getGroup(), criteriaQuery, target, sess, loginName, groupNames,
                    CriteriaSpecification.ROOT_ALIAS));
        }
        if (avoidSubclasses) {
            logic.add(Restrictions.eq("class", Integer.valueOf(0)));
        }
        logic.add(addCsmClassConstraints(criteriaQuery, className, sess, loginName, groupNames,
                CriteriaSpecification.ROOT_ALIAS));
        criteriaQuery.add(logic);
        return criteriaQuery;
    }

    /**
     * Fetch the names of the CSM groups that the user belongs to as a comma
     * separated sequence of SQL string literals.
     * 
     * @param sess
     *            The hibernate session to use.
     * @param loginName
     *            the user's login name.
     * @return A string containing comma separated SQL string literals or null.
     */
    private String userGroupsString(Session sess, String loginName) {
        Set<String> groups = fetchUserGroups(sess, loginName);
        if (groups.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = groups.iterator();
        builder.append('\'');
        builder.append(iter.next().replace("'", "''"));
        builder.append('\'');
        while (iter.hasNext()) {
            builder.append(',');
            builder.append('\'');
            builder.append(iter.next().replace("'", "''"));
            builder.append('\'');
        }
        return builder.toString();
    }

    /**
     * Fetch the names of the CSM groups that the user belongs to.
     * 
     * @param sess
     *            The hibernate session to use.
     * @param loginName
     *            the user's login name.
     * @return A possibly empty set of group names.
     */
    private Set<String> fetchUserGroups(Session sess, String loginName) {
        String queryString = "select distinct g.groupName from Group g"
                + " where g.application.applicationId=:app and g.users.loginName=:loginName";
        Query q = sess.createQuery(queryString);
        q.setLong("app", CsmDatabaseAccess.getInstance().fetchApplicationId());
        q.setString("loginName", loginName);
        List<java.lang.Object> results = resultList(q);
        Set<String> groups = new HashSet<String>();
        for (Iterator<java.lang.Object> iter = results.iterator(); iter.hasNext();) {
            java.lang.Object groupNameObject = iter.next();
            String groupName = (String) groupNameObject;
            if (groupName.length() > 0) {
                groups.add(groupName);
            }
        }
        return groups;
    }

    /**
     * @param q
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<java.lang.Object> resultList(Query q) {
        return q.list();
    }

    @SuppressWarnings("unchecked")
    private List<java.lang.Object[]> resultArrayList(Query q) {
        return q.list();
    }

    /**
     * Processes a CQL query attribute into HQL
     * 
     * @param attribute
     *            The CQL attribute
     * @param criteriaQuery
     *            The criteriaQuery that is being built
     * @param sourceQueryObject
     *            The object that the association to be processed is from.
     * @param alias
     *            The alias to use for qualifying properties.
     * @return A Criterion object that represents the constraints implied by the
     *         attribute.
     * @throws QueryProcessingException
     */
    private Criterion processAttribute(Attribute attribute, DetachedCriteria criteriaQuery, Object sourceQueryObject,
            String alias) throws QueryProcessingException {
        myLogger.debug("Processing attribute " + sourceQueryObject.getName() + "." + attribute.getName());

        Predicate thisPredicate = getActualComparisonPredicateOrDefault(attribute);
        String attributeValue = attribute.getValue();
        if (attributeValue == null) {
            throw new QueryProcessingException("No value specified for attribute " + attribute.getName());
        }
        CriteriaPredicate predicateRestriction = predicateRestrictions.get(thisPredicate);
        String qualifiedAttributeName = alias + "." + attribute.getName();
        try {
            if (predicateRestriction.isUnary) {
                return (Criterion) predicateRestriction.restrictionMethod.invoke(null, qualifiedAttributeName);
            } else {
                // determine the Java type of the field being processed
                String attributeFieldType = determineAttributeFieldType(attribute, sourceQueryObject);

                // convert the attribute value to a Java value
                java.lang.Object value = toJavaValue(attributeValue, attributeFieldType);

                return (Criterion) predicateRestriction.restrictionMethod.invoke(null, qualifiedAttributeName, value);
            }
        } catch (Exception e) {
            String msg = "Problem processing predicate " + attribute.getPredicate() + " for " + attribute.getName()
                    + "with value " + attribute.getValue();
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * @param attribute
     * @return
     */
    private Predicate getActualComparisonPredicateOrDefault(Attribute attribute) {
        Predicate thisPredicate = attribute.getPredicate();
        if (thisPredicate == null) {
            // No predicate specified, so use default.
            thisPredicate = Predicate.EQUAL_TO;
        }
        return thisPredicate;
    }

    /**
     * Convert the value string of a CQL attribute to a Java value.
     * 
     * @param attributeValue
     *            String representation of the attribute's value.
     * @param attributeFieldType
     *            The type of the attribute's value.
     * @return A Java object that contains that attribute's value.
     * @throws QueryProcessingException
     *             if there is a problem.
     */
    private java.lang.Object toJavaValue(String attributeValue, String attributeFieldType)
            throws QueryProcessingException {
        java.lang.Object value = valueToObject(attributeFieldType, caseInsensitive ? attributeValue.toLowerCase()
                : attributeValue);
        return value;
    }

    /**
     * @param attribute
     * @param sourceQueryObject
     * @return
     * @throws QueryProcessingException
     */
    private String determineAttributeFieldType(Attribute attribute, Object sourceQueryObject)
            throws QueryProcessingException {
        String attributeFieldType = typesInfoUtil
                .getAttributeJavaType(sourceQueryObject.getName(), attribute.getName());
        if (attributeFieldType == null) {
            throw new QueryProcessingException("Field type of " + sourceQueryObject.getName() + "."
                    + attribute.getName() + " could not be determined");
        }
        myLogger.debug("Attribute found to be of type " + attributeFieldType);
        return attributeFieldType;
    }

    /**
     * Processes CQL associations into HQL
     * 
     * @param association
     *            The CQL association
     * @param criteriaQuery
     *            The criteriaQuery that is being built
     * @param sourceQueryObject
     *            The object that the association to be processed is from.
     * @param sess
     *            The hibernate session used to access the database.
     * @param loginName
     *            A string that identifies the user in the csm_user table.
     * @param groupNames
     *            A string containing a comma-separated list of string literals
     *            that are the names of CSM groups the user is a member of or
     *            null.
     * @param sourceAlias
     *            An alias to identify the entity that this association is from.
     * @return A Criterion object that represents the constraints implied by the
     *         association.
     * @throws QueryProcessingException
     *             if there is a problem.
     */
    private Criterion processAssociation(Association association, DetachedCriteria criteriaQuery,
            Object sourceQueryObject, Session sess, String loginName, String groupNames, String sourceAlias)
            throws QueryProcessingException {
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Processing association " + sourceQueryObject.getName() + " to " + association.getName());
        }
        Junction logic = Restrictions.conjunction();

        // get the association's role name
        String associationRoleName = determineAssociationRoleName(association, sourceQueryObject);
        myLogger.debug("Role name determined to be " + associationRoleName);

        String destinationAlias = aliasForAssociation(criteriaQuery, sourceAlias, associationRoleName);

        // flag indicates query is only verifying that association is populated
        boolean simpleNullCheck = true;
        if (association.getAssociation() != null) {
            simpleNullCheck = false;
            logic.add(processAssociation(association.getAssociation(), criteriaQuery, association, sess, loginName,
                    groupNames, destinationAlias));
        }
        if (association.getAttribute() != null) {
            simpleNullCheck = false;
            logic.add(processAttribute(association.getAttribute(), criteriaQuery, association, destinationAlias));
        }
        if (association.getGroup() != null) {
            simpleNullCheck = false;
            logic.add(processGroup(association.getGroup(), criteriaQuery, association, sess, loginName, groupNames,
                    destinationAlias));
        }
        if (simpleNullCheck) {
            // query is checking for the association to exist and be non-null
            criteriaQuery.add(Restrictions.isNotEmpty(associationRoleName));
        }
        logic.add(addCsmClassConstraints(criteriaQuery, association.getName(), sess, loginName, groupNames,
                destinationAlias));
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Done processing association " + sourceQueryObject.getName() + " to "
                    + association.getName());
        }
        return logic;
    }

    /**
     * Return an alias for the specified association. If no such alias already
     * exists in the given create on in the given DetachedCriteria.
     * <p>
     * We avoid creating two aliases for the same association path. All
     * references to the same association path should be translated to the same
     * item in the SQL select statement's for list and the syntax for SQL allows
     * each item in a for list can have only one alias.
     * 
     * @param criteriaQuery
     *            The {@linkplain DetachedCriteria} to use for creating a new
     *            alias.
     * @param sourceAlias
     *            The root alias or the alias of the from item that the
     *            association is from.
     * @param associationRoleName
     *            The role name that identifies this association.
     * @return the found or created alias.
     */
    private String aliasForAssociation(DetachedCriteria criteriaQuery, String sourceAlias, String associationRoleName) {
        String sourceAliasDotRoleName = sourceAlias + "." + associationRoleName;
        String existingAlias = subEntityToAliasMap.get().get(sourceAliasDotRoleName);
        if (existingAlias != null) {
            return existingAlias;
        }
        String newAlias = nextTableAlias();
        criteriaQuery.createAlias(sourceAliasDotRoleName, newAlias);
        subEntityToAliasMap.get().put(sourceAliasDotRoleName, newAlias);
        return newAlias;
    }

    /**
     * Add constraints to the boolean expression that will be produced by this
     * object. The constrains will come from filters associated with the given
     * class name. The constraints will be anded to each other and any
     * constraints previously added to this object.
     * 
     * @param criteriaQuery
     *            The object that the association to be processed is from.
     * @param className
     *            The class name to use to retrieve CSM filters from the
     *            database.
     * @param sess
     *            Session used to retrieve CSM security information.
     * @param loginName
     *            A string that identifies the user
     * @param groupNames
     *            A string that contains comma separated string literals that
     *            are the names of groups that the user belongs to or null.
     * @param alias
     *            An alias to use for the table to which constraints are to be
     *            applied.
     * @return A Criterion object that include all of the relevant SQL filters
     *         anded together.
     */
    private Criterion addCsmClassConstraints(DetachedCriteria criteriaQuery, String className, Session sess,
            String loginName, String groupNames, String alias) {
        String queryString = "select generatedSQLForUser,generatedSQLForGroup"
                + " from FilterClause filter where filter.application.id = :appId and filter.className = :className";
        Query q = sess.createQuery(queryString);
        long appId = CsmDatabaseAccess.getInstance().fetchApplicationId();
        q.setLong("appId", appId);
        q.setString("className", className);
        List<java.lang.Object[]> rawFilterPairs = resultArrayList(q);
        Junction logic = Restrictions.disjunction();
        for (java.lang.Object[] rawFilterPair : rawFilterPairs) {
            String userFilter = (String) rawFilterPair[0];
            String groupFilter = (String) rawFilterPair[1];
            userFilter = userFilter.replace(":APPLICATION_ID", Long.toString(appId));
            userFilter = userFilter.replace(":USER_NAME", "'" + loginName.replace("'", "''") + "'");
            userFilter = userFilter.replace("table_name_csm_", nextTableAlias());
            logic.add(new CSMFilterSqlCriterion(alias, userFilter));
            if (groupNames != null) {
                groupFilter = groupFilter.replace(":APPLICATION_ID", Long.toString(appId));
                groupFilter = groupFilter.replace(":GROUP_NAMES", groupNames);
                groupFilter = groupFilter.replace("table_name_csm_", nextTableAlias());
                logic.add(new CSMFilterSqlCriterion(alias, groupFilter));
            }
        }
        return logic;
    }

    /**
     * Return a unique alias to use for a table in a CSM filter sub-query
     */
    private String nextTableAlias() {
        aliasCounter += 1;
        return "F" + aliasCounter;
    }

    /**
     * @param association
     * @param sourceQueryObject
     * @return
     * @throws QueryProcessingException
     */
    private String determineAssociationRoleName(Association association, Object sourceQueryObject)
            throws QueryProcessingException {
        String associationRoleName = roleNameResolver.getRoleName(sourceQueryObject.getName(), association);
        if (associationRoleName == null) {
            // still null?? no association to the object!
            // TODO: should probably be malformed query exception
            throw new QueryProcessingException("Association from type " + sourceQueryObject.getName() + " to type "
                    + association.getName() + " does not exist.  Use only direct associations");
        }
        return associationRoleName;
    }

    /**
     * Processes a CQL group into HQL
     * 
     * @param group
     *            The CQL Group
     * @param criteriaQuery
     *            The criteriaQuery that is being built
     * @param sourceQueryObject
     *            The object that the group to be processed is from.
     * @param sess
     *            The hibernate session used to access the database.
     * @param loginName
     *            A string that identifies the user in the csm_user table.
     * @param groupNames
     *            A string containing a comma-separated list of string literals
     *            that are the names of CSM groups the user is a member of or
     *            null.
     * @param alias
     *            An alias to use for the entity that this group is belongs to.
     * @return A Criterion object that represents the constraints implied by the
     *         group.
     * @throws QueryProcessingException
     */
    private Criterion processGroup(Group group, DetachedCriteria criteriaQuery, Object sourceQueryObject, Session sess,
            String loginName, String groupNames, String alias) throws QueryProcessingException {
        myLogger.debug("Processing group on " + sourceQueryObject.getName());

        Junction logic = convertLogicalOperator(group.getLogicRelation());

        if (group.getAssociation() != null) {
            for (int i = 0; i < group.getAssociation().length; i++) {
                logic.add(processAssociation(group.getAssociation(i), criteriaQuery, sourceQueryObject, sess,
                        loginName, groupNames, alias));
            }
        }
        if (group.getAttribute() != null) {
            for (int i = 0; i < group.getAttribute().length; i++) {
                logic.add(processAttribute(group.getAttribute(i), criteriaQuery, sourceQueryObject, alias));
            }
        }
        if (group.getGroup() != null) {
            for (int i = 0; i < group.getGroup().length; i++) {
                logic.add(processGroup(group.getGroup(i), criteriaQuery, sourceQueryObject, sess, loginName,
                        groupNames, alias));
            }
        }
        return logic;
    }

    /**
     * Converts a logical operator to a hibernate conjunction or disjunction.
     * 
     * @param op
     *            The logical operator to convert
     * @return A Conjunction or Disjunction object.
     */
    private Junction convertLogicalOperator(LogicalOperator op) throws QueryProcessingException {
        if (op == null) {
            throw new QueryProcessingException("No logicRelation atribute specified for group.");
        }
        if (op.getValue().equals(LogicalOperator._AND)) {
            return Restrictions.conjunction();
        } else if (op.getValue().equals(LogicalOperator._OR)) {
            return Restrictions.disjunction();
        }
        throw new QueryProcessingException("logicRelation value for group is '" + op.getValue()
                + "', but should be 'AND' or 'OR'.");
    }

    // uses the class type to convert the value to a typed object
    private java.lang.Object valueToObject(String className, String value) throws QueryProcessingException {
        myLogger.debug("Converting \"" + value + "\" to object of type " + className);
        if (className.equals(String.class.getName())) {
            return value;
        }
        if (className.equals(Integer.class.getName())) {
            return Integer.valueOf(value);
        }
        if (className.equals(Long.class.getName())) {
            return Long.valueOf(value);
        }
        if (className.equals(Double.class.getName())) {
            return Double.valueOf(value);
        }
        if (className.equals(Float.class.getName())) {
            return Float.valueOf(value);
        }
        if (className.equals(Boolean.class.getName())) {
            return Boolean.valueOf(value);
        }
        if (className.equals(Character.class.getName())) {
            if (value.length() == 1) {
                return Character.valueOf(value.charAt(0));
            } else {
                throw new QueryProcessingException("The value \"" + value + "\" of length " + value.length()
                        + " is not a valid character");
            }
        }
        if (className.equals(Date.class.getName())) {
            // try time, then dateTime, then just date
            List<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>(3);
            formats.add(new SimpleDateFormat("HH:mm:ss"));
            formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            formats.add(new SimpleDateFormat("yyyy-MM-dd"));

            Date date = null;
            Iterator<SimpleDateFormat> formatIter = formats.iterator();
            while (date == null && formatIter.hasNext()) {
                SimpleDateFormat formatter = formatIter.next();
                try {
                    date = formatter.parse(value);
                } catch (ParseException ex) {
                    myLogger.debug(value + " was not parsable by pattern " + formatter.toPattern());
                }
            }
            if (date == null) {
                throw new QueryProcessingException("Unable to parse date value \"" + value + "\"");
            }

            return date;
        }

        throw new QueryProcessingException("No conversion for type " + className);
    }

    /**
     * Metadata to manage predicate restrictions for criteria.
     * 
     * @author Mark Grand
     */
    private static class CriteriaPredicate {
        boolean isUnary;
        Method restrictionMethod;

        /**
         * Constructor
         * 
         * @param isUnary
         *            If true, predicate is unary; otherwise binary
         * @param restrictionMethod
         */
        public CriteriaPredicate(boolean isUnary, Method restrictionMethod) {
            super();
            this.isUnary = isUnary;
            this.restrictionMethod = restrictionMethod;
        }
    }
}
