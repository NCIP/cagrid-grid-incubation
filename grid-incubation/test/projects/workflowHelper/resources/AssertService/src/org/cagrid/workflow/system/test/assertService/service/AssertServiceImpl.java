package org.cagrid.workflow.system.test.assertService.service;

import java.rmi.RemoteException;

import systemtests.ComplexType;

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


	public boolean assertComplexArrayEquals(systemtests.ComplexType[] complexArray1,systemtests.ComplexType[] complexArray2) throws RemoteException {


		boolean equals = true;

		if( (complexArray1 != null) && (complexArray2 != null) ){

			if( complexArray1.length == complexArray2.length ){

				for(int i=0; i < complexArray1.length; i++){
					equals &= assertComplexTypeEquals(complexArray1[i], complexArray2[i]);

					if(!equals){
						break;
					}
				}
			}
			else throw new RemoteException("Complex arrays don't match");
		}

		if(equals){
			System.out.println("Complex arrays match");
		}
		
		
		return equals;
	}

	
	private boolean assertComplexTypeEquals(ComplexType complexType,
			ComplexType complexType2) throws RemoteException {

		boolean equals = true;
		if( (complexType != null) && (complexType2 != null)){
			
			equals &= (complexType.getId() == complexType2.getId());
			equals &= (complexType.getMessage().equals(complexType2.getMessage()));
		}
		else throw new RemoteException("Complex types don't match");
			

		return equals;
	}

	public boolean assertSimpleArrayEquals(java.lang.String[] stringArray1,java.lang.String[] stringArray2) throws RemoteException {
		
		boolean equals = true;
		
		if( (stringArray1 != null) && (stringArray2 != null) ){
			
			if( stringArray1.length == stringArray2.length ){
				
				for(int i=0; i < stringArray1.length; i++){

					equals &= stringArray1[i].equals(stringArray2[i]);
					if(!equals)  break;
				}
			}
			else equals = false;
			
		}
		else equals = false;
		
		
		if(equals){
			System.out.println("Simple arrays match");
		}
		else throw new RemoteException("Simple arrays don't match");
		
		
		return equals;
	}

}

