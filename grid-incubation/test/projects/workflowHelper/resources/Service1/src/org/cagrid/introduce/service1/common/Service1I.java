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
package org.cagrid.introduce.service1.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public interface Service1I {

  public org.cagrid.worklfow.service1.types.StringAndItsLenght generateData(java.lang.String info) throws RemoteException ;

  public org.cagrid.worklfow.service1.types.StringAndItsLenght secureGenerateData(java.lang.String info) throws RemoteException ;

}

