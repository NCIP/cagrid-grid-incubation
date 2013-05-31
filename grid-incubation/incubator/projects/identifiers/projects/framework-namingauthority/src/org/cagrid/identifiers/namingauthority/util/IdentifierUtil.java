/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.identifiers.namingauthority.util;

import java.util.UUID;

public class IdentifierUtil {

	public static synchronized String generate() {
		return java.util.UUID.randomUUID().toString();
	}
}
