package org.cagrid.redcap.util;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.redcap.Data;
import org.cagrid.redcap.Events;
import org.cagrid.redcap.Forms;
import org.cagrid.redcap.processor.DatabaseConnectionSource;
import org.cagrid.redcap.processor.PooledDatabaseConnectionSource;
import org.cagrid.redcap.service.RedcapDataServiceConfiguration;

public class UserPrivilegesUtil {

	private static final Log LOG = LogFactory.getLog(UserPrivilegesUtil.class);
	private static final String PROJECTS_USER_RIGHTS_QUERY = "rc_userrights_projects";
	private static final String FORMS_USER_RIGHTS_QUERY = "rc_userrights_forms";
	private static final String EVENTS_USER_RIGHTS_QUERY = "rc_userrights_events";
	private String DATA_USER_RIGHTS_QUERY = "rc_userrights_data";
	
	public  ArrayList<Object> getAuthorizedProjects(String identity, DatabaseConnectionSource connectionSource,CQLQuery cqlQuery) throws QueryProcessingException{
		
		LOG.debug("Getting authorized projects for user with identity: (" + identity + ")");
		ArrayList<Object> projectIdList = new ArrayList<Object>();
		ArrayList<Object> formNameList = new ArrayList<Object>();
		ArrayList<Object> dataList = new ArrayList<Object>();
		
		PreparedStatement preparedStmt = null;
		Connection con = null;
		String userRightsQuery = null;
		try {
			PooledDatabaseConnectionSource source = (PooledDatabaseConnectionSource) connectionSource;
			con = source.getConnection();
			String userAuthQueries = RedcapDataServiceConfiguration.getConfiguration().getRcUserAuthQueries();
			if(cqlQuery.getTarget().getName().equals(Forms.class.getName())){
				userRightsQuery = PropertiesUtil.getQuery(FORMS_USER_RIGHTS_QUERY,userAuthQueries);
			}else if(cqlQuery.getTarget().getName().equals(Events.class.getName())){
				userRightsQuery = PropertiesUtil.getQuery(EVENTS_USER_RIGHTS_QUERY,userAuthQueries);
			}else if(cqlQuery.getTarget().getName().equals(Data.class.getName())){
				userRightsQuery = PropertiesUtil.getQuery(FORMS_USER_RIGHTS_QUERY,userAuthQueries);
			}else{
				userRightsQuery = PropertiesUtil.getQuery(PROJECTS_USER_RIGHTS_QUERY,userAuthQueries);
			}
			
			preparedStmt = con.prepareStatement(userRightsQuery);
			preparedStmt.setString(1, identity);
			preparedStmt.setString(2, identity);
			
			LOG.debug("Querying for user rights: " + preparedStmt.toString());
			ResultSet resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				if(cqlQuery.getTarget().getName().equals(Forms.class.getName()) ||
						cqlQuery.getTarget().getName().equals(Data.class.getName())){
					String formsName = resultSet.getString(1);
					formNameList = getFormNames(formsName,formNameList);
					
				}else{
					projectIdList.add(resultSet.getString(1));
				}
				LOG.debug("Adding " + resultSet.getString(1)+ " to user rights list");
			}
			
			if(cqlQuery.getTarget().getName().equals(Data.class.getName())){
				StringBuffer dataQuery = new StringBuffer(PropertiesUtil.getQuery(DATA_USER_RIGHTS_QUERY,userAuthQueries));
				dataQuery = dataQuery.append("(");
				for(int i=0;i<formNameList.size();i++){
					dataQuery.append("?");
					if(i<formNameList.size()-1){
						dataQuery.append(",");
					}
				}
				if(formNameList.size()==0){
					dataQuery.append("''");
				}
				dataQuery = dataQuery.append(")))");
				
				preparedStmt = con.prepareStatement(dataQuery.toString());
				preparedStmt.setString(1, identity);
				preparedStmt.setString(2, identity);
				
				for(int j=0;j<formNameList.size();j++){
					preparedStmt.setString(j+3, formNameList.get(j).toString());
				}
				LOG.debug("Data Query for Authorized data: "+preparedStmt.toString());
				ResultSet rs1 = preparedStmt.executeQuery();
				try{
					while(rs1.next()){
						Data data = new Data();
						data.setProjectId(rs1.getInt(1));
						data.setFieldName(rs1.getString(2));
						data.setEventId(rs1.getInt(3));
						data.setRecord(rs1.getInt(4));
						data.setValue(rs1.getString(5));
						LOG.debug("DATA PID "+data.getProjectId()+" FIELDNAME "+data.getFieldName()+" RECORD:"+data.getRecord()+" VALUE:"+data.getValue());
						dataList.add(data);
					}
				}catch(Exception e){
					throw new QueryProcessingException("Error getting data",e);
				}
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
			throw new QueryProcessingException(e);
		} catch(Exception exp){
			throw new QueryProcessingException(exp);
		}
		if(cqlQuery.getTarget().getName().equalsIgnoreCase(Forms.class.getName())){
			LOG.debug("Form Names list size "+formNameList.size());
			return formNameList;
		}
		if(cqlQuery.getTarget().getName().equalsIgnoreCase(Data.class.getName())){
			LOG.debug("Data list size "+dataList.size());
			return dataList;
		}
		return projectIdList;
	}
	
	private ArrayList<Object> getFormNames(String formsName,ArrayList<Object> list){
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
