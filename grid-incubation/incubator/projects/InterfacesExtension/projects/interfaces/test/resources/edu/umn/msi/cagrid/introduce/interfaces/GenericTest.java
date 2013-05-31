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

import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;
import java.rmi.RemoteException;

public class GenericTest extends GenericTestBase {

  @ImplementsForService(interfaces={"edu.umn.msi.cagrid.introduce.interfaces.TestInterface4"})
  Object x;
  
  public GenericTest() throws RemoteException {
    super();
  }
}
