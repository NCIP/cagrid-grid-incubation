package edu.umn.msi.cagrid.introduce.interfaces;


import edu.umn.msi.cagrid.introduce.interfaces.codegen.Regexes;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.StringBufferUtils;
import junit.framework.TestCase;

public class StringBufferUtilsTestCase extends TestCase {
	
	// TODO: Fill out test case
	public void testReplace() {
		StringBuffer buffer;
		
		buffer = new StringBuffer("  1  2\t 3 ");
		StringBufferUtils.replaceAll(buffer, "\\s+", "");
		assertEquals("123", buffer.toString());
		
		buffer = new StringBuffer("  1  2\t 3 ");
		StringBufferUtils.replaceAll(buffer, "\\s", "");
		assertEquals("123", buffer.toString());
		
		buffer = new StringBuffer("\n  \r \n \t\n");
		StringBufferUtils.replaceAll(buffer, Regexes.MULTIPLE_WHITESPACE_ONLY_LINES, "");
		assertEquals("",buffer.toString());
		
		buffer = new StringBuffer("Hello\n  \r \n \t\n World!");
    StringBufferUtils.replaceAll(buffer, Regexes.MULTIPLE_WHITESPACE_ONLY_LINES, "");
    assertEquals("Hello World!",buffer.toString());
	}
	
	
	
	
}
