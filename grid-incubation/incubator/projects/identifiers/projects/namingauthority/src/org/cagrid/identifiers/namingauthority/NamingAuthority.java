package org.cagrid.identifiers.namingauthority;

public abstract class NamingAuthority {
	
	public String _prefix;
	public int _httpServerPort;
	
	public NamingAuthority( NamingAuthorityConfig config ) {
		_prefix = config.getPrefix();
		_httpServerPort = config.getHttpServerPort();
	}
	
	public String getPrefix() { return _prefix; }
	public int getHttpServerPort(){ return _httpServerPort; }
}
