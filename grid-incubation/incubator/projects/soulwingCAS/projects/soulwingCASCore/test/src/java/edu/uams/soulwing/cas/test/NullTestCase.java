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
