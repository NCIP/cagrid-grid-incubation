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

public class Calculator implements Adder, Subtracter {
  private Tracker tracker;

  public void setTracker(Tracker tracker) {
    this.tracker = tracker;
  }

  public int add(int x, int y) {
    tracker.setLastOp("Addition");
    return x + y;
  }

  public int add3(int x, int y, int z) {
    tracker.setLastOp("Addition");
    return x + y + z;
  }
  
  public int subtract(int x, int y) {
    tracker.setLastOp("Subtraction");
    return x - y;
  }

}

