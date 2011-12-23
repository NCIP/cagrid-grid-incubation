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