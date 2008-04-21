package org.cagrid.introduce.service3.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class Service3Impl extends Service3ImplBase {

	public Service3Impl() throws RemoteException {
		super();
	}

	/* Generate a string filled with 'x' and of size 'str_length' */
  public java.lang.String generateX(int str_length) throws RemoteException {

		
		String output = new String();
		for(int i=0; i < str_length; i++){
			
			output = output + 'x';
		}
		
		return output;
	}

}

