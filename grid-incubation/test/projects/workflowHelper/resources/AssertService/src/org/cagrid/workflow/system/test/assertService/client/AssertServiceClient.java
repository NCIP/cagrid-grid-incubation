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
package org.cagrid.workflow.system.test.assertService.client;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;

import org.globus.gsi.GlobusCredential;

import org.cagrid.workflow.system.test.assertService.stubs.AssertServicePortType;
import org.cagrid.workflow.system.test.assertService.stubs.service.AssertServiceAddressingLocator;
import org.cagrid.workflow.system.test.assertService.common.AssertServiceI;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class AssertServiceClient extends AssertServiceClientBase implements AssertServiceI {	

	public AssertServiceClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public AssertServiceClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public AssertServiceClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public AssertServiceClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(AssertServiceClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  AssertServiceClient client = new AssertServiceClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public boolean assertComplexArrayEquals(systemtests.ComplexType[] complexArray1,systemtests.ComplexType[] complexArray2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"assertComplexArrayEquals");
    org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequest params = new org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequest();
    org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequestComplexArray1 complexArray1Container = new org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequestComplexArray1();
    complexArray1Container.setComplexType(complexArray1);
    params.setComplexArray1(complexArray1Container);
    org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequestComplexArray2 complexArray2Container = new org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsRequestComplexArray2();
    complexArray2Container.setComplexType(complexArray2);
    params.setComplexArray2(complexArray2Container);
    org.cagrid.workflow.system.test.assertService.stubs.AssertComplexArrayEqualsResponse boxedResult = portType.assertComplexArrayEquals(params);
    return boxedResult.isResponse();
    }
  }

  public boolean assertSimpleArrayEquals(java.lang.String[] stringArray1,java.lang.String[] stringArray2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"assertSimpleArrayEquals");
    org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsRequest params = new org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsRequest();
    params.setStringArray1(stringArray1);
    params.setStringArray2(stringArray2);
    org.cagrid.workflow.system.test.assertService.stubs.AssertSimpleArrayEqualsResponse boxedResult = portType.assertSimpleArrayEquals(params);
    return boxedResult.isResponse();
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

  public boolean assertEquals(java.lang.String string1,java.lang.String string2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"assertEquals");
    org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsRequest params = new org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsRequest();
    params.setString1(string1);
    params.setString2(string2);
    org.cagrid.workflow.system.test.assertService.stubs.AssertEqualsResponse boxedResult = portType.assertEquals(params);
    return boxedResult.isResponse();
    }
  }

  public boolean assertNumbersEqual(long number1,long number2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"assertNumbersEqual");
    org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualRequest params = new org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualRequest();
    params.setNumber1(number1);
    params.setNumber2(number2);
    org.cagrid.workflow.system.test.assertService.stubs.AssertNumbersEqualResponse boxedResult = portType.assertNumbersEqual(params);
    return boxedResult.isResponse();
    }
  }

  public boolean secureAssertEquals(java.lang.String string1,java.lang.String string2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"secureAssertEquals");
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsRequest params = new org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsRequest();
    params.setString1(string1);
    params.setString2(string2);
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertEqualsResponse boxedResult = portType.secureAssertEquals(params);
    return boxedResult.isResponse();
    }
  }

  public boolean secureAssertNumberEquals(long number1,long number2) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"secureAssertNumberEquals");
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsRequest params = new org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsRequest();
    params.setNumber1(number1);
    params.setNumber2(number2);
    org.cagrid.workflow.system.test.assertService.stubs.SecureAssertNumberEqualsResponse boxedResult = portType.secureAssertNumberEquals(params);
    return boxedResult.isResponse();
    }
  }

}
