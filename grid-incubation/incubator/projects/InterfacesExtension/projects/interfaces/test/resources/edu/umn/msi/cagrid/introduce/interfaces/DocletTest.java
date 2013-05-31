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
package some.Test;

import some.random.Class1;
import other.random.Class2;
import edu.test.AnnotationTest;

/**
 *  
 * @author Some Developer
 *
 */
public class DocletTest {
  /**
   * @implementsForService = "Class1Interface";
   * @implementsForService = "AnotherClassInterface";
   */
  @AnnotationTest({"moo","cow"})
  private Class1 object1; 

  private Class2 object2;

  private String str;

  private Double double;

  /** @InterfacesIntroduceExtension */
  public void foo() {

  }

  /** A comment here.
   * 
   * @InterfacesIntroduceExtension */
  public void foo(int arg1) {

  }

  /* @InterfacesIntroduceExtension */
  public void foo(int i, int j) {
    String random = "@InterfacesIntroduceExtension";
  }

  public int bar(String str) {
    return 1;
  }

  @com.test.AnotherAnnotation
  public String bar(int i) {
    return "bar";
  }
}
