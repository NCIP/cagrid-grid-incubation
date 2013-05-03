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
  package edu.umn.msi.cagrid.introduce.interfaces.codegen;

public class Regexes {

  // The following regular Java comment expressions are adapted from 
  // http://ostermiller.org/findcomment.html
  /**
   * Regular expression that captures the inner part of a Java doc comment, before close
   * and after open. 
   */
  public static final String JAVA_DOC_NOT_CLOSE = "([^*]|[\r\n]|(\\*+([^*/]|[\r\n])))*";
  /**
   * Regular expression that captures the opening of a Java doc comment.
   */
  public static final String JAVA_DOC_OPEN = "\\/\\*\\*";
  /**
   * Regular expression that captures the closing of a Java doc comment.
   */
  public static final String JAVA_DOC_CLOSE = "\\*+\\/";
  
  public static final String JAVA_COMMENT_MULTILINE = "\\/\\*([^*]|[\r\n]|(\\*+([^*/]|[\r\n])))*\\*+\\/";
  
  /**
   * Regular expression that captures valid java identifiers.
   */
  public static final String JAVA_IDENTIFIER = "[\\w\\$\\.]+";
  /**
   * Regular expression that captures a restricted set of java annotations,
   * namely those that don't contain a ). 
   */
  public static final String JAVA_ANNOTATION = "@" + JAVA_IDENTIFIER + "(\\([^\\)]*\\))?";
  
  public static final String JAVA_ANNOTATIONS = "(?:" + JAVA_ANNOTATION + "(?:\\s*" + JAVA_ANNOTATION + ")*)?"; 
  
  /**
   *  Regular expression that captures valid modifiers for class fields.
   */
  public static final String FIELD_MODIFIER = "(?:public|private|protected|static|transient|volatile|final)";
    
  public static final String FIELD_MODIFIERS = "(?:" + FIELD_MODIFIER + "(?:\\s+" + FIELD_MODIFIER + ")*)?";
  
  public static final String FIELD_DECLARATION_UNMODIFIED = JAVA_IDENTIFIER + "\\s+" + JAVA_IDENTIFIER + "\\s*;";
  
  
  /**
   * Regular expression that captures variable declarations, as long as 
   * there is no explicit initialization. It will also capture other 
   * valid pieces of Java code.
   */
  public static final String FIELD_DECLARATION = "(?:" + FIELD_MODIFIERS +"\\s+)?" + FIELD_DECLARATION_UNMODIFIED;
  
  public static final String METHOD_MODIFIER = "(?:public|private|protected|static|synchronized|abstract|native)";

  public static final String METHOD_MODIFIERS =  "(?:" + FIELD_MODIFIER + "(?:\\s+" + FIELD_MODIFIER + ")*)?";
  
  public static final String METHOD_PARAMETER = JAVA_IDENTIFIER + "\\s+" + JAVA_IDENTIFIER;
  
  public static final String METHOD_PARAMETERS = "(?:" + METHOD_PARAMETER + "(?:\\s*,\\s*" + METHOD_PARAMETER + ")*)?";
  
  public static final String METHOD_DECLARATION_UNMODIFIED = JAVA_IDENTIFIER + "\\s+" + JAVA_IDENTIFIER + "\\s*\\(\\s*" + METHOD_PARAMETERS + "\\s*\\)";
  
  public static final String THROWS_DECLARATION ="throws\\s+" + JAVA_IDENTIFIER + "(?:\\s*,\\s*" + JAVA_IDENTIFIER + ")*";  
  
  public static final String METHOD_DECLARATION = "(?:" + METHOD_MODIFIERS +"\\s+)?" + METHOD_DECLARATION_UNMODIFIED + "(?:\\s*" + THROWS_DECLARATION + ")?";
  
  public static final String WHITESPACE_NO_NEWLINE = "[\\s&&[^\n\r]]";
  
  public static final String WHITESPACE_TO_NEWLINE =  Regexes.WHITESPACE_NO_NEWLINE + "*[\n\r]";
  
  public static final String OPTIONAL_WHITESPACE_TO_NEWLINE = optional(WHITESPACE_TO_NEWLINE);
  
  public static final String MULTIPLE_WHITESPACE_ONLY_LINES = "[\n\r]" + WHITESPACE_TO_NEWLINE + "(?:" + WHITESPACE_TO_NEWLINE + ")+"; 
    
  public static String optional(String regex) {
    return "(?:" + regex + ")?";
  }
  
  // public static final String THROWS_DECLARATION = "throws\\s+" + JAVA_IDENTIFIER + ""

  public static String javaDocWithDocletTag(String tagName) {
    StringBuffer regularExpression = new StringBuffer();
    regularExpression.append(Regexes.JAVA_DOC_OPEN);
    regularExpression.append(Regexes.JAVA_DOC_NOT_CLOSE);
    regularExpression.append("@" + tagName);
    regularExpression.append(Regexes.JAVA_DOC_NOT_CLOSE);
    regularExpression.append(Regexes.JAVA_DOC_CLOSE);  
    return regularExpression.toString();
  }
  
}
