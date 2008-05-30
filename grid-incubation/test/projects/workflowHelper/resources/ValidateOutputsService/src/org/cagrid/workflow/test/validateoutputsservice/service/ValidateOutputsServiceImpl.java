package org.cagrid.workflow.test.validateoutputsservice.service;

import java.rmi.RemoteException;

import org.workflow.systemtests.types.ComplexType;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class ValidateOutputsServiceImpl extends ValidateOutputsServiceImplBase {

	public ValidateOutputsServiceImpl() throws RemoteException {
		super();
	}

  public void validateTestOutput(int test1Param1,org.workflow.systemtests.types.ComplexType[] test1Param2,boolean test1Param3,int test2Param1,java.lang.String[] test2Param2,boolean test2Param3,java.lang.String test3Param1,java.lang.String test3Param2) throws RemoteException {

	  
	  System.out.println("[validateTestOutput] Invoked");
	  
		/* Match the received values agains the expexted ones */

		// Workflow 1: handling complex arrays
		try{

			this.matchWorkflow1(test1Param1, test1Param2, test1Param3);
		} catch (Throwable t){
			t.printStackTrace();
			System.out.println("Workflow1's outputs don't match");
		}

		// Workflow 2: handling simple arrays
		try{

			this.matchWorkflow2(test2Param1, test2Param2, test2Param3);
		} catch (Throwable t){
			t.printStackTrace();
			System.out.println("Workflow2's outputs don't match");
		}

		
		
		// Workflow 3: fan in and fan out 
		try{

			this.matchWorkflow3(test3Param1, test3Param2);
		} catch (Throwable t){
			t.printStackTrace();
			System.out.println("Workflow3's outputs don't match");
		}
		
		
		
		
		

		System.out.println("[validateTestOutput] All outputs match");
		//throw new RemoteException("What happens if I intentionally throw an exception from within the service?"); //DEBUG

		return;
	}

  
  
	private void matchWorkflow3(String test3Param1, String test3Param2) throws RemoteException {
	

		// Match the fan-in/fan-out workflow output
		String expected1 = "GEORGE TEADORO GORDAO QUE FALOU";
		if(!test3Param1.equals(expected1)){
			throw new RemoteException("[validateTestOutput] Value for test3Param1 ("+ test3Param1 
					+") doesn't match the expected ("+ expected1 +")");
		}
		
		String expected2 = new String();
		for(int i=0; i < expected1.length(); i++){
			expected2 += "x";
		}
		if(!test3Param2.equals(expected2)){
			throw new RemoteException("[validateTestOutput] Value for test3Param2 ("+ test3Param2 
					+") doesn't match the expected ("+ expected2 +")");
		}
		System.out.println("[validateTestOutput] Test workflow 3: outputs match");
		
		return;
	}

	private void matchWorkflow2(int test2Param1, String[] test2Param2,
			boolean test2Param3) throws RemoteException {

		if( test2Param1 != 999 ) throw new RemoteException("[validateTestOutput] Value for test2Param1 ("+ test2Param1 +") doesn't match the expected ("+ 999 +")");
		int array2_len = 6;
		if( test2Param2.length != array2_len ) throw new RemoteException("[validateTestOutput] Value for test2Param2.length ("+ test2Param2.length +") doesn't match the expected ("+ array2_len +")");
		for(int i=0; i < array2_len; i++){

			String curr_elem = test2Param2[i];
			if(!curr_elem.equals("number "+i))
				throw new RemoteException("[validateTestOutput] Value for test2Param2["+ i +"] ("+ curr_elem +") doesn't match the expected ("+ "Element "+i +")");
		}
		if( test2Param3 != true )
			throw new RemoteException("[validateTestOutput] Value for test2Param3 ("+ test2Param3 +") doesn't match the expected ("+ true +")");
		System.out.println("[validateTestOutput] Test workflow 2: outputs match");

	}

	private void matchWorkflow1(int test1Param1, ComplexType[] test1Param2,
			boolean test1Param3) throws RemoteException {

		if( test1Param1 != 999 ) throw new RemoteException("[validateTestOutput] Value for test1Param1 ("+ test1Param1 +") doesn't match the expected ("+ 999 +")");
		int array_len = 6;
		if( test1Param2.length != array_len ) throw new RemoteException("[validateTestOutput] Value for test1Param2.length ("+ test1Param2.length +") doesn't match the expected ("+ array_len +")");
		for(int i=0; i < array_len; i++){

			ComplexType curr_elem = test1Param2[i];
			if( curr_elem.getId() != i )
				throw new RemoteException("[validateTestOutput] Value for test1Param2["+ i +"].ID ("+ curr_elem.getId() +") doesn't match the expected ("+ i +")");
			if( !curr_elem.getMessage().equals("Element "+i) )
				throw new RemoteException("[validateTestOutput] Value for test1Param2["+ i +"].Message ("+ curr_elem.getMessage() +") doesn't match the expected ("+ "Element "+i +")");
		}
		if( test1Param3 != true )
			throw new RemoteException("[validateTestOutput] Value for test1Param3 ("+ test1Param3 +") doesn't match the expected ("+ true +")");
		System.out.println("[validateTestOutput] Test workflow 1: outputs match");
	}

  public void secureValidateTestOutput(int test1Param1,org.workflow.systemtests.types.ComplexType[] test1Param2,boolean test1Param3,int test2Param1,java.lang.String[] test2Param2,boolean test2Param3,java.lang.String test3Param1,java.lang.String test3Param2) throws RemoteException {
   
	  System.out.println("[secureValidateTestOutput] Delegating task to validateTestOutput");
	  this.validateTestOutput(test1Param1, test1Param2, test1Param3, test2Param1, test2Param2, test2Param3, test3Param1, test3Param2);
  }

}

