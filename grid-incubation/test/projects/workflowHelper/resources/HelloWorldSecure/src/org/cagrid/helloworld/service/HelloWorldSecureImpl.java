package org.cagrid.helloworld.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class HelloWorldSecureImpl extends HelloWorldSecureImplBase {

	
	public HelloWorldSecureImpl() throws RemoteException {
		super();
	}
	
  public byte newMethodSecure(byte _byte) throws RemoteException {
	System.out.println("[newMethodSecure] Invoked");
	  System.out.println("BYTE: "+ _byte);
    return _byte;
  }

}

