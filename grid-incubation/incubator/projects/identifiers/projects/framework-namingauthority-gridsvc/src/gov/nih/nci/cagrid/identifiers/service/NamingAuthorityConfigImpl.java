package gov.nih.nci.cagrid.identifiers.service;

import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class NamingAuthorityConfigImpl implements NamingAuthorityConfig {

	private String _prefix = "";
	private int _httpServerPort = 0;
	private String _gridSvcUrl = "";
	
	public void setPrefix( String prefix ) {
		_prefix = prefix;
	}
	
	public String getPrefix() {
		return _prefix;
	}
	
	public void setHttpServerPort( int port ) {
		_httpServerPort = port;
	}

	public int getHttpServerPort() {
		return _httpServerPort;
	}

	public void setGridSvcUrl(String gridSvcUrl) {
		_gridSvcUrl = gridSvcUrl;
	}
	public String getGridSvcUrl() {
		return this._gridSvcUrl;
	}

}
