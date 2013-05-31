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
package edu.umn.msi.cagrid.introduce.interfaces;

import java.io.InputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringBeanConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringConfigurationFactory;

public class SpringConfigurationTestCase extends TestCase {
  
  public void testParseAndGoodValidation() {
    InputStream stream = getClass().getResourceAsStream("sampleApplicationConfigParse.xml");
    SpringConfiguration config = SpringConfigurationFactory.get(stream);
    config.validate();
  }
  
  public void testGetServiceByName() {
    SpringConfiguration config = new SpringConfiguration();
    SpringBeanConfiguration bean1 = new SpringBeanConfiguration();
    SpringBeanConfiguration bean2 = new SpringBeanConfiguration();
    bean1.setServiceName("Test");
    bean2.setServiceName("Test");
    config.addBean(bean1);
    config.addBean(bean2);
    Iterator<SpringBeanConfiguration> beans = config.getBeansForService("Test");
    assertTrue(beans.hasNext());
    beans.next();
    assertTrue(beans.hasNext());
    beans.next();
    assertFalse(beans.hasNext());
    assertFalse(config.getBeansForService("NoSuchService").hasNext());
  }
  
  public void testBadValidation() {
   
    boolean exceptionThrown = false;
    SpringConfiguration config = new SpringConfiguration();
    SpringBeanConfiguration bean = new SpringBeanConfiguration();
    bean.setBeanId(null);
    config.addBean(bean);
    try {
      config.validate();
    } catch(Exception e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);

    exceptionThrown = false;
    config = new SpringConfiguration();
    bean = new SpringBeanConfiguration();
    bean.setBeanId("hello");
    bean.getInterfaces().add("good");
    bean.getInterfaces().add(null);
    config.addBean(bean);
    try {
      config.validate();
    } catch(Exception e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);    
  }
  
}
