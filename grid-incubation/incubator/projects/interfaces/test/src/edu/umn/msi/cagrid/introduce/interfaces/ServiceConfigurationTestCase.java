package edu.umn.msi.cagrid.introduce.interfaces;

import java.util.Iterator;

import junit.framework.TestCase;

import edu.umn.msi.cagrid.introduce.interfaces.configuration.ConfigurationFactory;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.ServiceConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.FieldConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.MethodConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.ParameterConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.InterfaceConfiguration;

public class ServiceConfigurationTestCase extends TestCase {
  ConfigurationFactory factory = new ConfigurationFactory();
  
  public void test() {
    String source;
    
    source = SourceProvider.getHello1TestContents();
    testCreate(source);
    
    source = SourceProvider.getHello2TestContents();
    testCreate(source);
    
    
    source = SourceProvider.getHello3TestContents();
    testCreate(source);
    
    source = SourceProvider.getHello4TestContents();
    testCreate(source);
    
    source = SourceProvider.getGenericTestContents();
    testGeneric(source);
  }

  
  private void testGeneric(String source) {
    ServiceConfiguration config = factory.getServiceConfiguration(source, null);
    assertEquals(1, config.getFields().size());
    FieldConfiguration fieldConfig = config.getFields().iterator().next();
    assertEquals(2, fieldConfig.getMethods().size());
    Iterator<MethodConfiguration> methodConfigIter = fieldConfig.getMethods().iterator();
    while(methodConfigIter.hasNext()) {
      MethodConfiguration methodConfig = methodConfigIter.next();
      if(methodConfig.getMethod().getName().equals("get")) {
        assertEquals(methodConfig.getParameters().get(0).getJavaType(), String.class);
        assertEquals(methodConfig.getResult().getJavaType(),String.class);
      } else {
        assertEquals(methodConfig.getParameters().get(1).getJavaType(), String.class);
        assertEquals(methodConfig.getParameters().get(0).getJavaType(), Integer.class);
        assertEquals(methodConfig.getResult().getJavaType(),Integer.class);
      }
    }
  }
  
  private void testCreate(String source) {
    ServiceConfiguration config = factory.getServiceConfiguration(source, null);
    assertEquals(2, config.getFields().size());
    for(FieldConfiguration fieldConfiguration : config.getFields()) {
      if(fieldConfiguration.getName().equals("subtracter")) {
        assertEquals(1, fieldConfiguration.getInterfaces().size());
        assertEquals(2, fieldConfiguration.getMethods().size());
        assertEquals(2, fieldConfiguration.getInterfaces().iterator().next().getMethods().size());
        testInterface1Create(fieldConfiguration.getInterfaces().iterator().next());
      } else {
        assertEquals("adder", fieldConfiguration.getName());
        assertEquals(2, fieldConfiguration.getInterfaces().size());
        assertEquals(4, fieldConfiguration.getMethods().size());
      }
    }
  }
  
  private void testInterface1Create(InterfaceConfiguration config) {
    for(MethodConfiguration methodConfiguration : config.getMethods()) {
      if(methodConfiguration.getMethod().getName().equals("foo")) {
        assertEquals(0, methodConfiguration.getParameters().size());
        assertEquals("int", methodConfiguration.getResult().getJavaType().getCanonicalName());
      } else {
        assertEquals("bar", methodConfiguration.getMethod().getName());
        assertEquals(2, methodConfiguration.getParameters().size());
        assertEquals("java.lang.String", methodConfiguration.getResult().getJavaType().getCanonicalName());
        for(ParameterConfiguration paramConfig : methodConfiguration.getParameters()) {
          assertEquals("int", paramConfig.getJavaType().getCanonicalName());
        }
      }
    
    }
  }
  
}
