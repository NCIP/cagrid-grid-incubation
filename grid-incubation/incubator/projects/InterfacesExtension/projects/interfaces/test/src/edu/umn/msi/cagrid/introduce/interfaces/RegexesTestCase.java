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

import edu.umn.msi.cagrid.introduce.interfaces.codegen.Regexes;
import junit.framework.TestCase;

public class RegexesTestCase extends TestCase {
  public void testIdentifier() {
    assertMatches("foobar", Regexes.JAVA_IDENTIFIER);
    assertMatches("f", Regexes.JAVA_IDENTIFIER);
    assertMatches("f00bar1", Regexes.JAVA_IDENTIFIER);
    assertMatches("foobar_", Regexes.JAVA_IDENTIFIER);
    assertMatches("foobar12", Regexes.JAVA_IDENTIFIER);
    assertMatches("edu.umn.Foobar.foo",Regexes.JAVA_IDENTIFIER);
    assertMatches("edu.umn.Foobar.fo$o",Regexes.JAVA_IDENTIFIER);
    
    assertNotMatches(" foobar", Regexes.JAVA_IDENTIFIER);
    assertNotMatches("", Regexes.JAVA_IDENTIFIER);
    assertNotMatches("     ", Regexes.JAVA_IDENTIFIER);
    assertNotMatches("int x", Regexes.JAVA_IDENTIFIER);
    assertNotMatches("x;", Regexes.JAVA_IDENTIFIER);
  }
  
  public void testFieldModifiers() {
    assertMatches("public", Regexes.FIELD_MODIFIER);
    assertMatches("static", Regexes.FIELD_MODIFIER);
    assertMatches("public", Regexes.FIELD_MODIFIERS);
    assertMatches("static", Regexes.FIELD_MODIFIERS);
    assertMatches("", Regexes.FIELD_MODIFIERS);
  }
  
  public void testFieldDeclaration() {
    assertMatches("int x;", Regexes.FIELD_DECLARATION);
    assertMatches("public int x;", Regexes.FIELD_DECLARATION);
    assertMatches("public static int x;", Regexes.FIELD_DECLARATION);
    assertMatches("public static int x;", Regexes.FIELD_DECLARATION);
    assertMatches("java.lang.String str;", Regexes.FIELD_DECLARATION);
    assertMatches("private final java.lang.String str;", Regexes.FIELD_DECLARATION);
    
    assertNotMatches("int x", Regexes.FIELD_DECLARATION);
    assertNotMatches("publi int x;", Regexes.FIELD_DECLARATION);
    assertNotMatches("int static x;", Regexes.FIELD_DECLARATION);
    assertNotMatches("public static int x", Regexes.FIELD_DECLARATION);
    assertNotMatches("java.lang.String", Regexes.FIELD_DECLARATION);
    assertNotMatches("", Regexes.FIELD_DECLARATION);
  }
  
  public void testMethodDeclaration() {
   assertMatches("int foo(int x)", Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertNotMatches("float int foo(int x)",  Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertNotMatches("int foo(float int x)",  Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertNotMatches("foo(int x)",  Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertNotMatches("foo(x)",  Regexes.METHOD_DECLARATION_UNMODIFIED);   
   assertNotMatches("foo",  Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertMatches("int foo(int x, java.lang.Object arg2)", Regexes.METHOD_DECLARATION_UNMODIFIED);
   assertMatches("int foo(int x, java.lang.Object arg2, AnotherType anotherArg)", Regexes.METHOD_DECLARATION_UNMODIFIED);
   
   assertMatches("int foo(int x)", Regexes.METHOD_DECLARATION);
   assertNotMatches("float int foo(int x)",  Regexes.METHOD_DECLARATION);
   assertNotMatches("int foo(float int x)",  Regexes.METHOD_DECLARATION);
   assertNotMatches("foo(int x)",  Regexes.METHOD_DECLARATION);
   assertNotMatches("foo(x)",  Regexes.METHOD_DECLARATION);
   assertNotMatches("foo",  Regexes.METHOD_DECLARATION);   
   assertMatches("int foo(int x, java.lang.Object arg2)", Regexes.METHOD_DECLARATION);
   assertMatches("int foo(int x, java.lang.Object arg2, AnotherType anotherArg)", Regexes.METHOD_DECLARATION);

   assertMatches("public int foo(int x)", Regexes.METHOD_DECLARATION);
   assertMatches("public static int foo(int x)", Regexes.METHOD_DECLARATION);
   assertMatches("public int foo(int x) throws Exception", Regexes.METHOD_DECLARATION);
   assertMatches("public static int foo(int x) throws Exception", Regexes.METHOD_DECLARATION);
   assertMatches("public int foo(int x) throws Exception,RemoteException", Regexes.METHOD_DECLARATION);
   assertMatches("public static int foo(int x) throws Exception , \nRemoteException", Regexes.METHOD_DECLARATION);
   assertNotMatches("public static int foo(int x) throws Exception\nRemoteException", Regexes.METHOD_DECLARATION);
   assertNotMatches("public static int foo(int x) throws Exception RemoteException", Regexes.METHOD_DECLARATION);
   assertNotMatches("int foo(int x) throws Exception RemoteException", Regexes.METHOD_DECLARATION);
   assertNotMatches("public static foo throws Exception RemoteException", Regexes.METHOD_DECLARATION);
  }
  
  public void testNewline() {
    assertMatches("\t", Regexes.WHITESPACE_NO_NEWLINE);
    assertMatches(" ", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches("\n", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches("\r", Regexes.WHITESPACE_NO_NEWLINE);
    assertMatches("\n  \n \r\t\n\n", Regexes.MULTIPLE_WHITESPACE_ONLY_LINES);
    /*
    assertNotMatches("\n", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches("\r", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches("H", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches("ello World!", Regexes.WHITESPACE_NO_NEWLINE);
    assertNotMatches(" ello World!", Regexes.WHITESPACE_NO_NEWLINE);
    assertMatches("\n  \n \r \n", Regexes.MULTIPLE_WHITESPACE_ONLY_LINES);
    */
  }
  
  public static void assertMatches(String str, String regex) {
    assertTrue(str.matches(regex));
  }
  
  public static void assertNotMatches(String str, String regex) {
    assertFalse(str.matches(regex));
  }
  
}
