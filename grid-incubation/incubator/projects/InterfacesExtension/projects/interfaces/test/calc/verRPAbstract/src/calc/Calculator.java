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

