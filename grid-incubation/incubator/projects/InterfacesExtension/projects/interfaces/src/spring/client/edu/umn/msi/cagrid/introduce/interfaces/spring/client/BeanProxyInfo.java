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

public class BeanProxyInfo {
  private String bean;
  private String setter;
  private String getter;

  public String getBeanGetterMethodName() {
    if(getter != null) {
      return getter;
    } else if(bean != null) {
      return BeanUtils.getGetterName(bean);
    } else {
      return null;
    }
  }
 
  public String getBeanSetterMethodName() {
    if(setter != null) {
      return setter;
    } else if(bean != null) {
      return BeanUtils.getSetterName(bean);
    } else {
      return null;
    }
  }

  public String getBean() {
    return bean;
  }

  public void setBean(String bean) {
    this.bean = bean;
  }

  public String getSetter() {
    return setter;
  }

  public void setSetter(String setter) {
    this.setter = setter;
  }

  public String getGetter() {
    return getter;
  }

  public void setGetter(String getter) {
    this.getter = getter;
  }

}
