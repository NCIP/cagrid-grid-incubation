package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceHome;
import org.springframework.context.ApplicationContext;

/**
 * This class holds on to information at runtime about deployed services.
 * Probably should be more sophisticated with this and make better 
 * use of JNDI.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class Services {
  private static Map<String, ServiceInformation> services = new HashMap<String, ServiceInformation>();
  
  public static void registerService(String service) {
    ResourceHome resourceHome = null;
    String servicePath = getServicePath();
    String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/home";
    try {
      javax.naming.Context initialContext = new InitialContext();
      resourceHome = (ResourceHome) initialContext.lookup(jndiName);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to instantiate resource home. : home", e);
    }
    ServiceInformation info = new ServiceInformation();
    info.resourceHome = resourceHome;
    info.servicePath = servicePath;
    services.put(service, info);
  }
  
  private static String getServicePath() {
    try {
      MessageContext ctx = MessageContext.getCurrentContext();
      String servicePath = ctx.getTargetService();
      return servicePath;
    } catch(Exception e) {
      throw new IllegalStateException("Unable to obtain current message context.");
    }
  }
  
  public static Collection<String> getServices() {
    return Collections.unmodifiableCollection(services.keySet());
  }
  
  private static ServiceInformation getServiceInformation() {
    String servicePath = getServicePath();
    ServiceInformation info = null;
    for(ServiceInformation curInfo : services.values()) {
      if(curInfo.servicePath.equals(servicePath)) {
        info = curInfo;
        break;
      }
    }
    return info;
  }
  
  public static ApplicationContext getApplicationContext() {
    return getServiceInformation().applicationContext;
  }
  
  public static void setApplicationContext(ApplicationContext applicationContext) {
    getServiceInformation().applicationContext = applicationContext;
  }
  
  static String getServiceName(String service) {
    String implClassName = service.substring(service.lastIndexOf(".")+1);
    return implClassName.substring(0, implClassName.length() - "Impl".length());
  }
  
  static String getServicePackage(String service) {
    return service.substring(0, service.lastIndexOf(".")-1);
  }
  
  static ResourceHome getResourceHome(String serviceName) {
    String service = getService(serviceName);
    return services.get(service).resourceHome;
  }
  
  static String getService(String serviceName) {
    for(String service : services.keySet()) {
      if(getServiceName(service).equals(serviceName)) {
        return service;
      }
    }
    throw new IllegalArgumentException("No registered service with name " + serviceName);
  }
  
  private static class ServiceInformation {
    ResourceHome resourceHome = null;
    ApplicationContext applicationContext = null;
    String servicePath = null;
  }
}
