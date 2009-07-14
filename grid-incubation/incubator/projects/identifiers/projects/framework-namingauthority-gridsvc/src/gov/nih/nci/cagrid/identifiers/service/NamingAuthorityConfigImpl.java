package gov.nih.nci.cagrid.identifiers.service;

import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class NamingAuthorityConfigImpl implements NamingAuthorityConfig {

	private String prefix = null;
	private int httpServerPort = 0;
	private String gridSvcUrl = null;
	private String dbUrl = null;
	private String dbUser = null;
	private String dbPassword = null;
	
	public void setPrefix( String prefix ) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public void setHttpServerPort( int port ) {
		this.httpServerPort = port;
	}

	public int getHttpServerPort() {
		return this.httpServerPort;
	}

	public void setGridSvcUrl(String gridSvcUrl) {
		this.gridSvcUrl = gridSvcUrl;
	}
	public String getGridSvcUrl() {
		return this.gridSvcUrl;
	}

	public void setDbUrl( String dbUrl ) { 
		this.dbUrl = dbUrl; 
	}
	
	public String getDbUrl() {
		return this.dbUrl;
	}

	public void setDbUser( String user ) {
		this.dbUser = user;
	}
	
	public String getDbUserName() {
		return this.dbUser;
	}

	public void setDbPassword( String pwd ) {
		this.dbPassword = pwd;
	}
	
	public String getDbPassword() {
		return this.dbPassword;
	}

}
