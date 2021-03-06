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
package org.cagrid.introduce.createarrayservice.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.rmi.RemoteException;

import org.cagrid.workflow.systemtests.types.ComplexType;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class CreateArrayServiceImpl extends CreateArrayServiceImplBase {

	
	public CreateArrayServiceImpl() throws RemoteException {
		super();
	}
	
  public java.lang.String[] getArray() throws RemoteException {
    
	  System.out.println("[getArray] Invoked");

	  int array_len = 6;
	  String[] output = new String[array_len];
	  for(int i=0; i < array_len; i++){
		  output[i] = "number "+i;
	  }
	  
	  System.out.println("[getComplexArray] Returning object: "+ output);
	  return output;
  }

  public org.cagrid.workflow.systemtests.types.ComplexType[] getComplexArray() throws RemoteException {
    
	  System.out.println("[getComplexArray] Invoked");
	  
	  final int array_len = 6;
	  ComplexType[] output = new ComplexType[array_len];
	  for(int i=0; i < output.length; i++){
		  
		  output[i] = new ComplexType(i, "Element "+i);
	  }
	  
	  System.out.println("[getComplexArray] Returning object: "+ output);
	  return output;
  }

  public java.lang.String[] secureGetArray() throws RemoteException {
    
	  System.out.println("[secureGetArray] Delegating task to getArray");
	  return this.getArray();
  }

  public org.cagrid.workflow.systemtests.types.ComplexType[] secureGetComplexArray() throws RemoteException {
    
	  System.out.println("[secureGetComplexArray] Delegating task to getComplexArray");
	  return this.getComplexArray();
  }

}

