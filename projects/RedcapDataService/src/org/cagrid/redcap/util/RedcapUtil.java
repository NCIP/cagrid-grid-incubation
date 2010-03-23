package org.cagrid.redcap.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import org.cagrid.cql.translator.cql1.hql320ga.ParameterizedHqlQuery;
import org.cagrid.cql.translator.cql1.hql320ga.QueryTranslationException;
import org.cagrid.cql.translator.cql1.hql320ga.HibernateConfigTypesInformationResolver;
import org.cagrid.redcap.processor.DatabaseConnectionSource;

/*
 * Utility to convert the CQL to parameterized HQL
 * and query the REDCap database by passing in the
 * required parameters.
 */
public class RedcapUtil {

	private static final Log LOG = LogFactory.getLog(RedcapUtil.class);
	private List<String> paramValueList = new ArrayList<String>();

	/*
	 * Method to get the connection from the
	 * Connection pool and query the REDCap
	 * database by passing the appropriate
	 * parameters. 
	 * @param CQLQuery: request from user
	 * @param SessionFactory: Hibernate session factory
	 * @param AnnotationConfig: Hibernate Annotation Config info
	 * @param DatabaseConnectionSource: pooled database connection source
	 * @return List of Top level objects from REDCap
	 */
	public List<Object> convert(CQLQuery cqlQuery, SessionFactory sessionFactory, AnnotationConfiguration annotationConfig,DatabaseConnectionSource connectionSource)	throws QueryProcessingException, SQLException {
		Session session = null;
		List<Object> objects = null;
		Connection con = null;
		try {
			HibernateConfigTypesInformationResolver conf = new HibernateConfigTypesInformationResolver(annotationConfig);
			CqlToHqlConverter cqlHqlConverter = new CqlToHqlConverter(conf,true);
			//get connection from pool
			con = connectionSource.getConnection();
			con.setAutoCommit(false);
			session = sessionFactory.openSession(con);
			LOG.info("Session open:"+session.isOpen());
			
			session.beginTransaction();
			
			ParameterizedHqlQuery parameterizedHql = cqlHqlConverter.convertToHql(cqlQuery);
			LOG.info("Parameterized HQL :"+parameterizedHql.toString());
			Query query = session.createQuery(parameterizedHql.getHql());

			List<Object> targetList = parameterizedHql.getParameters();
			if(cqlQuery.getTarget()!=null){
				for(int i=0;i<targetList.size();i++){
					query.setParameter(i, targetList.get(i));
					if(targetList.get(i)!=null){
						LOG.debug("Parameter at "+i+" :"+targetList.get(i).toString());
					}	
				}
			}
			
//			if (cqlQuery.getTarget() != null) {
//				List<String> targetParameterList = getCQLParameterList(cqlQuery);
//				for (int i = 0; i < targetParameterList.size(); i++) {
//					query.setString(i, targetParameterList.get(i).toString());
//					LOG.debug("Parameter at "+i+" :"+targetParameterList.get(i).toString());
//				}
//			}

			objects = query.list();
			if(objects!=null){
				LOG.info("Retrieved "+objects.size()+" top level Objects");
			}
			
			session.getTransaction().commit();

			if (session.isOpen()) {
				con.close();
				session.close();
			}
			LOG.debug("Session Open?"+session.isOpen());
		} catch(QueryException exp){
			if(session.isOpen()){
				session.getTransaction().rollback();
			}
			LOG.debug("Query Exception"+exp.getMessage(),exp);
			throw new QueryProcessingException(exp.getMessage());
		}catch (QueryTranslationException queryExp) {
			if(session.isOpen()){
				session.getTransaction().rollback();
			}
			LOG.debug("QueryTranslationException "+queryExp.getMessage(),queryExp);
			throw new QueryProcessingException(queryExp.getMessage());
		} catch (MappingException mappingExp) {
			if(session.isOpen()){
				session.getTransaction().rollback();
			}
			LOG.debug("MappingException "+mappingExp.getMessage(),mappingExp);
			throw new QueryProcessingException(mappingExp.getMessage());
		}finally{
			if (session.isOpen()) {
				if(con!=null&& !con.isClosed())
				con.close();
				session.close();
			}
			LOG.debug("Session Open?"+session.isOpen());
		}
		return objects;
	}

	private List<String> getCQLParameterList(CQLQuery cqlQuery) {
		if (cqlQuery.getTarget() != null) {
			paramValueList = getTargetParameterList(cqlQuery);
		}
		LOG.info("Number of parameters passed "+paramValueList.size());
		return paramValueList;
	}

	private List<String> getTargetParameterList(CQLQuery cqlQuery) {

		if (cqlQuery.getTarget().getAttribute() != null) {
			paramValueList = getAttributeValueList(cqlQuery.getTarget().getAttribute());
		}
		if (cqlQuery.getTarget().getAssociation() != null) {
			paramValueList = getAssociationValueList(cqlQuery.getTarget().getAssociation());
		}
		if (cqlQuery.getTarget().getGroup() != null) {
			paramValueList = getGroupValueList(cqlQuery.getTarget().getGroup());
		}
		return paramValueList;
	}

	private List<String> getAttributeValueList(Attribute attribute) {
		LOG.debug("Attribute Name: "+attribute.getName()+" Value: "+attribute.getValue());
		String value = attribute.getValue();
		paramValueList.add(value);
		LOG.debug("Adding value to parameterList, size: "+paramValueList.size());
		return paramValueList;
	}

	private List<String> getAssociationValueList(Association association) {

		LOG.debug("Association Name"+association.getName()+" RoleName: "+association.getRoleName());
		if (association.getAttribute() != null) {
			paramValueList = getAttributeValueList(association.getAttribute());
		}
		if (association.getAssociation() != null) {
			paramValueList = getAssociationValueList(association.getAssociation());
		}
		if (association.getGroup() != null) {
			paramValueList = getGroupValueList(association.getGroup());
		}
		return paramValueList;
	}

	private List<String> getGroupValueList(Group group) {
		LOG.debug("Group Logic relation: "+group.getLogicRelation());
		if (group.getAssociation() != null) {
			for (int i = 0; i < group.getAssociation().length; i++) {
				paramValueList = getAssociationValueList(group
						.getAssociation(i));
			}
		}
		if (group.getAttribute() != null) {
			for (int i = 0; i < group.getAttribute().length; i++) {
				paramValueList = getAttributeValueList(group.getAttribute(i));
			}
		}
		if (group.getGroup() != null) {
			for (int i = 0; i < group.getGroup().length; i++) {
				paramValueList = getGroupValueList(group.getGroup(i));
			}
		}
		return paramValueList;
	}
}