package org.cagrid.identifiers.resolver;

import org.cagrid.identifiers.core.IdentifierValues;

public interface Retriever {
	public Object retrieve( IdentifierValues ivs ) throws Exception;
}
