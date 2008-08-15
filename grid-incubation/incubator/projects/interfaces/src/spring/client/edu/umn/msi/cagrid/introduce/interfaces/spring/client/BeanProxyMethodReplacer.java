package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import org.springframework.beans.factory.support.MethodReplacer;

public interface BeanProxyMethodReplacer extends MethodReplacer {
  public void setBeanProxyInfo(BeanProxyInfo beanProxyInfo);
}
