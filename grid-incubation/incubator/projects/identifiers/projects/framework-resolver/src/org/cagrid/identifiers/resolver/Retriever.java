package org.cagrid.identifiers.resolver;

import org.cagrid.identifiers.core.IdentifierValues;

public interface Retriever {
	public Object resolve( IdentifierValues ivs ) throws Exception;
}
