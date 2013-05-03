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
package org.cagrid.introduce.service5.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class Service5Impl extends Service5ImplBase {

	public Service5Impl() throws RemoteException {
		super();
	}

	/* Check whether the string received has as many characters as the corresponding attribute indicates */
  public boolean checkStringAndItsLength(org.cagrid.workflow.service1.types.StringAndItsLenght stringAndItsLenght) throws RemoteException {
		
	  System.out.println("[checkStringAndItsLength] Invoked");
	  
		return (stringAndItsLenght.getStr().length() == stringAndItsLenght.getLength());
	}

  public boolean secureCheckStringAndItsLength(org.cagrid.workflow.service1.types.StringAndItsLenght stringAndItsLenght) throws RemoteException {
    
	  System.out.println("[secureCheckStringAndItsLength] Delegating task to checkStringAndItsLength");
	  return this.checkStringAndItsLength(stringAndItsLenght);
  }

}

