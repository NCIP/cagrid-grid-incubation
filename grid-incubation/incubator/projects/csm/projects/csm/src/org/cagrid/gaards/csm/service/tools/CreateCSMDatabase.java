package org.cagrid.gaards.csm.service.tools;

import org.cagrid.gaards.csm.service.CSMUtils;

/**
 * Creates and initializes the CSM database using the CSMUtils.createCSMDatabase() method. If a database already exists
 * with the same name, it will be overwritten. Calling the main method requires that properties files
 * CSMUtils.CSM_CONFIGURATION and CSMUtils.CSM_PROPERTIES are located in the classpath. This class was created to enable
 * users to create the CSM database from an Ant target. See the "createMysqlCsmDatabase" target in dev-build.xml.
 * 
 * @author kgasper
 */
public class CreateCSMDatabase {
	public static void main(String[] args) {
		createDatabase();
	}

	private static void createDatabase() {
		try {
			CSMUtils.createCSMDatabase();
		} catch (Exception e) {
			System.out.println("Exception encountered during CSM database creation: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
