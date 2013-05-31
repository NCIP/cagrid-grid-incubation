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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

public class SourceProvider {
  static HashMap<String,String> contentMap = new HashMap<String,String>();
  static {
    contentMap.put("add", readContents("AddTest.java"));
    contentMap.put("doclet", readContents("DocletTest.java"));
    contentMap.put("java", readContents("TestSource.java"));
    contentMap.put("akward", readContents("AkwardTest.java"));
    contentMap.put("springEmpty", readContents("SpringEmptyTest.java"));
    contentMap.put("springFirst", readContents("SpringFirstTest.java"));
    contentMap.put("hello1", readContents("HelloWorld1.java"));
    contentMap.put("hello2", readContents("HelloWorld2.java"));
    contentMap.put("hello3", readContents("HelloWorld3.java"));
    contentMap.put("hello4", readContents("HelloWorld4.java"));
    contentMap.put("hello5", readContents("HelloWorld5.java"));
    contentMap.put("generic", readContents("GenericTest.java"));
  }
  
  public static String getHello5TestContents() {
    return contentMap.get("hello5");
  }
  
  public static String getHello1TestContents() {
    return contentMap.get("hello1");
  }
  
  public static String getHello2TestContents() {
    return contentMap.get("hello2");
  }

  public static String getHello3TestContents() {
    return contentMap.get("hello3");
  }

  public static String getHello4TestContents() {
    return contentMap.get("hello4");
  }

  public static String getSpringEmptyTestContents() {
    return contentMap.get("springEmpty");
  }

  public static String getAkwardTestContents() {
    return contentMap.get("akward");
  }

  public static String getTestContents() {
    return contentMap.get("java");
  }

  public static String getAddTestContents() {
    return contentMap.get("add");
  }

  public static String getDocletTestContents() {
    return contentMap.get("doclet");
  }

  public static String getSpringFirstTestContents() {
    return contentMap.get("springFirst");
  }
  
  static String readContents(String resource) {
    try {
      InputStream is = SourceProvider.class.getResourceAsStream(resource);
      return IOUtils.toString(is);
    } catch(IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static String getGenericTestContents() {
    return contentMap.get("generic");
  } 
}
