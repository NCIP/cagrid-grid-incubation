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
package org.cagrid.gaards.csm.filters.service.tools;

public class StringUtils {

	
	/**
	 * This method appends wild cards before and after a string./
	 * 
	 * @param _name
	 * @return
	 */
	public static String wildCard(final String _name) {
		String trimmedName = _name.toString().trim();

		if (trimmedName == null || trimmedName.equals("")
				|| trimmedName.length() == 0) {
			return "";
		}

		if (trimmedName.length() > 0 && !trimmedName.endsWith("%")) {
			trimmedName = trimmedName + "%";
		}

		if (trimmedName.length() > 0 && !trimmedName.startsWith("%")) {
			trimmedName = "%" + trimmedName;
		}

		return trimmedName;
	}

	/**
	 * returns true of the string is blank or null
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		boolean test = false;
		if (str == null) {
			test = true;
		} else {
			if (str.equals("")) {
				test = true;
			}
		}
		return test;
	}

	/**
	 * initializes a string. 
	 * @param str
	 * @return
	 */
	public static String initString(String str) {
		String test = "";
		if (str != null) {
			test = str.trim();
		}
		return test;
	}

}
