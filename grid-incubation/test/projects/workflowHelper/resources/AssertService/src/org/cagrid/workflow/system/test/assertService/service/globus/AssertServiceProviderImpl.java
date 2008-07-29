package org.cagrid.workflow.system.test.assertService.service.globus;

import org.cagrid.workflow.system.test.assertService.service.AssertServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the AssertServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class AssertServiceProviderImpl{
	
	AssertServiceImpl impl;
	
	public AssertServiceProviderImpl() throws RemoteException {
		impl = new AssertServiceImpl();
	}
	

    public org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsResponse assertComplexArrayEquals(org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsResponse();
    boxedResult.setResponse(impl.assertComplexArrayEquals(params.getComplexArray1().getComplexType(),params.getComplexArray2().getComplexType()));
    return boxedResult;
  }

    public org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsResponse assertSimpleArrayEquals(org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsResponse();
    boxedResult.setResponse(impl.assertSimpleArrayEquals(params.getStringArray1(),params.getStringArray2()));
    return boxedResult;
  }

    public org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsResponse assertEquals(org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsResponse();
    boxedResult.setResponse(impl.assertEquals(params.getString1(),params.getString2()));
    return boxedResult;
  }

    public org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualResponse assertNumbersEqual(org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualResponse();
    boxedResult.setResponse(impl.assertNumbersEqual(params.getNumber1(),params.getNumber2()));
    return boxedResult;
  }

    public org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsResponse secureAssertEquals(org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsResponse();
    boxedResult.setResponse(impl.secureAssertEquals(params.getString1(),params.getString2()));
    return boxedResult;
  }

    public org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsResponse secureAssertNumberEquals(org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsRequest params) throws RemoteException {
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsResponse boxedResult = new org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsResponse();
    boxedResult.setResponse(impl.secureAssertNumberEquals(params.getNumber1(),params.getNumber2()));
    return boxedResult;
  }

}
