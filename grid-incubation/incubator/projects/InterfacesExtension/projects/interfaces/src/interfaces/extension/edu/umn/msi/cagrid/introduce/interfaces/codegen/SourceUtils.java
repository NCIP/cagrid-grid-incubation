package edu.umn.msi.cagrid.introduce.interfaces.codegen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceUtils {

  public static void appendToClass(String codeBlock, StringBuffer stringBuffer) {
    stringBuffer.lastIndexOf("}");
  }
  
  public static void addImport(Class<?> class_, StringBuffer contents) {
    addImport(class_.getCanonicalName(), contents);
  }
  
  // Problem if source contains a first import inside a comment.
  public static void addImport(String fullyQualifiedClassName, StringBuffer contents) {
    Pattern importPattern = Pattern.compile("import\\s+" + fullyQualifiedClassName + "\\s*;");
    Matcher importMatcher = importPattern.matcher(contents);
    if(!importMatcher.find()) {
      int firstImportIndex = contents.indexOf("import");
      String importLine = "import " + fullyQualifiedClassName +";\n";
      contents.insert(firstImportIndex, importLine);
    }
  }
}
