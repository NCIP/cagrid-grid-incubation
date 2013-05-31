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
package org.cagrid.workflow.manager.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	public static String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));

		char[] chars = new char[1024];
		int numRead = 0;
		while( (numRead = reader.read(chars)) > -1){
			sb.append(String.valueOf(chars, 0, numRead));	
		}
		reader.close();

		return sb.toString();
	}	
	
}
