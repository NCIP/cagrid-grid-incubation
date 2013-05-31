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
package edu.umn.msi.cagrid.introduce.interfaces.spring;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import static com.google.common.collect.Iterators.filter;
import com.google.common.base.Predicate;

public class SpringConfiguration {
  private Collection<SpringBeanConfiguration> beans = new LinkedList<SpringBeanConfiguration>();

  public Iterator<SpringBeanConfiguration> getBeans() {
    return beans.iterator();
  }

  public void addBean(SpringBeanConfiguration bean) {
    beans.add(bean);
  }

  public void validate() throws IllegalStateException {
    for(SpringBeanConfiguration bean : beans) {
      if(bean.getBeanId() == null) {
        throw new IllegalStateException("SpringBeanConfiguration does not contain an id.");
      }
      for(String interface_ : bean.getInterfaces()) {
        if(interface_.equals(null)) {
          throw new IllegalStateException("Implements null interface.");
        }
      }
    }
  }

  public Iterator<SpringBeanConfiguration> getBeansForService(final String serviceName) {
    Predicate<SpringBeanConfiguration> predicate = new Predicate<SpringBeanConfiguration>() {
      public boolean apply(SpringBeanConfiguration bean) {
        return serviceName.equals(bean.getServiceName());
      }      
    };
    return filter(beans.iterator(), predicate);
  }

  public Iterator<String> getServiceNames() {
    HashSet<String> serviceNames = new HashSet<String>();
    for(SpringBeanConfiguration bean : beans) {
      serviceNames.add(bean.getServiceName());
    }
    return serviceNames.iterator();
  }

  public void addDefaultFieldValues(String defaultService) {
    int i = 0;
    for(Iterator<SpringBeanConfiguration> beans = getBeans(); beans.hasNext();) {
      SpringBeanConfiguration bean = beans.next();
      if(bean.getServiceName() == null || bean.getServiceName().equals("")) {
        bean.setServiceName(defaultService);
      }
      if(bean.getFieldClass() == null || bean.getFieldClass().equals("")) {
        bean.setFieldClass("java.lang.Object");
      }
      if(bean.getFieldName() == null || bean.getFieldName().equals("")) {
        bean.setFieldName(Constants.DEFAULT_FIELD_PREFIX + ++i);
      }
    }
  }
}
