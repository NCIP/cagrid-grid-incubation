package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract public class BeanProxyMethodReplacerImpl implements BeanProxyMethodReplacer {
  private BeanProxyInfo beanProxyInfo;

  public Object reimplement(Object o, Method replacedMethod, Object[] args) {
    return call(replacedMethod.getName(), args);
  }

  abstract protected Object getTargetObject();
  
  abstract String getTargetSetterMethodName();
  
  abstract String getTargetGetterMethodName();
  
  protected Object call(String replaceMethodName, Object[] args) {
    boolean getting = (args == null || args.length == 0);
    String targetMethodName = null;    
    if(getting) {
      targetMethodName = getTargetGetterMethodName();
    } else {
      targetMethodName = getTargetSetterMethodName();
    }
    if(targetMethodName == null) {
      throw new UnsupportedOperationException("Cannot find method to proxy for method " + replaceMethodName);
    }
    Object targetObject = getTargetObject();
    Method targetMethod;
    try {
      if(getting) {
        targetMethod = targetObject.getClass().getMethod(targetMethodName);
      } else {
        targetMethod = targetObject.getClass().getMethod(targetMethodName, args[0].getClass());
      }
    } catch(NoSuchMethodException e) {
      throw new IllegalStateException("Unable to find proxied method " + targetMethodName,e);
    }
    
    try {
      if(getting) {
        return targetMethod.invoke(targetObject);
      } else {
        targetMethod.invoke(targetObject, args[0]);
        return null;
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      // Shouldn't really happen, setters and getter don't throw any checked exceptions
      throw new IllegalStateException(e);
    }
  }
  
  /*
  protected Object call(String beanMethodName, Object[] args) {
    //String resourcePropertyMethodName = methodNameMap.get(beanMethodName);
    String resourcePropertyMethodName = null;
    if(args == null || args.length == 0) {
      resourcePropertyMethodName = resourceInfo.getResourceGetterMethodName();
    } else {
      resourcePropertyMethodName = resourceInfo.getResourceSetterMethodName();
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
  
  public void setBeanProxyInfo(BeanProxyInfo beanProxyInfo) {
    this.beanProxyInfo = beanProxyInfo;

  }


}
