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
