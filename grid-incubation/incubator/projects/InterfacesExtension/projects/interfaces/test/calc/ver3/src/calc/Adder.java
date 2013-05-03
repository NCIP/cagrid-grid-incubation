/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package calc;

import edu.umn.msi.cagrid.introduce.interfaces.client.GridMethod;

public interface Adder {
  public int add(int x, int y);

  @GridMethod(operationName="add3")
  public int add(int x, int y, int z);

  @GridMethod(operationName="addArray")
  public int add(int[] xs);
}
