package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node; 
 

public class ResourcePropertyDecorator extends BeanProxyDecorator {

  protected Map<String, ?> getExtraProperties(Node source) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("resource", getAttributeValue(source, "name"));
    return properties;
  }

  @Override
  protected Class<? extends BeanProxyMethodReplacer> getReplacingClass() {
    return ResourcePropertyMethodReplacer.class;
  }
  
}
