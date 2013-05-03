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
package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

public class BeanUtils {

  static String getSetterName(String propertyName) {
      return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

  static String getGetterName(String propertyName) {
      return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

}
