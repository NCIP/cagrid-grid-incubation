package org.cagrid.gaards.csm.filters.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public interface FilterCreatorI {

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException ;

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException ;

  /**
   * returns the available target classes in this application
   *
   * @throws CSMInternalFault
   *	
   */
  public java.lang.String[] getClassNames() throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault ;

  public java.lang.String[] getAssociatedClassNames(java.lang.String className) throws RemoteException ;

  public java.lang.String[] getAssociatedAttributes(java.lang.String className) throws RemoteException ;

  public org.cagrid.gaards.csm.bean.FilterClause getFilterClauseBean(java.lang.String startingClass,java.lang.String[] filters,java.lang.String targetClassAttribute,java.lang.String targetClassAlias,java.lang.String targetClassAttributeAlias) throws RemoteException ;

}

