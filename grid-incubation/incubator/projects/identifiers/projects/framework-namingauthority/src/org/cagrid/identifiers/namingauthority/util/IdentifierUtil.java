package org.cagrid.identifiers.namingauthority.util;

import java.util.UUID;

public class IdentifierUtil {

	public static synchronized String generate() {
		return java.util.UUID.randomUUID().toString();
	}
}
