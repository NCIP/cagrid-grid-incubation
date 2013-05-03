/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package calc;

import java.io.IOException;
import java.rmi.RemoteException;


public interface Subtracter {
  public static class MyIOException extends IOException {
    public MyIOException(String str) {
      super(str);
    }
  }

  public static class MyRemoteException extends RemoteException {
    public MyRemoteException(String str) {
      super(str);
    }
  }
 
  public int subtract(int x, int y) throws MyRemoteException;

  public int subtract3(int x, int y, int z) throws MyIOException;
}
