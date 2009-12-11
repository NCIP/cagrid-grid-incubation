package calc;

public class Calculator implements Adder, Subtracter {
  public int add(int x, int y, String ticket, byte[] bytes) { 
    return x + y;
  }

  public int add3(int x, int y, int z, String ticket, byte[] bytes) {
    return x + y + z;
  }
  
  public int subtract(int x, int y, String ticket) {
    return x - y;
  }

  public int addArray(int[] xs, String ticket, byte[] bytes) {
    int sum = 0;
    for(int x : xs) sum += x;
    return sum;
  } 
}
