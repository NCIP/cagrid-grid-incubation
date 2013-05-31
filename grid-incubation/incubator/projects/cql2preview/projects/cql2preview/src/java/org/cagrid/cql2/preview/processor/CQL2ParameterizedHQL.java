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
package org.cagrid.cql2.preview.processor;

import gov.nih.nci.cagrid.cql2.associations.AssociationPopulationSpecification;
import gov.nih.nci.cagrid.cql2.associations.NamedAssociation;
import gov.nih.nci.cagrid.cql2.attribute.AttributeValue;
import gov.nih.nci.cagrid.cql2.attribute.BinaryCQLAttribute;
import gov.nih.nci.cagrid.cql2.attribute.CQLAttribute;
import gov.nih.nci.cagrid.cql2.attribute.UnaryCQLAttribute;
import gov.nih.nci.cagrid.cql2.components.CQLAssociatedObject;
import gov.nih.nci.cagrid.cql2.components.CQLGroup;
import gov.nih.nci.cagrid.cql2.components.CQLObject;
import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.components.CQLTargetObject;
import gov.nih.nci.cagrid.cql2.components.GroupLogicalOperator;
import gov.nih.nci.cagrid.cql2.predicates.BinaryPredicate;
import gov.nih.nci.cagrid.cql2.predicates.UnaryPredicate;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.DomainTypesInformationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.cagrid.cql2.preview.processor.RoleNameResolver.ClassAssociation;

/** 
 *  CQL2ParameterizedHQL
 *  Converter utility to turn CQL into HQL using positional parameters 
 *  compatible with Hibernate 3.2.0ga for use with caCORE SDK 4
 * 
 * @author David Ervin
 * 
 * @created Mar 2, 2007 10:26:47 AM
 * @version $Id: CQL2ParameterizedHQL.java,v 1.3 2008/04/08 13:56:46 dervin Exp $ 
 */
public class CQL2ParameterizedHQL {
    public static final String TARGET_ALIAS = "__TargetAlias__";
    
    private static Logger LOG = Logger.getLogger(CQL2ParameterizedHQL.class);
	
    // maps a CQL predicate to its HQL string representation 
	private Map<Object, String> predicateValues = null;
    
    private DomainTypesInformationUtil typesInfoUtil = null;
    private RoleNameResolver roleNameResolver = null;
    
    
    public CQL2ParameterizedHQL(DomainTypesInformation typesInfo, DomainModel model) throws IOException, ClassNotFoundException {
        this.typesInfoUtil = new DomainTypesInformationUtil(typesInfo);
        this.roleNameResolver = new RoleNameResolver(model);
        initPredicateValues();
    }
    
    
    private void initPredicateValues() {
        predicateValues = new HashMap<Object, String>();
        predicateValues.put(BinaryPredicate.EQUAL_TO, "=");
        predicateValues.put(BinaryPredicate.GREATER_THAN, ">");
        predicateValues.put(BinaryPredicate.GREATER_THAN_EQUAL_TO, ">=");
        predicateValues.put(BinaryPredicate.LESS_THAN, "<");
        predicateValues.put(BinaryPredicate.LESS_THAN_EQUAL_TO, "<=");
        predicateValues.put(BinaryPredicate.LIKE, "like");
        predicateValues.put(BinaryPredicate.NOT_EQUAL_TO, "!=");
        predicateValues.put(UnaryPredicate.IS_NOT_NULL, "is not null");
        predicateValues.put(UnaryPredicate.IS_NULL, "is null");
    }
    
	
	/**
	 * Converts CQL to parameterized HQL suitable for use with 
     * Hibernate v3.2.0ga and the caCORE SDK version 4.0.  This
     * translation process <b>does not</b> include application of
     * CQL Query Modifiers; this functionality is implemented as a
     * post-processing operation in the CQL2QueryProcessor class.
	 * 
	 * @param query
	 * 		The query to convert
	 * @return
	 * 		A parameterized HQL Query representing the CQL query
	 * @throws Exception
	 */
	public ParameterizedHqlQuery convertToHql(CQLQuery query) throws Exception {
		// create a string builder to build up the HQL
		StringBuilder rawHql = new StringBuilder();
        
        // create the list in which parameters will be placed
        List<java.lang.Object> parameters = new LinkedList<java.lang.Object>();
        
        // determine if the target has subclasses
        List<String> subclasses = typesInfoUtil.getSubclasses(
            query.getCQLTargetObject().getClassName());
        boolean hasSubclasses = !(subclasses == null || subclasses.size() == 0);
        LOG.debug(query.getCQLTargetObject().getClassName()
            + (hasSubclasses ? " has " + subclasses.size() + " subclasses" : " has no subclasse"));
        
        // begin processing at the target level
		processTarget(query.getCQLTargetObject(), query.getAssociationPopulationSpecification(), 
            rawHql, parameters, hasSubclasses);
        
        // build the final query object
        ParameterizedHqlQuery hqlQuery = new ParameterizedHqlQuery(rawHql.toString(), parameters);
		return hqlQuery;
	}
	
	
	/**
	 * Processes the target object of a CQL query
	 * 
	 * @param target
	 * 		The target of a CQL query
     * @param associtaionPopulation
     *      The (optional) association population specification information
	 * @param hql
	 * 		The hql string builder to append to
     * @param parameters
     *      The list of positional parameter values
	 * @param avoidSubclasses
	 * 		A flag to indicate the target has subclasses, which we should not return
	 * @throws QueryProcessingException
	 */
	private void processTarget(CQLTargetObject target, AssociationPopulationSpecification associtaionPopulation, 
        StringBuilder hql, List<java.lang.Object> parameters, boolean avoidSubclasses) throws Exception {
		LOG.debug("Processing target " + target.getClassName());
        
        // the stack of associations processed at the current depth of the query
		Stack<CQLAssociatedObject> associationStack = new Stack<CQLAssociatedObject>();
        
        // start the query
		hql.append("From ").append(target.getClassName()).append(" as ").append(TARGET_ALIAS).append(' ');
		
        if (associtaionPopulation != null) {
            String fetchSpec = generateAssociationFetchClause(target.getClassName(), associtaionPopulation);
            hql.append(fetchSpec);
        }
        
		if (target.getCQLAssociatedObject() != null) {
			hql.append("where ");
			processAssociation(target.getCQLAssociatedObject(), hql, parameters, associationStack, target, TARGET_ALIAS);
		}
		if (target.getBinaryCQLAttribute() != null || target.getUnaryCQLAttribute() != null) {
            hql.append("where ");
            CQLAttribute baseAttribute = target.getBinaryCQLAttribute();
            if (baseAttribute == null) {
                baseAttribute = target.getUnaryCQLAttribute();
            }
			processAttribute(baseAttribute, hql, parameters, target, TARGET_ALIAS);
		}
		if (target.getCQLGroup() != null) {
			hql.append("where ");
			processGroup(target.getCQLGroup(), hql, parameters, associationStack, target, TARGET_ALIAS);
		}
		
		if (avoidSubclasses) {
			boolean mustAddWhereClause = 
				target.getCQLAssociatedObject() == null
				&& target.getBinaryCQLAttribute() == null
                && target.getUnaryCQLAttribute() == null
				&& target.getCQLGroup() == null;
			if (mustAddWhereClause) {
				hql.append(" where ");
			} else {
				hql.append(" and ");
			}
			hql.append(TARGET_ALIAS).append(".class = ?");
            // 0 is the targeted class, 1 is the first subclass, 2 is the next...
            parameters.add(Integer.valueOf(0));
		}
	}
	
	
	/**
	 * Processes a CQL query attribute into HQL
	 * 
	 * @param attribute
	 * 		The CQL attribute
	 * @param hql
	 * 		The HQL statement fragment
     * @param parameters
     *      The positional parameters list
	 * @param associationTrace
	 * 		The trace of associations
	 * @param objectClassName
	 * 		The class name of the object to which this association belongs
	 * @throws QueryProcessingException
	 */
	private void processAttribute(CQLAttribute attribute, StringBuilder hql, 
        List<java.lang.Object> parameters, CQLObject queryObject, String queryObjectAlias) throws Exception {
        LOG.debug("Processing attribute " + queryObject.getClassName() + "." + attribute.getName());
        
        // determine the Java type of the field being processed
        String attributeFieldType = typesInfoUtil.getAttributeJavaType(queryObject.getClassName(), attribute.getName());
        if (attributeFieldType == null) {
            throw new Exception("Field type of " 
                + queryObject.getClassName() + "." + attribute.getName() + " could not be determined");
        }
        LOG.debug("Attribute found to be of type " + attributeFieldType);
        
        boolean unaryAttribute = attribute instanceof UnaryCQLAttribute;
		
        // construct the query fragment        
        // append the path to the attribute itself
        hql.append(queryObjectAlias).append('.').append(attribute.getName());
        
        // append the predicate
		hql.append(' ');
        if (unaryAttribute) {
            // unary predicates just get appended w/o values associated with them
            String predicateAsString = predicateValues.get(((UnaryCQLAttribute) attribute).getPredicate());
            hql.append(predicateAsString);
        } else {
            // binary predicates have a predicate string and a value
            String predicateAsString = predicateValues.get(((BinaryCQLAttribute) attribute).getPredicate());
            AttributeValue rawValue = ((BinaryCQLAttribute) attribute).getAttributeValue();
            
            hql.append(predicateAsString).append(' ');
            
            // add a placeholder parameter to the HQL query
            hql.append('?');
            
            // add the parameter to the positional params list
            parameters.add(getAttributeValueObject(rawValue));
        }
	}
	
	
	/**
	 * Processes CQL associations into HQL
	 * 
	 * @param association
	 * 		The CQL association
	 * @param hql
	 * 		The HQL fragment which will be edited
     * @param parameters
     *      The positional HQL query parameters
	 * @param associationTrace
	 * 		The trace of associations
	 * @param sourceClassName
	 * 		The class name of the type to which this association belongs
	 * @throws QueryProcessingException
	 */
	private void processAssociation(CQLAssociatedObject association, StringBuilder hql, List<java.lang.Object> parameters, 
        Stack<CQLAssociatedObject> associationStack, CQLObject sourceQueryObject, String sourceAlias) throws Exception {
        LOG.debug("Processing association " + sourceQueryObject.getClassName() + " to " + association.getClassName());
        
        // get the association's role name
		String roleName = association.getSourceRoleName();
		LOG.debug("Role name determined to be " + roleName);
        
        // determine the alias for this association
        String alias = getAssociationAlias(sourceQueryObject.getClassName(), association.getClassName(), roleName);
        LOG.debug("Association alias determined to be " + alias);
        
		// add this association to the stack
		associationStack.push(association);
        
        // flag indicates the query is only verifying the association is populated
        boolean simpleNullCheck = true;
		if (association.getCQLAssociatedObject() != null) {
            simpleNullCheck = false;
            // add clause to select things from this association
            hql.append(sourceAlias).append('.').append(roleName);            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
            processAssociation(association.getCQLAssociatedObject(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		if (association.getBinaryCQLAttribute() != null || association.getUnaryCQLAttribute() != null) {
            simpleNullCheck = false;
            CQLAttribute attrib = association.getBinaryCQLAttribute();
            if (attrib == null) {
                attrib = association.getUnaryCQLAttribute();
            }
			processAttribute(attrib, hql, parameters, association, sourceAlias + "." + roleName);
		}
		if (association.getCQLGroup() != null) {
            simpleNullCheck = false;
            hql.append(sourceAlias).append('.').append(roleName);            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
			processGroup(association.getCQLGroup(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		
        if (simpleNullCheck) {
            // query is checking for the association to exist and be non-null
            hql.append(sourceAlias).append('.').append(roleName).append(".id is not null ");
        }
        
		// pop this association off the stack
        associationStack.pop();
        LOG.debug(associationStack.size() + " associations remain on the stack");
	}
	
	
	/**
	 * Processes a CQL group into HQL
	 * 
	 * @param group
	 * 		The CQL Group
	 * @param hql
	 * 		The HQL fragment which will be edited
     * @param parameters
     *      The positional HQL query parameters
	 * @param associationTrace
	 * 		The trace of associations
	 * @param sourceClassName
	 * 		The class to which this group belongs
	 * @throws QueryProcessingException
	 */
	private void processGroup(CQLGroup group, StringBuilder hql, List<java.lang.Object> parameters,
        Stack<CQLAssociatedObject> associationStack, CQLObject sourceQueryObject, String sourceAlias) throws Exception {
        LOG.debug("Processing group on " + sourceQueryObject.getClassName());
        
		String logic = convertLogicalOperator(group.getLogicalOperation());
		boolean mustAddLogic = false;
		
		// open the group
		hql.append('(');
		
		if (group.getCQLAssociatedObject() != null) {
			for (int i = 0; i < group.getCQLAssociatedObject().length; i++) {
				mustAddLogic = true;
				processAssociation(group.getCQLAssociatedObject(i), hql, parameters, associationStack, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getCQLAssociatedObject().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getBinaryCQLAttribute() != null || group.getUnaryCQLAttribute() != null) {
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
            List<CQLAttribute> groupAttributes = new ArrayList<CQLAttribute>();
            if (group.getBinaryCQLAttribute() != null) {
                Collections.addAll(groupAttributes, group.getBinaryCQLAttribute());
            }
            if (group.getUnaryCQLAttribute() != null) {
                Collections.addAll(groupAttributes, group.getUnaryCQLAttribute());
            }
			for (int i = 0; i < groupAttributes.size(); i++) {
				mustAddLogic = true;
				processAttribute(groupAttributes.get(i), hql, parameters, sourceQueryObject, sourceAlias);
				if (i + 1 < groupAttributes.size()) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getCQLGroup() != null) {
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getCQLGroup().length; i++) {
				processGroup(group.getCQLGroup(i), hql, parameters, associationStack, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getCQLGroup().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		
		// close the group
		hql.append(')');
	}
	
	
	/**
	 * Converts a logical operator to its HQL string equiavalent.
	 * 
	 * @param op
	 * 		The logical operator to convert
	 * @return
	 * 		The CQL logical operator as HQL
	 */
	private String convertLogicalOperator(GroupLogicalOperator op) throws Exception {
		if (op.getValue().equals(GroupLogicalOperator._AND)) {
			return "AND";
		} else if (op.getValue().equals(GroupLogicalOperator._OR)) {
			return "OR";
		}
		throw new Exception("Logical operator '" + op.getValue() + "' is not recognized.");
	}
    
    
    private Object getAttributeValueObject(AttributeValue value) {
        if (value.getStringValue() != null) {
            return value.getStringValue();
        } else if (value.getBooleanValue() != null) {
            return value.getBooleanValue();
        } else if (value.getDateValue() != null) {
            return value.getDateValue();
        } else if (value.getIntegerValue() != null) {
            return value.getIntegerValue();
        } else if (value.getLongValue() != null) {
            return value.getLongValue();
        } else if (value.getTimeValue() != null) {
            return value.getTimeValue().getAsCalendar().getTime();
        }
        return null;
    }
    
    
    private String generateAssociationFetchClause(
        String targetClassName, AssociationPopulationSpecification spec) throws Exception {
        StringBuffer clause = new StringBuffer();
        if (spec.getPopulationDepth() != null) {
            int maxDepth = -1;
            if (spec.getPopulationDepth().isInfinite()) {
                maxDepth = Integer.MAX_VALUE;
            } else {
                maxDepth = spec.getPopulationDepth().getDepth().intValue();
            }
            Set<String> joinedAssociations = new HashSet<String>();
            appendJoinsByDepth(clause, targetClassName, TARGET_ALIAS, 0, 0, maxDepth, joinedAssociations);
        } else {
            System.out.println("Using named associations");
            int aliasIndex = 0;
            NamedAssociation[] namedAssociations = spec.getNamedAssociationList().getNamedAssociation();
            for (NamedAssociation na : namedAssociations) {
                appendNamedJoins(clause, na, TARGET_ALIAS, aliasIndex);
            }
        }
        return clause.toString();
    }
    
    
    private void appendJoinsByDepth(StringBuffer buff, String parentClassName, String parentAlias, 
        int aliasIndex, int currentDepth, int maxDepth, Set<String> joinedAssociations) throws Exception {
        currentDepth++;
        if (currentDepth > maxDepth) {
            return;
        }
        List<ClassAssociation> associations = roleNameResolver.getAssociationRoleNames(parentClassName);
        for (ClassAssociation ca : associations) {
            String fetchName = ca.getClassName() + "." + ca.getRoleName();
            if (!joinedAssociations.contains(fetchName)) {
                joinedAssociations.add(fetchName);
                String myAlias = "fetchAlias" + aliasIndex;
                aliasIndex++;
                buff.append("left join fetch ").append(parentAlias).append('.').append(ca.getRoleName())
                    .append(" as ").append(myAlias).append(' ');
                int newMaxDepth = maxDepth;
                int newCurrentDepth = currentDepth;
                appendJoinsByDepth(buff, ca.getClassName(), myAlias, aliasIndex, 
                    newCurrentDepth, newMaxDepth, joinedAssociations);
            }
        }
    }
    
    
    private void appendNamedJoins(StringBuffer buff, NamedAssociation na, String parentAlias, int aliasIndex) {
        String myAlias = "fetchAlias" + aliasIndex;
        aliasIndex++;
        buff.append("left join fetch ").append(parentAlias).append('.').append(na.getRoleName())
            .append(" as ").append(myAlias).append(' ');
        if (na.getNamedAssociation() != null) {
            for (NamedAssociation subAssociation : na.getNamedAssociation()) {
                appendNamedJoins(buff, subAssociation, myAlias, aliasIndex);
            }
        }
    }
    
    
    private String getAssociationAlias(String parentClassName, String associationClassName, String roleName) {
        int dotIndex = parentClassName.lastIndexOf('.');
        String parentShortName = dotIndex != -1 ? parentClassName.substring(dotIndex + 1) : parentClassName;
        dotIndex = associationClassName.lastIndexOf('.');
        String associationShortName = dotIndex != -1 ? associationClassName.substring(dotIndex + 1) : associationClassName;
        String alias = "__" + parentShortName + "_" + associationShortName + "_" + roleName;
        return alias;
    }
}
