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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: Consider validating configuration file.
public class SpringConfigurationFactory {
  
  public static SpringConfiguration get(File springConfigFile) {
    try {
      return get(new FileInputStream(springConfigFile));
    } catch(IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public static SpringConfiguration get(InputStream springConfigStream) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(springConfigStream);
      return get(document);
    } catch(Exception e) {
      throw new IllegalStateException("Failed to parse spring file.",e); 
    }
  }
 
  public static SpringConfiguration get(Document document) {
    SpringConfiguration springConfiguration = new SpringConfiguration();
    NodeList nodes = document.getElementsByTagNameNS(Constants.SPRING_XSD_NAMESPACE, "implements-for-service");
    for(int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      SpringBeanConfiguration bean = new SpringBeanConfiguration();
      bean.setServiceName(getAttributeValue(node, "service"));
      bean.setBeanId(getAttributeValue(node, "implementer"));
      NodeList childNodes = node.getChildNodes();
      for(int j = 0; j < childNodes.getLength(); j++) {
        Node childNode = childNodes.item(j);
        if(Constants.SPRING_XSD_NAMESPACE.equals(childNode.getNamespaceURI()) && 
            childNode.getLocalName().equals("field")) {
          bean.setFieldClass(getAttributeValue(childNode, "class"));
          bean.setFieldName(getAttributeValue(childNode, "name"));
        } else if(Constants.SPRING_XSD_NAMESPACE.equals(childNode.getNamespaceURI()) &&
                    childNode.getLocalName().equals("interface")) {
          bean.getInterfaces().add(getAttributeValue(childNode, "name"));
        }
      }
      springConfiguration.addBean(bean);
    }
    return springConfiguration;
  }
  
  
  /**
   * 
   * @param node
   * @param attributeName
   * @return Value of attribute of null if not such attribute exists.
   */
  private static String getAttributeValue(Node node, String attributeName) {
    String value = null;
    if(node.hasAttributes()) {
      Node attributeNode = node.getAttributes().getNamedItem(attributeName);
      if(attributeNode != null) { 
        value = attributeNode.getNodeValue();
      }
    }
    return value;
  }
  
}
