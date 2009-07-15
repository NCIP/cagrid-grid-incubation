package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class NamingAuthorityConfigImpl implements NamingAuthorityConfig {

	private String prefix = null;
	private Integer httpServerPort = null;
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
	
	public void setHttpServerPort( Integer port ) {
		this.httpServerPort = port;
	}

	public Integer getHttpServerPort() {
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
