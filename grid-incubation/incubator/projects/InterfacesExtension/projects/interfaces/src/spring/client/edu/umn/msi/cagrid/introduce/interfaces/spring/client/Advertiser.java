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

import gov.nih.nci.cagrid.advertisement.AdvertisementClient;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.mds.aggregator.types.AggregatorConfig;
import org.globus.mds.aggregator.types.AggregatorContent;
import org.globus.mds.aggregator.types.GetMultipleResourcePropertiesPollType;
import org.globus.mds.servicegroup.client.ServiceGroupRegistrationParameters;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.impl.servicegroup.client.ServiceGroupRegistrationClient;
import org.globus.wsrf.utils.AddressingUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

public class Advertiser implements InitializingBean {
  private Iterable<String> indexServices;
  private String containerScheme = null, containerHostname = null;
  private Integer containerPort = null;
  private boolean perform = true;
  
  public void setIndexServices(String indexServices) {
    String[] indexServiceArray = indexServices.split("\\s*,\\s*");
    if(indexServiceArray == null) {
      indexServiceArray = new String[0];
    }
    setIndexServices(Arrays.asList(indexServiceArray));        
  }
  
  private void setIndexServices(Iterable<String> indexServices) {
    this.indexServices = indexServices;
  }

  public void setContainerPort(Integer containerPort) {
    this.containerPort = containerPort;
  }
  
  public void setContainerScheme(String containerScheme) {
    this.containerScheme = containerScheme;
  }
  
  public void setContainerHostname(String containerHostname) {
    this.containerHostname = containerHostname;
  }

  public void setContainerUrl(String containerUrl) {
    if(StringUtils.hasText(containerUrl)) {
      this.containerHostname = ContainerUrlParser.getHostname(containerUrl);
      this.containerPort = ContainerUrlParser.getPort(containerUrl);
      this.containerScheme = ContainerUrlParser.getScheme(containerUrl);
    }
  }
  
  public void setPerform(boolean preform) {
    this.perform = preform;
  }
  
  
  public void afterPropertiesSet() throws Exception {
    if(!perform) {
      return ;
    }
    
    // check to see if there are any resource properties that
    // require registration
    ResourceContext ctx;
    MessageContext msgContext = MessageContext.getCurrentContext();
    if (msgContext == null) {
      return;
    }

    ctx = ResourceContext.getResourceContext(msgContext);
    EndpointReferenceType epr;
    // since this is a singleton, pretty sure we dont't want to
    // register the key (allows multiple instances of same service
    // on successive restarts)
    epr = AddressingUtils.createEndpointReference(ctx, null);
    
    if(containerPort != null && containerPort > 0) {
      epr.getAddress().setPort(containerPort);
    }
   
    if(StringUtils.hasText(containerHostname)) {
      epr.getAddress().setHost(containerHostname);
    }
    if(StringUtils.hasText(containerScheme)) {
      epr.getAddress().setScheme(containerScheme);
    }

    for(String indexServiceUrl : indexServices) {
      String servicePath = msgContext.getTargetService();

      String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/configuration";
      javax.naming.Context initialContext = new InitialContext();
      Object resourceConfiguration = initialContext.lookup(jndiName);
      
      String templateRegistrationPath = (String) invoke("getRegistrationTemplateFile", resourceConfiguration);    
      File registrationFile = new File(ContainerConfig.getBaseDirectory() + File.separator + templateRegistrationPath);

      final ServiceGroupRegistrationParameters params = ServiceGroupRegistrationClient.readParams(registrationFile.getAbsolutePath());
      params.setServiceGroupEPR(new EndpointReferenceType(new Address(indexServiceUrl)));

      // set our service's EPR as the registrant, or use the specified
      // value
      final EndpointReferenceType registrantEpr = params.getRegistrantEPR();
      if (registrantEpr == null) {
        params.setRegistrantEPR(epr);
      }
      if (params != null) {
        final AggregatorContent content = (AggregatorContent) params.getContent();
        final AggregatorConfig config = content.getAggregatorConfig();
        final MessageElement[] elements = config.get_any();
        GetMultipleResourcePropertiesPollType pollType = null;
        try {
          pollType = (GetMultipleResourcePropertiesPollType) ObjectDeserializer.toObject(elements[0], GetMultipleResourcePropertiesPollType.class);
        } catch (DeserializationException e1) {
          throw e1;
        }

        if (pollType != null) {            
          // if there are properties names that need to be registered then
          // register them to the index service
          if (pollType.getResourcePropertyNames()!=null && pollType.getResourcePropertyNames().length != 0) {              
            // perform the registration for this service
            final AdvertisementClient client = new AdvertisementClient(params);
            client.register();
          }
        }
      }
    }        
  }

  private static Object invoke(final String methodName, final Object targetObject, final Object... arguments) throws Exception {
    Method targetMethod = null;
    for (final Method method : targetObject.getClass().getMethods()) {
      if (!method.getName().equals(methodName)) {
        continue;
      }
      final Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length != arguments.length) {
        continue;
      }
      for (int i = 0; i < arguments.length; i++) {
        if (!parameterTypes[i].isAssignableFrom(arguments[i].getClass())) {
          continue;
        }
      }
      targetMethod = method;
      break;
    }
    if (targetMethod == null) {
      throw new IllegalArgumentException("No such method " + methodName);
    }
    return targetMethod.invoke(targetObject, arguments);
  }  
}
