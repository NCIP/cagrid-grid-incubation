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
package edu.uams.soulwing.cas.test;

import junit.framework.*; 
import junit.textui.*; 

/** 
 *  NullTestCase
 *  Tests nothing
 * 
 * @author 
 * 
 * @created May 8, 2008 4:01:13 PM
 * @version $Id:  Exp $ 
 */
public class NullTestCase extends TestCase {
    
    public NullTestCase(String name) {
        super(name);
    }

    public void testGetName() throws Exception 
    {
        String fileSpec = new String("c:\\xxx\\yyy\\zzz.txt");
        assertEquals("zzz.txt", fileSpec);
    }
    
    public static void main(String[] args) {
        String fileSpec = new String("c:\\xxx\\yyy\\zzz.txt");
	assertEquals("zzz.txt", fileSpec);
    }

}
