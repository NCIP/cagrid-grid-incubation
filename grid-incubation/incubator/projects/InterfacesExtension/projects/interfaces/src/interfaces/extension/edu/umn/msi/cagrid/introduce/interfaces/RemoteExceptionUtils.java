package edu.umn.msi.cagrid.introduce.interfaces;

import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

/**
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class RemoteExceptionUtils {
  
  public static boolean serviceMethodRequiresExceptionWrapper(Method method, MethodTypeExceptions faults) {
    for(Class<?> exceptionClass : method.getExceptionTypes()) {
      if(!RemoteException.class.isAssignableFrom(exceptionClass)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 
   * 
   * @param method
   * @return True if the method can throw a RemoteException and 
   * false otherwise.
   */
  public static boolean canThrowRemoteException(Method method) {
    for(Class<?> exceptionClass : method.getExceptionTypes()) {
      if(exceptionClass.isAssignableFrom(RemoteException.class)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean throwsRemoteException(Method method) {
    for(Class<?> exceptionClass : method.getExceptionTypes()) {
      if(RemoteException.class.isAssignableFrom(exceptionClass)) {
        return true;
      }
    }
    return false;
  }  
}
