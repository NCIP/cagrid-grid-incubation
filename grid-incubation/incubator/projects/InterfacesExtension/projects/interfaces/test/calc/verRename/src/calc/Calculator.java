package calc;

public class Calculator implements Adder, Subtracter {
  public int add(int x, int y) {
    return x + y;
  }


  public int add(int x, int y, int z) {
    return x + y + z;
  }

  public int add(int[] xs) {
    return 42;
  }
    
  public int add(int[][] xss) {
    return 42*42;
  }
  
  public int subtract(int x, int y) {
    return x - y;
  }
}

