package org.cagrid.workflow.system.test.assertService.service;

import java.rmi.RemoteException;

import junit.framework.Assert;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class AssertServiceImpl extends AssertServiceImplBase {

	
	public AssertServiceImpl() throws RemoteException {
		super();
	}
	
  public boolean assertEquals(java.lang.String string1,java.lang.String string2) throws RemoteException {
  
  
	  boolean equals = string1.equals(string2);
  
	  if(!equals){
		  throw new RemoteException("Arguments don't match. Arg1 = '"+ string1 +"', Arg2 = '"+ string2 +"'");
	  }
	  
  
	  return equals;
  }

  public boolean assertNumbersEqual(long number1,long number2) throws RemoteException {
   
	  boolean equals = (number1 == number2);
	  
	  if(!equals){
		  throw new RemoteException("Arguments don't match. Arg1 = '"+ number1 +"', Arg2 = '"+ number2 +"'");
	  }
	  
	  return equals;
	  
  }

  public boolean secureAssertEquals(java.lang.String string1,java.lang.String string2) throws RemoteException {
  
	  return assertEquals(string1, string2);
  }

  public boolean secureAssertNumberEquals(long number1,long number2) throws RemoteException {

	  return assertNumbersEqual(number1, number2);
  }

}

