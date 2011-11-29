package org.cagrid.gaards.csm.filters.client;

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

import org.cagrid.gaards.csm.filters.stubs.FilterCreatorPortType;
import org.cagrid.gaards.csm.filters.stubs.service.FilterCreatorServiceAddressingLocator;
import org.cagrid.gaards.csm.filters.common.FilterCreatorI;
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
 * @created by Introduce Toolkit version 1.3
 */
public class FilterCreatorClient extends FilterCreatorClientBase implements FilterCreatorI {	

	public FilterCreatorClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public FilterCreatorClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public FilterCreatorClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public FilterCreatorClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(FilterCreatorClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  FilterCreatorClient client = new FilterCreatorClient(args[1]);
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

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"destroy");
    return portType.destroy(params);
    }
  }

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"setTerminationTime");
    return portType.setTerminationTime(params);
    }
  }

  public java.lang.String[] getClassNames() throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getClassNames");
    org.cagrid.gaards.csm.filters.stubs.GetClassNamesRequest params = new org.cagrid.gaards.csm.filters.stubs.GetClassNamesRequest();
    org.cagrid.gaards.csm.filters.stubs.GetClassNamesResponse boxedResult = portType.getClassNames(params);
    return boxedResult.getResponse();
    }
  }

  public java.lang.String[] getAssociatedClassNames(java.lang.String className) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAssociatedClassNames");
    org.cagrid.gaards.csm.filters.stubs.GetAssociatedClassNamesRequest params = new org.cagrid.gaards.csm.filters.stubs.GetAssociatedClassNamesRequest();
    params.setClassName(className);
    org.cagrid.gaards.csm.filters.stubs.GetAssociatedClassNamesResponse boxedResult = portType.getAssociatedClassNames(params);
    return boxedResult.getResponse();
    }
  }

  public java.lang.String[] getAssociatedAttributes(java.lang.String className) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAssociatedAttributes");
    org.cagrid.gaards.csm.filters.stubs.GetAssociatedAttributesRequest params = new org.cagrid.gaards.csm.filters.stubs.GetAssociatedAttributesRequest();
    params.setClassName(className);
    org.cagrid.gaards.csm.filters.stubs.GetAssociatedAttributesResponse boxedResult = portType.getAssociatedAttributes(params);
    return boxedResult.getResponse();
    }
  }

  public org.cagrid.gaards.csm.bean.FilterClause getFilterClauseBean(java.lang.String startingClass,java.lang.String[] filters,java.lang.String targetClassAttribute,java.lang.String targetClassAlias,java.lang.String targetClassAttributeAlias) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getFilterClauseBean");
    org.cagrid.gaards.csm.filters.stubs.GetFilterClauseBeanRequest params = new org.cagrid.gaards.csm.filters.stubs.GetFilterClauseBeanRequest();
    params.setStartingClass(startingClass);
    params.setFilters(filters);
    params.setTargetClassAttribute(targetClassAttribute);
    params.setTargetClassAlias(targetClassAlias);
    params.setTargetClassAttributeAlias(targetClassAttributeAlias);
    org.cagrid.gaards.csm.filters.stubs.GetFilterClauseBeanResponse boxedResult = portType.getFilterClauseBean(params);
    return boxedResult.getFilterClause();
    }
  }

}