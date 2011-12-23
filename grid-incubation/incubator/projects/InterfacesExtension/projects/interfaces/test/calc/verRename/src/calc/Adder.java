package calc;

import edu.umn.msi.cagrid.introduce.interfaces.client.GridParam;
import edu.umn.msi.cagrid.introduce.interfaces.client.GridMethod;
import edu.umn.msi.cagrid.introduce.interfaces.client.GridResult;

public interface Adder {
  
  @GridMethod(operationName = "add2", description = "Adds two numbers")
  @GridResult(name = "sum", description = "sum of 2 numbers")
  public int add(@GridParam(name="x", description="first number") int x, @GridParam(name="y", description="second number") int y);

  @GridMethod(operationName = "add3",description = "Adds three numbers")
  @GridResult(name = "sum", description = "sum of 3 numbers")
  public int add(int x, int y, @GridParam(name="z", description="thrid number to add") int z);

  @GridMethod(operationName = "addArray", description="Adds array",exclude=false)
  @GridResult(name = "arraySum", description = "sum of 2 numbers")
  public int add(@GridParam(description="array to add up") int[] xs);
    
  @GridMethod(exclude=true)
  public int add(int[][] xss);
}