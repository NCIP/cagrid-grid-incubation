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
package org.cagrid.redcap.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.translator.cql1.hql320ga.ParameterizedHqlQuery;
import org.cagrid.cql.translator.cql1.hql320ga.QueryTranslationException;
import org.cagrid.cql.translator.cql1.hql320ga.TypesInformationException;
import org.cagrid.cql.translator.cql1.hql320ga.TypesInformationResolver;

/** 
 *  CqlToHqlConverter
 *  Converter utility to turn CQL into HQL using positional parameters 
 *  compatible with Hibernate 3.2.0ga
 * 
 */
public class CqlToHqlConverter{
    public static final String TARGET_ALIAS = "__TargetAlias__";
    
    private static Log LOG = LogFactory.getLog(CqlToHqlConverter.class);
	
    // maps a CQL predicate to its HQL string representation 
	private Map<Predicate, String> predicateValues = null;
    
    private TypesInformationResolver typesInformationResolver = null;
    private boolean caseInsensitive;
    
    
    public CqlToHqlConverter(TypesInformationResolver typesInfoResolver, boolean caseInsensitive) {
       	this.typesInformationResolver = typesInfoResolver;
        this.caseInsensitive = caseInsensitive;
        initPredicateValues();
    }
    
    
    private void initPredicateValues() {
        predicateValues = new HashMap<Predicate, String>();
        predicateValues.put(Predicate.EQUAL_TO, "=");
        predicateValues.put(Predicate.GREATER_THAN, ">");
        predicateValues.put(Predicate.GREATER_THAN_EQUAL_TO, ">=");
        predicateValues.put(Predicate.LESS_THAN, "<");
        predicateValues.put(Predicate.LESS_THAN_EQUAL_TO, "<=");
        predicateValues.put(Predicate.LIKE, "LIKE");
        predicateValues.put(Predicate.NOT_EQUAL_TO, "!=");
        predicateValues.put(Predicate.IS_NOT_NULL, "is not null");
        predicateValues.put(Predicate.IS_NULL, "is null");
    }
    
	
	/**
	 * Converts CQL to parameterized HQL suitable for use with 
     * Hibernate v3.2.0ga
	 * 
	 * @param query
	 * 		The query to convert
	 * @return
	 * 		A parameterized HQL Query representing the CQL query
	 * @throws QueryTranslationException
	 */
	public ParameterizedHqlQuery convertToHql(CQLQuery query) throws QueryTranslationException {
		// create a string builder to build up the HQL
		StringBuilder rawHql = new StringBuilder();
        
        // create the list in which parameters will be placed
        List<java.lang.Object> parameters = new LinkedList<java.lang.Object>();
        
        // determine if the target has subclasses
        boolean hasSubclasses = false;
        try {
            hasSubclasses = typesInformationResolver.classHasSubclasses(query.getTarget().getName());
        } catch (TypesInformationException ex) {
            throw new QueryTranslationException(ex.getMessage(), ex);
        }
        LOG.debug(query.getTarget().getName() 
            + (hasSubclasses ? " has subclasses" : " has no subclasse"));
        
        // begin processing at the target level
		processTarget(query.getTarget(), rawHql, parameters, hasSubclasses);
        
        // apply query modifiers
		if (query.getQueryModifier() != null) {
			handleQueryModifier(query.getQueryModifier(), rawHql);
		} else {
		    // select only unique objects
            rawHql.insert(0, "Select distinct (" + TARGET_ALIAS + ") ");      
        }
        
        // build the final query object
        ParameterizedHqlQuery hqlQuery = new ParameterizedHqlQuery(rawHql.toString(), parameters);
		return hqlQuery;
	}
	
	
	/**
	 * Applies query modifiers to the HQL query
	 * 
	 * @param mods
	 * 		The modifiers to apply
	 * @param hql
	 * 		The HQL to apply the modifications to
	 */
	private void handleQueryModifier(QueryModifier mods, StringBuilder hql) {
		StringBuilder prepend = new StringBuilder();
		if (mods.isCountOnly()) {
			prepend.append("select count(");
			if (mods.getDistinctAttribute() != null) {
				prepend.append("distinct ").append(TARGET_ALIAS).append(".")
				.append(mods.getDistinctAttribute());
			} else {
				prepend.append("distinct " + TARGET_ALIAS + ".id");
			}
			prepend.append(')');
		} else {
		    // select distinct tuples
			prepend.append("select distinct ");
			
			//REDCAP query modifier association query
			prepend.append(TARGET_ALIAS);
			prepend.append(".");
			
			if (mods.getDistinctAttribute() != null) {
				prepend.append(mods.getDistinctAttribute());
			} else {
				for (int i = 0; i < mods.getAttributeNames().length; i++) {
					prepend.append(mods.getAttributeNames(i));
					if (i + 1 < mods.getAttributeNames().length) {
						prepend.append(", ");
						prepend.append(TARGET_ALIAS);
						prepend.append(".");
					}
				}
			}
		}
		
		prepend.append(' ');
		
		hql.insert(0, prepend.toString());
	}
	
	
	/**
	 * Processes the target object of a CQL query
	 * 
	 * @param target
	 * 		The target of a CQL query
	 * @param hql
	 * 		The hql string builder to append to
     * @param parameters
     *      The list of positional parameter values
	 * @param avoidSubclasses
	 * 		A flag to indicate the target has subclasses, which we should not return
	 * @throws QueryTranslationException
	 */
	private void processTarget(Object target, StringBuilder hql, List<java.lang.Object> parameters,
		boolean avoidSubclasses) throws QueryTranslationException {
		LOG.debug("Processing target " + target.getName());
        
        // the stack of associations processed at the current depth of the query
		Stack<Association> associationStack = new Stack<Association>();
        
        // start the query
		hql.append("From ").append(target.getName()).append(" as ").append(TARGET_ALIAS).append(' ');
		
		if (target.getAssociation() != null) {
			processAssociation(target.getAssociation(), hql, parameters, associationStack, target, TARGET_ALIAS);
		}
		if (target.getAttribute() != null) {
			hql.append("where ");
			processAttribute(target.getAttribute(), hql, parameters, target, TARGET_ALIAS);
		}
		if (target.getGroup() != null) {

			if(target.getGroup().getAssociation()==null){
				hql.append(" where ");
			}
			processGroup(target.getGroup(), hql, parameters, associationStack, target, TARGET_ALIAS);
		}
		
		if (avoidSubclasses) {
			boolean mustAddWhereClause = 
				target.getAssociation() == null
				&& target.getAttribute() == null
				&& target.getGroup() == null;
			if (mustAddWhereClause) {
				hql.append(" where ");
			} else {
				hql.append(" and ");
			}
			hql.append(TARGET_ALIAS).append(".class = ?");
			java.lang.Object classDiscriminatorInstance = null;
			try {
			    classDiscriminatorInstance = typesInformationResolver.getClassDiscriminatorValue(target.getName());
			} catch (TypesInformationException ex) {
			    String message = "Error determining class discriminator for " + target.getName() + ": " + ex.getMessage();
			    LOG.error(message, ex);
			    throw new QueryTranslationException(message, ex);
			}
            parameters.add(classDiscriminatorInstance);
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
	 * @throws QueryTranslationException
	 */
	private void processAttribute(Attribute attribute, StringBuilder hql, 
        List<java.lang.Object> parameters, Object queryObject, String queryObjectAlias) throws QueryTranslationException {
        LOG.debug("Processing attribute " + queryObject.getName() + "." + attribute.getName());
        
        
        // get the predicate, check for a default value
        Predicate predicate = attribute.getPredicate();
        if (predicate == null) {
            predicate = Predicate.EQUAL_TO;
        }
        
        // determine if the predicate is unary
		boolean unaryPredicate = predicate.equals(Predicate.IS_NOT_NULL)
			|| predicate.equals(Predicate.IS_NULL);
		
        // construct the query fragment
        if (caseInsensitive) {
			hql.append("lower(");
		}
        
        // append the path to the attribute itself
        hql.append(queryObjectAlias).append('.').append(attribute.getName());
        
        // close up case insensitivity
		if (caseInsensitive) {
			hql.append(')');
		}
        
        // append the predicate
		hql.append(' ');
		String predicateAsString = predicateValues.get(predicate);
		if (!unaryPredicate) {
			hql.append(predicateAsString).append(' ');

            // add a placeholder parameter to the HQL query
			hql.append('?');

			// convert the attribute value to the specific data type of the attribute
			Class<?> attributeType = null;
			try {
			    attributeType = typesInformationResolver.getJavaDataType(queryObject.getName(), attribute.getName());
			} catch (TypesInformationException ex) {
			    throw new QueryTranslationException(ex.getMessage(), ex);
			}
			if (attributeType == null) {
			    throw new QueryTranslationException("No type could be determined for attribute " 
			        + queryObject.getName() + "." + attribute.getName());
			}
            java.lang.Object value = valueToObject(attributeType, 
                caseInsensitive ? attribute.getValue().toLowerCase() : attribute.getValue());

            // add a positional parameter value to the list            
            parameters.add(value);
		} else {
            // binary predicates just get appended w/o values associated with them
			hql.append(predicateAsString);
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
	 * @throws QueryTranslationException
	 */
	private void processAssociation(Association association, StringBuilder hql, List<java.lang.Object> parameters, 
        Stack<Association> associationStack, Object sourceQueryObject, String sourceAlias) throws QueryTranslationException {
        LOG.debug("Processing association " + sourceQueryObject.getName() + " to " + association.getName());
       
        // get the association's role name
		String roleName = null;
		try {
		    roleName = typesInformationResolver.getRoleName(sourceQueryObject.getName(), association.getName());
		} catch (TypesInformationException ex) {
		    throw new QueryTranslationException(ex.getMessage(), ex);
		}
		if (roleName == null) {
			// still null?? no association to the object!
			throw new QueryTranslationException("Association from type " + sourceQueryObject.getName() + 
				" to type " + association.getName() + " does not exist.  Use only direct associations");
		}
        LOG.debug("Role name determined to be " + roleName);
        
        // determine the alias for this association
        String alias = getAssociationAlias(sourceQueryObject.getName(), association.getName(), roleName);
        LOG.debug("Association alias determined to be " + alias);
        
		// add this association to the stack
		associationStack.push(association);
        
        // flag indicates the query is only verifying the association is populated
        boolean simpleNullCheck = true;
		if (association.getAssociation() != null) {
            simpleNullCheck = false;
            
            hql.append(" join ");
            
            // add clause to select things from this association
            hql.append(sourceAlias).append('.').append(roleName);   

            hql.append(" as ");
            hql.append("_").append(roleName).append("_");
            hql.append(" where ");
            hql.append("_").append(roleName).append("_");
                       
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias);
            
            processAssociation(association.getAssociation(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		if (association.getAttribute() != null) {
            simpleNullCheck = false;
           
            //REDCAP
            hql.append(" join ");
            hql.append(sourceAlias).append('.').append(roleName);
            hql.append(" as ");
            hql.append("_").append(roleName).append("_");
            hql.append(" where ");
            hql.append("_").append(roleName).append("_");            
            //
            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias).append(" where ");
			processAttribute(association.getAttribute(), hql, parameters, association, alias);
			hql.append(") ");
		}
		if (association.getGroup() != null) {
			
			 hql.append(" join ");
			 
            simpleNullCheck = false;
            hql.append(sourceAlias).append('.').append(roleName);            
            
            //REDCAP
            hql.append(" as ").append(" b ");
            hql.append(" where ").append(" b ");   
            //
            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias);
            
            //REDCAP
            if(association.getGroup().getAssociation()==null){
            	hql.append(" where ");
            }
            //
            
			hql.append(" ");
            
            processGroup(association.getGroup(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		
        if (simpleNullCheck) {
            // query is checking for the association to exist and be non-null
        	hql.append(" where ");
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
	 * @throws QueryTranslationException
	 */
	private void processGroup(Group group, StringBuilder hql, List<java.lang.Object> parameters,
        Stack<Association> associationStack, Object sourceQueryObject, String sourceAlias) throws QueryTranslationException {
        LOG.debug("Processing group on " + sourceQueryObject.getName());
        
		String logic = convertLogicalOperator(group.getLogicRelation());
		boolean mustAddLogic = false;
		
		if (group.getAssociation() != null) {
			boolean nestedAssociation = false;
			//groups having multiple associations
			LOG.debug("group association length"+group.getAssociation().length);
			if(group.getAssociation().length>1){
				for(int i=0;i<group.getAssociation().length;i++){
					
					LOG.debug("Processing group association " + sourceQueryObject.getName() + " to " + group.getAssociation(i).getName());
				       
			        // get the association's role name
					String roleName = null;
					try {
					    roleName = typesInformationResolver.getRoleName(sourceQueryObject.getName(), group.getAssociation(i).getName());
					} catch (TypesInformationException ex) {
					    throw new QueryTranslationException(ex.getMessage(), ex);
					}
					if (roleName == null) {
						// still null?? no association to the object!
						throw new QueryTranslationException("Association from type " + sourceQueryObject.getName() + 
							" to type " + group.getAssociation(i).getName() + " does not exist.  Use only direct associations");
					}
			        LOG.debug("Role name determined to be " + roleName);
			        
			        // determine the alias for this association
			        String alias = getAssociationAlias(sourceQueryObject.getName(), group.getAssociation(i).getName(), roleName);
			        LOG.debug("Association alias determined to be " + alias);
			        
			        
					if(group.getAssociation(i).getAssociation()!=null){
						nestedAssociation=true;
					}
					hql.append(" join ");
		            hql.append(sourceAlias).append('.').append(roleName);
		            
		            hql.append(" as ");
		            hql.append(sourceAlias).append("_").append(roleName).append("_");
				}
				
				hql.append(" where ");
				
				for(int i=0;i<group.getAssociation().length;i++){
					mustAddLogic = true;
					processGroupAssociations(group,group.getAssociation(i), hql, parameters, associationStack, sourceQueryObject, sourceAlias);
					if (i + 1 < group.getAssociation().length) {
						hql.append(' ').append(logic).append(' ');
					}
				}
			}else{
			//until here
				for (int i = 0; i < group.getAssociation().length; i++) {
					mustAddLogic = true;
					processAssociation(group.getAssociation(i), hql, parameters, associationStack, sourceQueryObject, sourceAlias);
					if (i + 1 < group.getAssociation().length) {
						hql.append(' ').append(logic).append(' ');
					}
				}
			}
		}
		if (group.getAttribute() != null) {
			
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getAttribute().length; i++) {
				mustAddLogic = true;
				
				processAttribute(group.getAttribute(i), hql, parameters, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getAttribute().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getGroup() != null) {
			//hql.append(" join ");
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getGroup().length; i++) {
				
				processGroup(group.getGroup(i), hql, parameters, associationStack, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getGroup().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
	}
	
	
	/**
	 * Converts a logical operator to its HQL string equivalent.
	 * 
	 * @param op
	 * 		The logical operator to convert
	 * @return
	 * 		The CQL logical operator as HQL
	 */
	private String convertLogicalOperator(LogicalOperator op) throws QueryTranslationException {
		if (op.getValue().equals(LogicalOperator._AND)) {
			return "AND";
		} else if (op.getValue().equals(LogicalOperator._OR)) {
			return "OR";
		}
		throw new QueryTranslationException("Logical operator '" + op.getValue() + "' is not recognized.");
	}
    
    
    // uses the class type to convert the value to a typed object
    private java.lang.Object valueToObject(Class<?> fieldType, String value) throws QueryTranslationException {
        LOG.debug("Converting \"" + value + "\" to object of type " + fieldType.getName());
        if (String.class.equals(fieldType)) {
            return value;
        }
        if (Integer.class.equals(fieldType)) {
            return Integer.valueOf(value);
        }
        if (Long.class.equals(fieldType)) {
            return Long.valueOf(value);
        }
        if (Double.class.equals(fieldType)) {
            return Double.valueOf(value);
        }
        if (Float.class.equals(fieldType)) {
            return Float.valueOf(value);
        }
        if (Boolean.class.equals(fieldType)) {
            return Boolean.valueOf(value);
        }
        if (Character.class.equals(fieldType)) {
            if (value.length() == 1) {
                return Character.valueOf(value.charAt(0));
            } else {
                throw new QueryTranslationException("The value \"" + value + "\" of length " 
                    + value.length() + " is not a valid character");
            }
        }
        if (Date.class.equals(fieldType)) {
            // try time, then dateTime, then just date
            List<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>(3);
            formats.add(new SimpleDateFormat("HH:mm:ss"));
            formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            formats.add(new SimpleDateFormat("yyyy-MM-dd"));
            formats.add(new SimpleDateFormat("MM/dd/yyyy"));
            
            Date date = null;
            Iterator<SimpleDateFormat> formatIter = formats.iterator();
            while (date == null && formatIter.hasNext()) {
                SimpleDateFormat formatter = formatIter.next();
                try {
                    date = formatter.parse(value);
                } catch (ParseException ex) {
                    LOG.debug(value + " was not parsable by pattern " + formatter.toPattern());
                }
            }
            if (date == null) {
                throw new QueryTranslationException("Unable to parse date value \"" + value + "\"");
            }
            
            return date;
        }
        
        throw new QueryTranslationException("No conversion for type " + fieldType != null ? fieldType.getName() : "null");
    }
    
    
    private String getAssociationAlias(String parentClassName, String associationClassName, String roleName) {
        int dotIndex = parentClassName.lastIndexOf('.');
        String parentShortName = dotIndex != -1 ? parentClassName.substring(dotIndex + 1) : parentClassName;
        dotIndex = associationClassName.lastIndexOf('.');
        String associationShortName = dotIndex != -1 ? associationClassName.substring(dotIndex + 1) : associationClassName;
        String alias = "__" + parentShortName + "_" + associationShortName + "_" + roleName;
        return alias;
    }
    
    //Groups having multiple associtations
    private void processGroupAssociations(Group group, Association association, StringBuilder hql, List<java.lang.Object> parameters, 
            Stack<Association> associationStack, Object sourceQueryObject, String sourceAlias) throws QueryTranslationException {
        LOG.debug("Processing group association " + sourceQueryObject.getName() + " to " + association.getName());
       
        // get the association's role name
		String roleName = null;
		try {
		    roleName = typesInformationResolver.getRoleName(sourceQueryObject.getName(), association.getName());
		} catch (TypesInformationException ex) {
		    throw new QueryTranslationException(ex.getMessage(), ex);
		}
		if (roleName == null) {
			// still null?? no association to the object!
			throw new QueryTranslationException("Association from type " + sourceQueryObject.getName() + 
				" to type " + association.getName() + " does not exist.  Use only direct associations");
		}
        LOG.debug("Role name determined to be " + roleName);
        
        // determine the alias for this association
        String alias = getAssociationAlias(sourceQueryObject.getName(), association.getName(), roleName);
        LOG.debug("Association alias determined to be " + alias);
        
		// add this association to the stack
		associationStack.push(association);
        
        // flag indicates the query is only verifying the association is populated
        boolean simpleNullCheck = true;
		if (association.getAssociation() != null) {
            simpleNullCheck = false;
            
            hql.append(sourceAlias).append("_");
            hql.append(roleName).append("_");
            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias);//.append(" where ");
            
            processAssociation(association.getAssociation(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		if (association.getAttribute() != null) {
            simpleNullCheck = false;
           
            hql.append(sourceAlias).append("_");
           	hql.append(roleName).append("_");
            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias).append(" where ");
			processAttribute(association.getAttribute(), hql, parameters, association, alias);
			hql.append(") ");
		}
		if (association.getGroup() != null) {
			
//			 hql.append(" join ");
			 
            simpleNullCheck = false;
//            hql.append(sourceAlias).append('.').append(roleName);            
//            
//            //REDCAP
//            hql.append(" as ").append(" b ");
//            hql.append(" where ").append(" b ");   
            //
            
            hql.append(sourceAlias).append("_");
           	hql.append(roleName).append("_");
            
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getName()).append(" as ").append(alias);
            
            if(association.getGroup().getAssociation()==null){
            	hql.append(" where ");
            }
            //
            
			hql.append(" ");
            
            processGroup(association.getGroup(), hql, parameters, associationStack, association, alias);
            hql.append(") ");
		}
		
        if (simpleNullCheck) {
            // query is checking for the association to exist and be non-null
            hql.append(" where ");
        	hql.append(sourceAlias).append('.').append(roleName).append(".id is not null ");
         }
        
		// pop this association off the stack
        associationStack.pop();
        LOG.debug(associationStack.size() + " associations remain on the stack");
	}
}
