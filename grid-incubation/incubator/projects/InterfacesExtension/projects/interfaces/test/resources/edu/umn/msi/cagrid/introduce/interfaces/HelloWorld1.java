package gov.nih.nci.cagrid.helloworld.service;


import calc.Adder;
import calc.AdderImpl;
import calc.Subtracter;
import calc.SubtracterImpl;
import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;
import java.rmi.RemoteException;

/**
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 *
 * @created by Introduce Toolkit version 1.1
 *
 */
public class HelloWorldImpl extends HelloWorldImplBase {
    @ImplementsForService(interfaces = {"edu.umn.msi.cagrid.introduce.interfaces.TestInterface"})
    Subtracter subtracter;
    
    @ImplementsForService(interfaces = {"edu.umn.msi.cagrid.introduce.interfaces.TestInterface2","edu.umn.msi.cagrid.introduce.interfaces.TestInterface3"})
    Adder adder;

  public HelloWorldImpl() throws RemoteException {
    super();
  }

}
