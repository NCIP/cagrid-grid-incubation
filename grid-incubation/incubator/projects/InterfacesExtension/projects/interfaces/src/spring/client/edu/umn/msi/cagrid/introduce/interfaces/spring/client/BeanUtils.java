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
package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

public class BeanUtils {

  static String getSetterName(String propertyName) {
      return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

  static String getGetterName(String propertyName) {
      return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

}
