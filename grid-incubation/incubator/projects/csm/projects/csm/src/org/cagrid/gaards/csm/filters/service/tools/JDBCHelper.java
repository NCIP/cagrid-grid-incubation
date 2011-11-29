package org.cagrid.gaards.csm.filters.service.tools;

import gov.nih.nci.security.exceptions.CSException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;

/**
 * JDBC Helper class is created to test the database connection parameters
 * available from UPT's ApplicationForm.
 * 
 * @author parmarv
 * 
 */
public class JDBCHelper {


	
	/**
	 * This method uses Hibernates SessionFactory to get a Session and using Hibernates Criteria does a sample query 
	 * to connection and obtain results as part of testing for successful connection.
	 * 
	 * Based on the kind of exceptions this method throws CSException with appropriate message.
	 * @param appForm -
	 *            The ApplicationForm with application database parameters to
	 *            test connection for.
	 * @return String - The message indicating that connection and a SQL query
	 *         was successful
	 * @throws CSException -
	 *             The exception message indicates which kind of application
	 *             database parameters are invalid.
	 */
	public static String testConnectionHibernate(AnnotationConfiguration configuration) throws CSException {
		
		
		SessionFactory sf = null;
		ResultSet rs = null;
		Statement stmt=null;
		Connection conn = null;
		Session session = null;
		try {
			sf = configuration.buildSessionFactory();
			session = sf.openSession();
			conn = session.connection();
			stmt = conn.createStatement();
			stmt.execute("select count(*) from csm_application");
			rs = stmt.getResultSet();

			System.out.println(rs.getMetaData().getColumnCount());
			
			return DisplayConstants.APPLICATION_DATABASE_CONNECTION_SUCCESSFUL;
		
		} catch(Throwable t){
			// Depending on the cause of the exception obtain message and throw a CSException.
			if(t instanceof SQLGrammarException){
				throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED+"<BR>"+t.getCause().getMessage());
			}
			if(t instanceof JDBCConnectionException){
//				if(t.getCause() instanceof CommunicationsException){
//					throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED_URL_SERVER_PORT);
//				}
				if(t.getCause() instanceof SQLException){
					throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED_URL);
				}
				throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED+"<BR>"+t.getMessage());
			}
			if(t instanceof GenericJDBCException){
				throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED_URL_USER_PASS+"<BR>");
			}
			if(t instanceof CacheException){
				throw new CacheException("Please Try Again.\n ");
				
			}
			if(t instanceof HibernateException){
				throw new CSException(DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED+"<BR>"+t.getMessage());
			}
			
			throw new CSException(
					DisplayConstants.APPLICATION_DATABASE_CONNECTION_FAILED_URL_USER_PASS);
		}finally{
			try{
				sf.close();
				rs.close();
				stmt.close();
				conn.close();
				session.close();
			}catch(Exception e){}

		}

	}
	

}
