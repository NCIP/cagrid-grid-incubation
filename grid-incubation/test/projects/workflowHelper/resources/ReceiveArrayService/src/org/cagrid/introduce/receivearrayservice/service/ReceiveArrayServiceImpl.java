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
package org.cagrid.introduce.receivearrayservice.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class ReceiveArrayServiceImpl extends ReceiveArrayServiceImplBase {

	private static File logfile;
	private static PrintStream logstream;
	static {
		logstream = System.out;
		/*logfile = new File("ReceiveArrayService.log");
		try {
			logstream = new PrintStream(logfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} // */
	}

	public ReceiveArrayServiceImpl() throws RemoteException {
		super();
	}

  public void receiveArray(java.lang.String[] arrayStr) throws RemoteException {

	  System.out.println("[receiveArray] Invoked");
	  System.out.println("[receiveArray] arrayStr = "+ arrayStr);
	  
		for(int i=0 ; i < arrayStr.length; i++){

			logstream.println("Element "+i+" is "+arrayStr[i]);
		}
		logstream.flush();
	}

  public void receiveArrayAndMore(int number,java.lang.String[] strArray,boolean booleanValue) throws RemoteException {

	  System.out.println("[receiveArrayAndMore] Invoked");
	  
		logstream.println("Received integer value is: "+number);
		logstream.println("Received array of strings:");
		this.receiveArray(strArray);
		logstream.println("Received boolean value is: "+booleanValue);
		logstream.flush();
	}

  public void receiveComplexArray(int number,org.cagrid.workflow.systemtests.types.ComplexType[] complexArray,boolean booleanValue) throws RemoteException {

		logstream.println("BEGIN receiveComplexArray");
		logstream.println("Received integer value is: "+number);
		logstream.println("Received array of complex: "+ complexArray);
		for(int i=0 ; i < complexArray.length; i++){

			logstream.println("Element "+i+" is "+complexArray[i].getId()+'#'+complexArray[i].getMessage());

		}
		logstream.println("Received boolean value is: "+booleanValue);
		logstream.flush();
	}
  public void secureReceiveArray(java.lang.String[] arrayStr) throws RemoteException {
    
	  System.out.println("[secureReceiveArray] Delegating task to receiveArray");
	  this.receiveArray(arrayStr);
  }

  public void secureReceiveArrayAndMore(int number,java.lang.String[] strArray,boolean booleanValue) throws RemoteException {
    
	  System.out.println("[secureReceiveArrayAndMore] Delegating task to receiveArrayAndMore");
	  this.receiveArrayAndMore(number, strArray, booleanValue);
  }

  public void secureReceiveComplexArray(int number,org.cagrid.workflow.systemtests.types.ComplexType[] complexArray,boolean booleanValue) throws RemoteException {
    
	  System.out.println("[secureReceiveComplexArray] Delegating task to receiveComplexArray");
	  this.receiveComplexArray(number, complexArray, booleanValue);
  }

}

