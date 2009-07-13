package org.cagrid.identifiers.namingauthority;

public abstract class NamingAuthority {
	
	private NamingAuthorityConfig config;
	
	public NamingAuthority( NamingAuthorityConfig config ) {
		this.config = config;
	}
	
	public NamingAuthorityConfig getConfig() { return this.config; }
}
