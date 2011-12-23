package edu.umn.msi.cagrid.introduce.interfaces;

/**
 * Various string constants used throughout the extension.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public interface Constants {
  public static final boolean DEBUG = true;
  public static final String INTERFACES_ANNOTATION_TAG = "InterfacesIntroduceExtension";
  public static final String IMPLEMENTED_JAVA_DOC = "  /**\n   * DO NOT REMOVE THIS COMMENT!\n   * @"+ INTERFACES_ANNOTATION_TAG + "\n   */\n";
  
  public static final String EXCEPTION_MESSAGE = "Delagated method exception.";
  public static final String DEFAULT_IDENTATION = "  ";
  public static final String NEWLINE = System.getProperty("line.separator");
  public static final String DEFAULT_METHOD_DESCRIPTION = "";
  public static final String DEFAULT_PARAMETER_DESCRIPTION = "";
  public static final String DEFAULT_METHOD_RETURN_DESCRIPTION = "";  
  public static final String IMPLEMENTING_CLIENT_POSTFIX  = "InterfacesClient";
  public static final String VERSION = "1.1";
  public static final String INTERFACES_XSD_NAMESPACE = "http://msi.umn.edu/cagrid/introduce/interfaces/" + VERSION;  
  
} 
