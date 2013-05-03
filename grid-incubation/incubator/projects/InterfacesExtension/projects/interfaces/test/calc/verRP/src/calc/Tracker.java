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

import lastop.LastOp;

public class Tracker {

  public LastOp theLastOperation;
    
  public void setLastOp(LastOp theLastOperation) {
    this.theLastOperation = theLastOperation;
  }

  public LastOp getLastOp() {
    return theLastOperation;
  }

}
