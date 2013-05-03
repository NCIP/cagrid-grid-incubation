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
package edu.umn.msi.cagrid.introduce.interfaces;

import java.io.IOException;
import java.util.Iterator;

import edu.umn.msi.cagrid.introduce.interfaces.Constants;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.DocletUtils;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.StringBufferUtils;
import static edu.umn.msi.cagrid.introduce.interfaces.codegen.DocletUtils.HasAnnotation;
import static edu.umn.msi.cagrid.introduce.interfaces.codegen.DocletUtils.HasDoclet;

import static com.google.common.collect.Iterables.filter;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;


/**
 * This class contains JUnit test cases for the DocletUtils class.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class DocletUtilsTestCase extends TestCase {
  private String contents = SourceProvider.getDocletTestContents();

  public void testEnumerate() throws Exception {
    Iterable<JavaField> fields = DocletUtils.enumerateFields(contents);
    assertEquals(4,count(fields));
    Iterable<JavaMethod> methods = DocletUtils.enumerateMethods(contents);
    assertEquals(5,count(methods));
  }

  public void testPredicates() throws Exception {
    Iterable<JavaField> fields;

    fields = DocletUtils.enumerateFields(contents);
    fields = filter(fields, new HasAnnotation());
    assertEquals(1, count(fields));

    fields = DocletUtils.enumerateFields(contents);
    fields = filter(fields, new HasAnnotation("edu.test.AnnotationTest"));
    assertEquals(1, count(fields));

    fields = DocletUtils.enumerateFields(contents);
    fields = filter(fields, new HasAnnotation("edu.test.OtherAnnotation"));
    assertEquals(0, count(fields));

    Iterable<JavaMethod> methods;

    methods = DocletUtils.enumerateMethods(contents);
    methods = filter(methods, new HasDoclet());
    assertEquals(2, count(methods));

    methods = DocletUtils.enumerateMethods(contents);
    methods = filter(methods, new HasDoclet("foo"));
    assertEquals(0, count(methods));

    methods = DocletUtils.enumerateMethods(contents);
    methods = filter(methods, new HasDoclet("InterfacesIntroduceExtension"));
    assertEquals(2, count(methods));

  }



  private int count(Iterable<?> iterator) {
    int count = 0;
    for(Object object : iterator) {
      count++;
    }
    return count;
  }


  public void testEliminateTag() throws IOException {
    StringBuffer source = new StringBuffer(contents);
    assertEquals(4, StringBufferUtils.countOccurrences(source, Constants.INTERFACES_ANNOTATION_TAG));
    DocletUtils.eliminateCommentsWithTag(Constants.INTERFACES_ANNOTATION_TAG, source);
    assertEquals(2, StringBufferUtils.countOccurrences(source, Constants.INTERFACES_ANNOTATION_TAG));
    assertTrue(source.indexOf("class DocletTest") > 0); 
    assertTrue(source.indexOf("private Double double") > 0);
    assertTrue(source.indexOf("@author Some Developer") > 0);
    assertTrue(source.indexOf("@implementsForService") > 0);    
  }

}
