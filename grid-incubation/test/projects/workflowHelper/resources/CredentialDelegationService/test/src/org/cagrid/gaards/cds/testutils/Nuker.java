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
package org.cagrid.gaards.cds.testutils;

import org.cagrid.tools.database.Database;

public class Nuker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = Utils.getDatabase();
			System.out.println("Destroying database........ "
					+ db.getDatabaseName());
			db.destroyDatabase();
			System.out.println("The database " + db.getDatabaseName()
					+ " was successfully destroyed!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
