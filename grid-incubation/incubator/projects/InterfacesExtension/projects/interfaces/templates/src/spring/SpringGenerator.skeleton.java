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
package edu.umn.msi.cagrid.introduce.interfaces.spring;

import java.util.Iterator;
import java.util.Collection;

public class CLASS {
  // JET chokes when generics are used to specify type of beans here.
  private Iterator beans;	
	private String serviceName;
	
	public void setServiceName(String serviceName) {
	  this.serviceName = serviceName;
	}
	
  public Iterator<SpringBeanConfiguration> getBeans() {
    return beans;
  }
  
  public void setBeans(Iterator<SpringBeanConfiguration> beans) {
    this.beans = beans;
  }
  
  public String getInterfacesString(Collection<String> interfaceNames) {
    boolean first = true;
    StringBuffer interfacesString = new StringBuffer();
    for(String interfaceName : interfaceNames) {
      if(first) {
        first = false;
      } else {
        interfacesString.append(",");
      }
      interfacesString.append('"');
      interfacesString.append(interfaceName);
      interfacesString.append('"');
    }
    return interfacesString.toString();
  }
  
  public String generate() {
    return "";
  } 
}
