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
	  
	  return output;
  }

  public org.cagrid.workflow.systemtests.types.ComplexType[] getComplexArray() throws RemoteException {
    
	  System.out.println("[getComplexArray] Invoked");
	  
	  final int array_len = 6;
	  ComplexType[] output = new ComplexType[array_len];
	  for(int i=0; i < output.length; i++){
		  
		  output[i] = new ComplexType(i, "Element "+i);
	  }
	  
	  return output;
  }

}

