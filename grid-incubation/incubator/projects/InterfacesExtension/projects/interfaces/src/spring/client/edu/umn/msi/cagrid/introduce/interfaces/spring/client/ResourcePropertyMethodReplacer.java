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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.springframework.beans.factory.support.MethodReplacer;

public class ResourcePropertyMethodReplacer extends BeanProxyMethodReplacerImpl {
  private static final Logger logger = Logger.getLogger(ResourcePropertyMethodReplacer.class);
  private String resource;
  
  public void setResource(String resource) {
    this.resource = resource;
  }
  /*
  protected Object call(String beanMethodName, Object[] args) {
    //String resourcePropertyMethodName = methodNameMap.get(beanMethodName);
    String resourcePropertyMethodName = null;
    if(args == null || args.length == 0) {
      resourcePropertyMethodName = BeanUtils.getGetterName(resource);
    } else {
      resourcePropertyMethodName = BeanUtils.getSetterName(resource);
    }
    if(resourcePropertyMethodName == null) {
      throw new UnsupportedOperationException("Cannot find registered resource property for method " + beanMethodName);
    }
    Resource resource;
    try {
      resource = ResourceContext.getResourceContext().getResource();
    } catch (ResourceContextException e) {
      throw new IllegalStateException(e);
    } catch (ResourceException e) {
      throw new IllegalStateException(e);
    }
    Method resourcePropertyMethod;

    try {
      if(args == null || args.length == 0) {
        resourcePropertyMethod = resource.getClass().getMethod(resourcePropertyMethodName);
      } else {
        resourcePropertyMethod = resource.getClass().getMethod(resourcePropertyMethodName, args[0].getClass());
      }
    } catch(NoSuchMethodException e) {
      throw new IllegalStateException("Mapped method does not exist",e);
    }

    try {
      if(args == null || args.length == 0) {
        return resourcePropertyMethod.invoke(resource);
      } else {
        resourcePropertyMethod.invoke(resource, args[0]);
        return null;
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      // Shouldn't really happen, setters and getter don't throw any checked exceptions
      throw new IllegalStateException(e);
    }
  }  
  */

  @Override
  String getTargetGetterMethodName() {
    return BeanUtils.getGetterName(resource);
  }

  @Override
  protected Object getTargetObject() {
    try {
      return ResourceContext.getResourceContext().getResource();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to obtain resource", e);
    }
  }

  @Override
  String getTargetSetterMethodName() {
    return BeanUtils.getSetterName(resource);
  }
}
