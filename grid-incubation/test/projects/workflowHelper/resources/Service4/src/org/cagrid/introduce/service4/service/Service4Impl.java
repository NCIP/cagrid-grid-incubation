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
package org.cagrid.introduce.service4.service;

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
public class Service4Impl extends Service4ImplBase {

	private static File logfile;
	private static PrintStream logstream;
	static {
		logfile = new File("ReceiveArrayService.log");
		try {
			logstream = new PrintStream(logfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Service4Impl() throws RemoteException {
		super();
	}

	/* Concatenate the two string and create an output string with one string by line */
  public java.lang.String printResults(java.lang.String result1,java.lang.String result2) throws RemoteException {

		String result = "[printResults] Received: \n" +"----------------------------------------------\n"+result1 + '\n' + result2
		+"\n----------------------------------------------\n"; 
		logstream.println(result);
		logstream.flush();
		System.out.println(result);
		System.out.flush();
		return result;
	}

  public java.lang.String securePrintResults(java.lang.String result1,java.lang.String result2) throws RemoteException {
    
	  System.out.println("[securePrintResults] Delegating task to printResults");
	  return this.printResults(result1, result2);
  }

}

