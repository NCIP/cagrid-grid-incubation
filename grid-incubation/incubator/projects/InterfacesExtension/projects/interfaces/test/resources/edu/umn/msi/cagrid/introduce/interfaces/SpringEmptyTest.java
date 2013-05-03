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
package some.Test;

import some.random.Class1;
import other.random.Class2;
import edu.test.AnnotationTest;

import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;


/**
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 *
 * @created by Introduce Toolkit version 1.1
 * @author Some Developer
 */
public class SpringTestImpl extends SpringTestImplBase {
  /**
   * DO NOT REMOVE THIS COMMENT
   * @SpringIntroduceExtension
   */
   private __initSpring__() throws RemoteException
   {
     ServiceApplicationContext.init(getConfiguration());
   }

  public SpringTestImpl() { 
    super();
    __initSpring__();
    
  }
  
}
