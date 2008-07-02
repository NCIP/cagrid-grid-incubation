package org.cagrid.first.service;

import java.rmi.RemoteException;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class FirstImpl extends FirstImplBase {

	
	public FirstImpl() throws RemoteException {
		super();
	}
	
  public java.lang.String print() throws RemoteException {
	  System.out.println("Service First");
	  String outputMine = "funcionou";
	  return outputMine;
  }

}

