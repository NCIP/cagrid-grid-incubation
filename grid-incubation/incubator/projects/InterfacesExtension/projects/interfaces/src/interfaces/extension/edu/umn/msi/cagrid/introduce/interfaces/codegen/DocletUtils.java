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

import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;

import static edu.umn.msi.cagrid.introduce.interfaces.codegen.StringBufferUtils.replaceAll;

import com.google.common.base.Predicate;
import static com.google.common.collect.Iterables.filter;

/**
 * Utilities for dealing with doclet tags in java source files, 
 * using codehaus's QDox library.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu) 
 *
 */
public class DocletUtils {
  
  /**
   * Removes all the methods from the contents of a Java source 
   * file (specified by a StringBuffer) that are preceded by a
   * JavaDoc comment which contains the given doclet tag.
   * 
   * One caveat is that the method body must not contain any 
   * curly brackets.
   * 
   * @param tag 
   * @param source The StringBuffer containing the Java source file
   * contents to remove methods from
   */
  public static void eliminateSimpleMethodsWithTag(String tag, StringBuffer source) {
    StringBuffer regularExpression = new StringBuffer();
    regularExpression.append(Regexes.WHITESPACE_NO_NEWLINE + "*");
    regularExpression.append(Regexes.javaDocWithDocletTag(tag));
    regularExpression.append("\\s*");
    regularExpression.append(Regexes.JAVA_ANNOTATIONS);
    regularExpression.append("\\s*");
    regularExpression.append(Regexes.METHOD_DECLARATION);
    regularExpression.append("\\s*");
    regularExpression.append("\\{[^\\}]*\\}");
    regularExpression.append(Regexes.OPTIONAL_WHITESPACE_TO_NEWLINE);
    replaceAll(source, regularExpression,"");
  }

  /**
   * Removes all the class fields from the contents of a Java source 
   * file (specified by a StringBuffer) that are preceded by a
   * JavaDoc comment which contains the given doclet tag.
   * 
   * @param tag
   * @param source The StringBuffer containing the Java source file
   * contents to remove class fields from.
   */
  public static void eliminateFieldsWithTag(String tag, StringBuffer source) {
    StringBuffer regularExpression = new StringBuffer();
    regularExpression.append(Regexes.WHITESPACE_NO_NEWLINE + "*");
    regularExpression.append(Regexes.javaDocWithDocletTag(tag));
    regularExpression.append("\\s*");
    regularExpression.append(Regexes.JAVA_ANNOTATIONS);
    regularExpression.append("\\s*");
    regularExpression.append(Regexes.FIELD_DECLARATION);
    regularExpression.append(Regexes.OPTIONAL_WHITESPACE_TO_NEWLINE);
    replaceAll(source, regularExpression, "");
  }

  /**
   * Removes all the JavaDoc comments from the contents of a 
   * Java source file (specified by a StringBuffer) which contain
   * the given doclet tag.
   * 
   * @param tag
   * @param source
   */
  public static void eliminateCommentsWithTag(String tag, StringBuffer source) {
    StringBuffer regularExpression = new StringBuffer();
    regularExpression.append(Regexes.WHITESPACE_NO_NEWLINE + "*");
    regularExpression.append(Regexes.javaDocWithDocletTag(tag));
    regularExpression.append(Regexes.OPTIONAL_WHITESPACE_TO_NEWLINE);
    replaceAll(source, regularExpression, "");
  }


  public static class HasAnnotation implements Predicate<AbstractJavaEntity> {
    private String annotationType;

    public HasAnnotation() {}

    public HasAnnotation(String annotationType) {
      this.annotationType = annotationType;
    }

    public HasAnnotation(Class<?> annotationClass_) {
      this(annotationClass_.getCanonicalName());
    }

    public boolean apply(AbstractJavaEntity entity) {
      for(Annotation annotation : entity.getAnnotations()) {
        if(annotationType == null ||
            annotation.getType().getValue().equals(annotationType)) {
          return true;
        }
      }
      return false;
    }
  }

  public static class HasDoclet implements Predicate<AbstractJavaEntity> {
    private String docletName;
    private String docletValue;

    public HasDoclet() {}

    public HasDoclet(String docletName) {
      this.docletName = docletName;
    }

    public HasDoclet(String docletName, String docletValue) {
      this.docletName = docletName;
      this.docletValue = docletValue;
    }

    public boolean apply(AbstractJavaEntity entity) {
      for(DocletTag docletTag : entity.getTags()) {
        if(docletName == null) {
          return true;
        }
        if(docletTag.getName().equals(docletName) &&
            ((docletValue == null) || docletValue.equals(docletTag.getValue()))) {
          return true;
        }
      }
      return false;
    }
  }

  public static Iterable<JavaField> findFields(String contents, Predicate<? super JavaField> predicate) {
    Iterable<JavaField> fields = enumerateFields(contents);
    return filter(fields, predicate);
  }
  
  public static Iterable<JavaMethod> findMethods(String contents, Predicate<? super JavaMethod> predicate) {
    Iterable<JavaMethod> methods = enumerateMethods(contents);
    return filter(methods,predicate);
  }
  
  /**
   * 
   * @param source The contents of the Java source file
   * @return An Iterator over all of the class fields declared in
   * the given Java source file as QDOX JavaField objects
   */
  public static Iterable<JavaField> enumerateFields(String source) {
    JavaSource javaSource = getJavaSourceFromContents(source);
    LinkedList<JavaField> fields = new LinkedList<JavaField>();
    for(JavaClass class_ : javaSource.getClasses()) {
      fields.addAll(Arrays.asList(class_.getFields()));      
    }
    return fields;
  }

  /**
   * 
   * @param source The contents of the Java source file
   * @return An Iterator over all of the class methods declared in
   * the given Java source file as QDOX JavaMethod objects
   */
  public static Iterable<JavaMethod> enumerateMethods(String source) {
    JavaSource javaSource = getJavaSourceFromContents(source);
    LinkedList<JavaMethod> methods = new LinkedList<JavaMethod>();
    for(JavaClass class_ : javaSource.getClasses()) {
      methods.addAll(Arrays.asList(class_.getMethods()));
    }
    return methods;
  }

  /**
   * @see enumerateMethods(String)
   */
  public static Iterable<JavaMethod> enumerateMethods(StringBuffer source) {
    return enumerateMethods(source.toString());
  }

  /**
   * @see enumerateMethods(String)
   */
  public static Iterable<JavaField> enumerateFields(StringBuffer contents) {
    return enumerateFields(contents.toString());
  }

  private static JavaSource getJavaSourceFromContents(String contents) {
    JavaDocBuilder builder = new JavaDocBuilder();
    builder.addSource(new StringReader(contents));
    JavaSource source = builder.getSources()[0];	
    return source;
  }

}
