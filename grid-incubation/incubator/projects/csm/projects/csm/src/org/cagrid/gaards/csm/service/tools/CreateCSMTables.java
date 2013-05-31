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
package org.cagrid.gaards.csm.service.tools;

import org.cagrid.gaards.csm.service.CSMUtils;

/**
 * Creates and initializes the CSM tables in an existing CSM database using using the CSMUtils.createCSMTables() method.
 * If CSM tables already exist, they will be overwritten. However, other tables in the specified database will be
 * preserved. Calling the main method requires that properties files CSMUtils.CSM_CONFIGURATION and
 * CSMUtils.CSM_PROPERTIES are located in the classpath. This class was created to enable users to create the CSM
 * database from an Ant target. See the "createDatabase" target in dev-build.xml.
 * 
 * @author kgasper
 */
public class CreateCSMTables {
	public static void main(String[] args) {
		createTables();
	}

	private static void createTables() {
		try {
			CSMUtils.createCSMTables();
		} catch (Exception e) {
			System.out.println("Exception encountered during database table creation: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
