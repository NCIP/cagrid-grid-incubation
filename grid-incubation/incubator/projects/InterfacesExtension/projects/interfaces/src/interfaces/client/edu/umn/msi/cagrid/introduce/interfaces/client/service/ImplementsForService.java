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
package edu.umn.msi.cagrid.introduce.interfaces.client.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface ImplementsForService {
  /**
   * @return Interfaces the service should be modified to implement at 
   * compile time.
   */
  String[] interfaces(); // Type could be Class[] but that gets Sticky because this is based on source code, would have resolve the class. 
}
