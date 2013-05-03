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
