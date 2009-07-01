package org.cagrid.identifiers.resolver;

import org.cagrid.identifiers.core.IdentifierValues;

public interface Resolver {
	public Object resolve( IdentifierValues ivs ) throws Exception;
}
