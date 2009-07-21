package org.cagrid.identifiers.namingauthority.util;

import java.util.UUID;

public class IdentifierUtil {

	public static synchronized String generate( String prefix ) {
		UUID localId = java.util.UUID.randomUUID();
		return generate(prefix, localId.toString() );
	}
	
	public static String generate( String prefix, String localId ) {
		if (prefix.endsWith("/"))
			return prefix + localId;
		return prefix + "/" + localId;
	}

}
