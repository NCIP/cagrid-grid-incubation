package org.cagrid.second.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class SecondImpl extends SecondImplBase {

	
	public SecondImpl() throws RemoteException {
		super();
	}
	
  public void receive(java.lang.String input) throws RemoteException {
	  System.out.println("Fala doidao: mim ser service 2: input = "+input);
  }

}

