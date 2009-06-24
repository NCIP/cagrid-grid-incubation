package org.cagrid.identifiers.namingauthority.util;

import java.util.UUID;

public class IdentifierUtil {

	private static Object generateLock = new Object();
	
	public static String generate( String prefix ) {
		synchronized(generateLock) {
			UUID localId = java.util.UUID.randomUUID();
			return generate(prefix, localId.toString() );
		}
	}
	
	public static String generate( String prefix, String localId ) {
		if (prefix.endsWith("/"))
			return prefix + localId;
		return prefix + "/" + localId;
	}

}
