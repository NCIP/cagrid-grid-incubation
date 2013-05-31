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
package calc;

import lastop.LastOp;

public class Calculator implements Adder, Subtracter {
  private Tracker tracker;

  public void setTracker(Tracker tracker) {
    this.tracker = tracker;
  }

  public int add(int x, int y) {
    LastOp op = new LastOp();
    op.setValue("Addition");
    tracker.setLastOp(op);
    return x + y;
  }

  public int add3(int x, int y, int z) {
    LastOp op = new LastOp();
    op.setValue("Addition");
    tracker.setLastOp(op);
    return x + y + z;
  }
  
  public int subtract(int x, int y) {
    LastOp op = new LastOp();
    op.setValue("Subtraction");
    tracker.setLastOp(op);
    return x - y;
  }

}

