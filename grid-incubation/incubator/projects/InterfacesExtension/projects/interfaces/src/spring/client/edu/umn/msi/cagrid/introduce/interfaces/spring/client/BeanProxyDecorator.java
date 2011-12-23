package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.generateBeanName;
import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

abstract public class BeanProxyDecorator implements BeanDefinitionDecorator {
  private static final Logger logger = Logger.getLogger(BeanProxyDecorator.class);
  
  abstract protected Class<? extends BeanProxyMethodReplacer> getReplacingClass();
  
  abstract protected Map<String, ?> getExtraProperties(Node source);
  
  public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder holder, ParserContext parserContext) {
    AbstractBeanDefinition definition = ((AbstractBeanDefinition) holder.getBeanDefinition());
    BeanProxyInfo beanProxyInfo = parseNode(source);
    String replacerName = createMethodReplacer(beanProxyInfo, parserContext, source);
    String getterMethodName = beanProxyInfo.getBeanGetterMethodName();
    String setterMethodName = beanProxyInfo.getBeanSetterMethodName();
    ReplaceOverride override;
    if(getterMethodName != null) {
      logger.debug("Adding method replacer for getter method with name " + getterMethodName);
      override = new ReplaceOverride(getterMethodName, replacerName);
      definition.getMethodOverrides().addOverride(override);
    }
    if(setterMethodName != null) {
      logger.debug("Adding method replacer for setter method with name " + setterMethodName);
      override = new ReplaceOverride(setterMethodName, replacerName);
      definition.getMethodOverrides().addOverride(override);
    }
    return holder;
  }

  /**
   *
   */
  protected String createMethodReplacer(BeanProxyInfo resourceInfo, ParserContext context, Node source) {
    BeanDefinitionRegistry registry = context.getRegistry();
    AbstractBeanDefinition methodReplacerDefinition;
    try {
      String className = getReplacingClass().getCanonicalName();
      methodReplacerDefinition =  BeanDefinitionReaderUtils.createBeanDefinition(null, className, null);
    } catch (ClassNotFoundException e) {
      // Should never happen, that class will be in the same jar as this piece of code!
      throw new IllegalStateException("Cannot find implementing BeanProxyMethodReplacer class.");
    }
    methodReplacerDefinition.getPropertyValues().addPropertyValue("beanProxyInfo", resourceInfo);
    Map<String, ?> extraProperties = getExtraProperties(source);
    for(Entry<String,?> entry : extraProperties.entrySet()) {
      methodReplacerDefinition.getPropertyValues().addPropertyValue(entry.getKey(), entry.getValue());
    }
    String name = generateBeanName(methodReplacerDefinition, registry);
    BeanDefinitionHolder holder = new BeanDefinitionHolder(methodReplacerDefinition, name);
    registerBeanDefinition(holder, registry);
    return name;
  }

  /**
   * Creates ResourceInfo describing given XML node.
   */
  static BeanProxyInfo parseNode(Node source) {
    BeanProxyInfo info = new BeanProxyInfo();
    info.setBean(getAttributeValue(source, "bean-property"));
    info.setGetter(getAttributeValue(source, "get-method"));
    info.setSetter(getAttributeValue(source, "set-method"));
    return info;
  }
  
  
  /**
   * 
   * @param node
   * @param attributeName
   * @return Value of attribute of null if not such attribute exists.
   */
  protected static String getAttributeValue(Node node, String attributeName) {
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
