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
package edu.umn.msi.cagrid.introduce.interfaces;

import edu.umn.msi.cagrid.introduce.interfaces.codegen.MethodBody;
import junit.framework.TestCase;

public class MethodBodyTestCase extends TestCase {
  private String docletSourceContents = SourceProvider.getDocletTestContents();
  private String akwardSourceContents = SourceProvider.getAkwardTestContents();

  public void testBoundary() throws Exception {
    StringBuffer docletSourceBuffer = new StringBuffer(docletSourceContents);
    MethodBody body = new MethodBody(docletSourceBuffer, "public int bar(String str)");
    CharSequence sequence = body.getContents();
    assertTrue(sequence.toString().contains("return 1;"));
    assertFalse(sequence.toString().contains("{"));
    assertFalse(sequence.toString().contains("}"));

    StringBuffer akwardSourceBuffer = new StringBuffer(akwardSourceContents);
    String[] methodNames = new String[]{"oneLine","oneLineCondensed","topRun","buttomRun","spread","badTab","badTabSpread"};
    for(String methodName : methodNames) {
      body = new MethodBody(akwardSourceBuffer, "public void " + methodName + "()");
      assertMethodBody(body.getContents());
    }
  }

  public void testContains() throws Exception {
    StringBuffer akwardSourceBuffer = new StringBuffer(akwardSourceContents);
    String[] methodNames = new String[]{"oneLine","oneLineCondensed","topRun","buttomRun","spread","badTab","badTabSpread"};
    for(String methodName : methodNames) {
      MethodBody body = new MethodBody(akwardSourceBuffer, "public void " + methodName + "()");
      assertTrue(body.contains("1\\+2;"));
      assertFalse(body.contains("[{}]"));
    }	
    MethodBody constructorBody = new MethodBody(akwardSourceBuffer, "public AkwardTest()");
    assertTrue(constructorBody.contains("super"));
  }

  public void testIdentation() {
    StringBuffer akwardSourceBuffer = new StringBuffer(akwardSourceContents);
    MethodBody twoFoo = new MethodBody(akwardSourceBuffer, "public void two()");
    MethodBody fourFoo = new MethodBody(akwardSourceBuffer, "public void four()");
    MethodBody tabFoo = new MethodBody(akwardSourceBuffer, "public void tab()");
    assertEquals("  ", twoFoo.guessIndentation());
    assertEquals("    ", fourFoo.guessIndentation());
    assertEquals("\t", tabFoo.guessIndentation());
    assertEquals("    int x;\n    x=5;\n    x++;", twoFoo.indent("int x;\nx=5;\nx++;"));
    assertEquals("        java.lang.String str = null;\r        str.hello();", fourFoo.indent("java.lang.String str = null;\rstr.hello();"));
    assertEquals("\t\tfloat f = 1.0;", tabFoo.indent("float f = 1.0;"));
  }


  private static void assertMethodBody(CharSequence sequence) {
    assertTrue(sequence.toString().contains("1+2;"));
    assertFalse(sequence.toString().contains("{"));
    assertFalse(sequence.toString().contains("}"));
  }

  public void testAppend() throws Exception {
    StringBuffer akwardSourceBuffer = new StringBuffer(akwardSourceContents);
    MethodBody body = new MethodBody(akwardSourceBuffer, "public void oneLineCondensed()");
    body.append("init();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){1+2;init();}")>0);
    body.append("foo();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){1+2;init();foo();}")>0);
    body.setContents("init();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){init();}")>0);
    body.append("foo();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){init();foo();}")>0);
  }

  public void testPrepend() throws Exception {
    StringBuffer akwardSourceBuffer = new StringBuffer(akwardSourceContents);
    MethodBody body = new MethodBody(akwardSourceBuffer, "public void oneLineCondensed()");
    body.prepend("init();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){init();1+2;}")>0);
    body.prepend("foo();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){foo();init();1+2;}")>0);
    body.setContents("init();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){init();}")>0);
    body.prepend("foo();");
    assertTrue(akwardSourceBuffer.indexOf("oneLineCondensed(){foo();init();}")>0);
  }


  public void testSetMethodBody() throws Exception {
    StringBuffer docletSourceBuffer = new StringBuffer(docletSourceContents);
    MethodBody body = new MethodBody(docletSourceBuffer, "public int bar(String str)");
    body.setContents("\tint x = 1 + 2;");
    MethodBody newBody = new MethodBody(docletSourceBuffer, "public int bar(String str)");
    assertEquals("\tint x = 1 + 2;", newBody.getContents());
  }

}
