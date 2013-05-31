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

	  System.out.println("[capitalize] Invoked");
		return uncapitalized.toUpperCase();
	}

  public java.lang.String secureCapitalize(java.lang.String uncapitalized) throws RemoteException {
    
	  System.out.println("[secureCapitalize] Delegating task to capitalize");
	  return this.capitalize(uncapitalized);
  }

}

