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

import java.io.FileNotFoundException;

public class Calculator implements Adder, Subtracter {
  public int add(int x, int y) throws FileNotFoundException {
    throw new FileNotFoundException("no file.");
  }

  public int add3(int x, int y, int z) {
    return x + y + z;
  }
  
  public int subtract(int x, int y) {
    return x - y;
  }

  public int subtract3(int x, int y, int z) {
    return x - y - z;
  }
}

