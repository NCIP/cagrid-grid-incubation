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

