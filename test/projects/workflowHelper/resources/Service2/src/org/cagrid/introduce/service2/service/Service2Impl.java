package org.cagrid.introduce.service2.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class Service2Impl extends Service2ImplBase {

	public Service2Impl() throws RemoteException {
		super();
	}

	/* Change all characters in the string to upper case */
  public java.lang.String capitalize(java.lang.String uncapitalized) throws RemoteException {

		return uncapitalized.toUpperCase();
	}

}

