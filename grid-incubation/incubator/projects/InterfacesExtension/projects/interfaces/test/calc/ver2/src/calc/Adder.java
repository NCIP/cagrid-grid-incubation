package calc;

public interface Adder {
  public int add(int x, int y, String ticket);
  public int add3(int x, int y, int z, String ticket);
  public int addArray(int[] xs, String ticket);
}