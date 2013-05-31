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
package org.cagrid.redcap.test.system.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.redcap.test.system.RCDSTestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

public class ConfigureRedcapDataServiceStep extends BaseStep{
	
	private RCDSTestCaseInfo tci;
    private ServiceContainer container;
	
    private static final String JDBCPWD = "cqlQueryProcessorConfig_jdbcPassword";
	private static final String CONNECTSTRING = "cqlQueryProcessorConfig_jdbcConnectString";
	private static final String USERNAME = "cqlQueryProcessorConfig_jdbcUserName";
	
	private static final String DBPWD = "db.password";
	private static final String DBUSER = "db.username";
	private static final String DBSERVER = "db.server";
	private static final String DBPORT = "db.port";
	private static final String DBSCHEMA = "db.schema.name";
    
	private static final Log LOG = LogFactory.getLog(ConfigureRedcapDataServiceStep.class);
	
	public ConfigureRedcapDataServiceStep(RCDSTestCaseInfo tci, ServiceContainer container, boolean build)
			throws Exception {
		super(tci.getDir(), build);
        this.tci = tci;
        this.container = container;
	}

	public void runStep() throws Throwable {
		String path1 = System.getProperty("basedir")+File.separator+"test\\resources\\properties\\test.database.properties";
		FileInputStream fis = new FileInputStream(path1);
		
		Properties dbProperties = new Properties();
		dbProperties.load(fis);
		
		String dbPassword = dbProperties.getProperty(DBPWD);
		String dbUsername = dbProperties.getProperty(DBUSER);
		String dbServer = dbProperties.getProperty(DBSERVER);
		String dbPort = dbProperties.getProperty(DBPORT);
		String dbSchemaName = dbProperties.getProperty(DBSCHEMA);
		String connectString = "jdbc:mysql://"+dbServer+":"+dbPort+"/"+dbSchemaName;
		String authorization=dbProperties.getProperty("authorization");
				
		String servicePropsLoc = tci.getTempDir()+File.separator+"service.properties";
		FileInputStream stream = new FileInputStream(servicePropsLoc);
		Properties props = new java.util.Properties();
		props.load(stream);
	 
		props.setProperty(JDBCPWD, dbPassword);
		props.setProperty(CONNECTSTRING, connectString);
		props.setProperty(USERNAME, dbUsername);
		props.setProperty("authorization", authorization); 
		
		LOG.debug("Configuring service with Authorization turned:"+authorization);
		// Write properties file.
		try {
			props.store(new FileOutputStream(servicePropsLoc), null);
		} catch (IOException e) {
		     LOG.error("Error writing to properties file:"+e.getMessage());
		}
		stream.close();
	}
}
