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
		
		return (stringAndItsLenght.getStr().length() == stringAndItsLenght.getLength());
	}

}

