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

