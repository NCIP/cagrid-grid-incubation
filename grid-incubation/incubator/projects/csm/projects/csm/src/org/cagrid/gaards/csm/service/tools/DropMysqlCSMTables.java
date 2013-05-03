/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.csm.service.tools;

import org.cagrid.gaards.csm.service.CSMUtils;

/**
 * Drops all CSM tables in the database using the CSMUtils.dropMysqlDatabaseTables() method. Calling the main method
 * requires that properties files CSMUtils.CSM_CONFIGURATION and CSMUtils.CSM_PROPERTIES are located in the classpath.
 * This class was created to allow users to drop tables via an ANT target. See the "dropMysqlCSMTables" target in
 * dev-build.xml.
 * 
 * @author kgasper
 */
public class DropMysqlCSMTables {
	public static void main(String[] args) {
		initializeDatabase();
	}

	private static void initializeDatabase() {
		try {
			CSMUtils.dropMysqlDatabaseTables();
		} catch (Exception e) {
			System.out.println("Exception encountered while dropping CSM tables:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
