package org.cagrid.redcap.test.system.steps;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

public class ConfigureRedcapDataServiceStep extends BaseStep{
	
	private TestCaseInfo tci;
    private ServiceContainer container;
	
    private static final String JDBCPWD = "cqlQueryProcessorConfig_jdbcPassword";
	private static final String CONNECTSTRING = "cqlQueryProcessorConfig_jdbcConnectString";
	private static final String USERNAME = "cqlQueryProcessorConfig_jdbcUserName";
	
	private static final String DBPWD = "db.password";
	private static final String DBUSER = "db.username";
	private static final String DBSERVER = "db.server";
	private static final String DBPORT = "db.port";
	private static final String DBSCHEMA = "db.schema.name";
    
    public ConfigureRedcapDataServiceStep(TestCaseInfo tci, ServiceContainer container, boolean build)
			throws Exception {
		super(tci.getDir(), build);
        this.tci = tci;
        this.container = container;
	}

	public void runStep() throws Throwable {
		
		InputStream dbProps = this.getClass().getResourceAsStream("/properties/test.database.properties");
		Properties dbProperties = new Properties();
		dbProperties.load(dbProps);
		
		String dbPassword = dbProperties.getProperty(DBPWD);
		String dbUsername = dbProperties.getProperty(DBUSER);
		String dbServer = dbProperties.getProperty(DBSERVER);
		String dbPort = dbProperties.getProperty(DBPORT);
		String dbSchemaName = dbProperties.getProperty(DBSCHEMA);
		String connectString = "jdbc:mysql://"+dbServer+":"+dbPort+"/"+dbSchemaName;
		dbProps.close();
	    
		//String servicePropertiesFile =  "/"+ getBaseDir() + "/service.properties";
		String servicePropertiesFile = "/service.properties";
		System.out.println("printing service properties file"+servicePropertiesFile);
		
		
		InputStream stream = this.getClass().getResourceAsStream(servicePropertiesFile);
		Properties props = new java.util.Properties();
		props.load(stream);
	 
		props.setProperty(JDBCPWD, dbPassword);
		props.setProperty(CONNECTSTRING, connectString);
		props.setProperty(USERNAME, dbUsername);
		 
		// Write properties file.
		try {
			String path = ".\\tmp\\"+getBaseDir()+"\\service.properties";
			//props.store(new FileOutputStream(".\\tmp\\RedcapDataService\\service.properties"), null);
			props.store(new FileOutputStream(path), null);
		} catch (IOException e) {
		     System.out.println("cannot write to properties file::::::::"+e.getMessage());
		}
		stream.close();

	}
}
