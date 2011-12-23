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
    private Subtracter getSubtracter() {
      return null;
    }
        
    /* Whenever subtracter is initialized the parser breaks for next annotation,
       it breaks even more with this comment here. 
     */
    @ImplementsForService(interfaces = {"edu.umn.msi.cagrid.introduce.interfaces.TestInterface2","edu.umn.msi.cagrid.introduce.interfaces.TestInterface3"})
    private Adder adder;
    
    public HelloWorldImpl() throws RemoteException {
      super();
    }
}
