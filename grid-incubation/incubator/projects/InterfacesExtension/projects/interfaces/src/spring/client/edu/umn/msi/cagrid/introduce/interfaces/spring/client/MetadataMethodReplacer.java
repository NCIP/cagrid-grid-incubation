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

import java.lang.reflect.Method;

import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;


public class MetadataMethodReplacer extends BeanProxyMethodReplacerImpl  {
  private String service;
  private String type;
  

  public void setService(String service) {
    this.service = service;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  String getTargetGetterMethodName() {
    return BeanUtils.getGetterName(type);
  }

  @Override
  protected Object getTargetObject() {
    try {
      ResourceHome resourceHome = Services.getResourceHome(service);
      Resource resource = resourceHome.find(null);
      return resource;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to obtain resource", e);
    }
  }

  @Override
  String getTargetSetterMethodName() {
    return BeanUtils.getSetterName(type);
  }


}
