package org.cagrid.identifiers.resolver;

public class RetrieverFactory {
	public static Retriever getResolver( org.cagrid.identifiers.core.IdentifierValues ivs ) throws Exception {
		//TODO
		throw new Exception("Not implemented yet");
	}
	
	public static Retriever getResolver( RetrieverProfile.Type type ) throws Exception {
		if ( type == RetrieverProfile.Type.CQL )
			return new CQLRetriever();
		
		throw new Exception("No resolver implemented for profile " + type.toString());
	}
}
