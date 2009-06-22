package gov.nih.nci.cagrid.identifiers.na;

public abstract class NamingAuthority {
	
	public String _prefix;
	
	public NamingAuthority( NamingAuthorityConfig config ) {
		_prefix = config.getPrefix();
	}
	
	public String getPrefix() { return _prefix; } 
}
