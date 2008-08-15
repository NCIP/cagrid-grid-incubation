package calc;

import edu.umn.msi.cagrid.introduce.interfaces.client.GridParam;

public interface Adder {
  public int add(int x, int y, org.apache.axis.types.URI uri);
  public int add3(int x, int y, int z);
}