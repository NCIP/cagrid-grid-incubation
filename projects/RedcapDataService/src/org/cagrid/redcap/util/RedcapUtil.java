package org.cagrid.redcap.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
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
					if(targetList.get(i)!=null){
						LOG.debug("Parameter at "+i+" :"+targetList.get(i).toString());
						if(targetList.get(i) instanceof java.util.Date){
							query.setDate(i, (java.util.Date)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Boolean){
							query.setBoolean(i, (java.lang.Boolean)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Integer){
							query.setInteger(i, (java.lang.Integer)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Long){
							query.setLong(i, (java.lang.Long)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Double){
							query.setDouble(i, (java.lang.Double)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Float){
							query.setFloat(i, (java.lang.Float)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.Character){
							query.setCharacter(i, (java.lang.Character)targetList.get(i));
						}else if(targetList.get(i) instanceof java.lang.String){
							query.setString(i, (java.lang.String)targetList.get(i));
						}else{					
							query.setParameter(i, targetList.get(i));
						}
					}	
				}
			}
			
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
}