package calc;

import edu.umn.msi.cagrid.introduce.interfaces.client.GridMethod;

public interface Adder {
  public int add(int x, int y);

  @GridMethod(operationName="add3")
  public int add(int x, int y, int z);

  @GridMethod(operationName="addArray")
  public int add(int[] xs);
}