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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

public class MetadataDecorator extends BeanProxyDecorator {

  protected Map<String, ?> getExtraProperties(Node source) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("service", getAttributeValue(source, "service"));
    properties.put("type", getAttributeValue(source, "type"));
    return properties;
  }

  @Override
  protected Class<? extends BeanProxyMethodReplacer> getReplacingClass() {
    return MetadataMethodReplacer.class;
  }

}
