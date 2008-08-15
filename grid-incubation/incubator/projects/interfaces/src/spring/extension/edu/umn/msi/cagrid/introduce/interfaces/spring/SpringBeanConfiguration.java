package edu.umn.msi.cagrid.introduce.interfaces.spring;

import java.util.Collection;
import java.util.LinkedList;

public class SpringBeanConfiguration {
  private String serviceName = "";
  private String beanId = "";
  private String fieldName = "";
  private String fieldClass = "";
  private Collection<String> interfaces = new LinkedList<String>();
  
  public String getServiceName() {
    return serviceName;
  }
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }
  public String getBeanId() {
    return beanId;
  }
  public void setBeanId(String beanId) {
    this.beanId = beanId;
  }
  public String getFieldName() {
    return fieldName;
  }
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  public String getFieldClass() {
    return fieldClass;
  }
  public void setFieldClass(String fieldClass) {
    this.fieldClass = fieldClass;
  }
  public Collection<String> getInterfaces() {
    return interfaces;
  }
  public void setInterfaces(Collection<String> interfaces) {
    this.interfaces = interfaces;
  }
}
