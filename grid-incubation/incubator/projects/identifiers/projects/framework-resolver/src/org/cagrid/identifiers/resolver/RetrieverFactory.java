package org.cagrid.identifiers.resolver;

public class ResolverFactory {
	public static Resolver getResolver( org.cagrid.identifiers.core.IdentifierValues ivs ) throws Exception {
		//TODO
		throw new Exception("Not implemented yet");
	}
	
	public static Resolver getResolver( ResolutionProfile.Type type ) throws Exception {
		if ( type == ResolutionProfile.Type.CQL )
			return new CQLResolver();
		
		throw new Exception("No resolver implemented for profile " + type.toString());
	}
}
