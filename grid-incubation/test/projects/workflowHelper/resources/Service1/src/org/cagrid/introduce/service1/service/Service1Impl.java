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
package org.cagrid.introduce.service1.service;

import java.rmi.RemoteException;

import org.cagrid.worklfow.service1.types.StringAndItsLenght;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class Service1Impl extends Service1ImplBase {

	public Service1Impl() throws RemoteException {
		super();
	}

  public org.cagrid.worklfow.service1.types.StringAndItsLenght generateData(java.lang.String info) throws RemoteException {

	  System.out.println("[generateData] Invoked");
		return new StringAndItsLenght(info.length(), info);
	}

  public org.cagrid.worklfow.service1.types.StringAndItsLenght secureGenerateData(java.lang.String info) throws RemoteException {
    
	  System.out.println("[secureGenerateData] Delegating task to generateData");
	  return this.generateData(info);
  }

}

