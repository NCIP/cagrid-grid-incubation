package org.cagrid.redcap.util;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.redcap.Events;
import org.cagrid.redcap.Forms;
import org.cagrid.redcap.processor.DatabaseConnectionSource;
import org.cagrid.redcap.processor.PooledDatabaseConnectionSource;

public class UserPrivilegesUtil {

	private static final Log LOG = LogFactory.getLog(UserPrivilegesUtil.class);
	private static final String PROJECTS_USER_RIGHTS_QUERY = "rc_userrights_projects";
	private static final String FORMS_USER_RIGHTS_QUERY = "rc_userrights_forms";
	private static final String EVENTS_USER_RIGHTS_QUERY = "rc_userrights_events";
	
	public static ArrayList<String> getAuthorizedProjects(String identity, DatabaseConnectionSource connectionSource,CQLQuery cqlQuery) {
		
		LOG.debug("Getting authorized projects for user with identity: (" + identity + ")");
		ArrayList<String> projectIdList = new ArrayList<String>();
		ArrayList<String> formNameList = new ArrayList<String>();
		PreparedStatement preparedStmt = null;
		Connection con = null;
		String userRightsQuery = null;
		try {
			PooledDatabaseConnectionSource source = (PooledDatabaseConnectionSource) connectionSource;
			con = source.getConnection();
			
			if(cqlQuery.getTarget().getName().equals(Forms.class.getName())){
				userRightsQuery = PropertiesUtil.getQuery(FORMS_USER_RIGHTS_QUERY);
			}else if(cqlQuery.getTarget().getName().equals(Events.class.getName())){
				userRightsQuery = PropertiesUtil.getQuery(EVENTS_USER_RIGHTS_QUERY);
			}else{
				userRightsQuery = PropertiesUtil.getQuery(PROJECTS_USER_RIGHTS_QUERY);
			}
			
			preparedStmt = con.prepareStatement(userRightsQuery);
			preparedStmt.setString(1, identity);
			preparedStmt.setString(2, identity);
			
			LOG.debug("Querying for user rights: " + preparedStmt.toString());
			ResultSet resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				if(cqlQuery.getTarget().getName().equals(Forms.class.getName())){
					String formsName = resultSet.getString(1);
					formNameList = getFormNames(formsName,formNameList);
				}else{
					projectIdList.add(resultSet.getString(1));
				}
				LOG.debug("Adding " + resultSet.getString(1)+ " to user rights list");
			}
			
			LOG.debug("Authorized projects list size" + projectIdList.size());
			resultSet.close();
			preparedStmt.close();
			con.close();
		} catch (SQLException e) {
			LOG.error("Error querying for user rights",e);
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		if(cqlQuery.getTarget().getName().equalsIgnoreCase(Forms.class.getName())){
			LOG.debug("Form Names list size "+formNameList.size());
			return formNameList;
		}
		return projectIdList;
	}
	
	private static ArrayList<String> getFormNames(String formsName,ArrayList<String> list){
		String replacedString = formsName.replaceAll("]", " ");
		replacedString = replacedString.replaceAll("\\[", " ");
		StringTokenizer st = new StringTokenizer(replacedString, "  ");
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if(token.endsWith(",2")||token.endsWith(",1")){
				String formName = token.substring(0, token.length()-2);
				list.add(formName);
			}
		}
		return list;
	}
}
