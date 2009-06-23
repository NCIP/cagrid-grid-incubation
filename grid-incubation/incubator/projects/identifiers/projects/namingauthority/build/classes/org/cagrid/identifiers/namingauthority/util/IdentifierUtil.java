package org.cagrid.identifiers.namingauthority.util;

public class IdentifierUtil {

	private static Object generateLock = new Object();
	
	public static String generate( String prefix ) {
		synchronized(generateLock) {
			return prefix + ":" + java.util.UUID.randomUUID();
		}
	}

}
