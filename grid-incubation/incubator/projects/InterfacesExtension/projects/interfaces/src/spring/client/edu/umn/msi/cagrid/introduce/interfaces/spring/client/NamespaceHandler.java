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
package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.config.BeanDefinition;

import org.w3c.dom.Element;

/**
 * A namespace handler is required when using custom
 * namespaces with Spring. Currently there are two elements
 * the Spring SAX parser might encounter, implements-for-service
 * and resource-property. Spring creates an instance of this
 * class and calls the init method to register listeners for
 * these XML elements.
 *
 * This class essentially ignores the implements-for-service
 * commands as they are processed at compile time. At some
 * later time additonal processing may be added to allow for
 * nested annonymous beans for instance.
 *
 * This class registers a ResourcePropertyDecorator as the
 * BeanDefinitionDecorator responsible for resource-property
 * tags. 
 *
 * @author John Chilton (chilton at msi dot umn dot edu)
 */
public class NamespaceHandler extends NamespaceHandlerSupport {
  private class NullParser implements BeanDefinitionParser {
    public BeanDefinition parse(Element element, ParserContext context) {
      return null;
    }
  }

  /** 
   * Called by Spring responsible for registering listeners for 
   * elements in the XML namespace that may be encountered.
   */
  public void init() {
    // implements-for-service tags are detected and processed at 
    // compile time, so just pass them to a null parser.
    //System.out.println("In init of spring extension namespace handler");
    registerBeanDefinitionParser("implements-for-service", new NullParser());
    registerBeanDefinitionDecorator("resource-property", new ResourcePropertyDecorator());
    registerBeanDefinitionDecorator("metadata", new MetadataDecorator());
  }
}
