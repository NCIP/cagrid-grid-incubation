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

import edu.umn.msi.cagrid.introduce.interfaces.client.GridParam;

public interface Adder {
  public int add(int x, int y, @GridParam(namespaceURI="http://www.w3.org/2001/XMLSchema", localPart="anySimpleType") String ticket, @GridParam(namespaceURI="http://www.w3.org/2001/XMLSchema", localPart="hexBinary") byte[] bytes);
  public int add3(int x, int y, int z, @GridParam(namespaceURI="http://www.w3.org/2001/XMLSchema", localPart="string")String ticket, @GridParam(namespaceURI="http://www.w3.org/2001/XMLSchema", localPart="byte")byte[] bytes);
  public int addArray(int[] xs, String ticket, @GridParam(namespaceURI="http://www.w3.org/2001/XMLSchema", localPart="base64Binary") byte[] bytes);
}
