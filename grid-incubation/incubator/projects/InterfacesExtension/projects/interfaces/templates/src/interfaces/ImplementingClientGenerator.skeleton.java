package edu.umn.msi.cagrid.introduce.interfaces;

import java.util.Iterator;

import edu.umn.msi.cagrid.introduce.interfaces.configuration.InterfaceConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.ServiceConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.MethodConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.services.Service;


public class CLASS {
  private Service service;
  private ServiceConfiguration serviceConfiguration;
  private boolean usedRemoteRuntimeException = false;
  
  
  public String getPackage() {
    return service.getClientPackageName();
  }
  
  public Service getService() {
    return service;
  }
  
  public void setService(Service service) {
    this.service = service;
  }
  
  public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
    this.serviceConfiguration = serviceConfiguration;
  }
  
  /**
   * @return Fully qualified interfaces seperated by commas.
   */
  public String getInterfacesString() {
    StringBuffer buffer = new StringBuffer();
    boolean first = true;
    for(InterfaceConfiguration interfaceConfiguration : serviceConfiguration.getInterfaces()) {
      if(first) {
        first = false;
        buffer.append("implements ");
      } else {
        buffer.append(",");
      }
      buffer.append(interfaceConfiguration.getName());
    }
    return buffer.toString();
  }
  
  public String getDelegatedCallStatement(MethodConfiguration methodConfiguration) {
    StringBuffer statement = new StringBuffer();
    if(!methodConfiguration.getMethod().getReturnType().equals(Void.TYPE)) {
      statement.append("return ");
    }
    statement.append("caGridClient.");
    if(methodConfiguration.getServiceMethodName() == null || methodConfiguration.getServiceMethodName().equals("")) {
      statement.append(methodConfiguration.getName());
    } else {
      statement.append(methodConfiguration.getServiceMethodName());
    }
    statement.append("(" + methodConfiguration.getDelegatedCallArguments() + ");");
    return statement.toString();
  }
  
  public String getClientClassName() {
    return service.getName() + Constants.IMPLEMENTING_CLIENT_POSTFIX;
  }
  
  public String getCaGridClientClassName() {
    return service.getName() + "Client";
  }
  
  public Iterator<MethodConfiguration> getAllMethods() {
    return serviceConfiguration.getMethods().iterator();
  }
 
  public void usedRemoteRuntimeException() {
    this.usedRemoteRuntimeException = true;
  }
  
  public boolean getUsedRemoteRuntimeException() {
    return usedRemoteRuntimeException;
  }
  
  public String generate() {
    return "";
  }
}