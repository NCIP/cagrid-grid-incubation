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
package edu.umn.msi.cagrid.introduce.interfaces.services;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.property.ServiceProperties;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import org.apache.commons.io.FileUtils;

/**
 * Introduce has two concepts of a service, captured by the types
 * ServiceInformation and ServiceType. ServiceInformation encapsulates
 * the whole service, i.e. the actual main entry point service and 
 * any contained service contexts. ServiceType captures these individual
 * services.
 * 
 * This class is abstraction around the ServiceInformation class and
 * is meant to encapsulate the whole Introduce generated caGrid service.
 * 
 * This class contains delegated calls to ServiceInformation as well
 * as convenience methods for management of service properties and jar
 * files. It also provides a mechanism for fetching the Services objects
 * that correspond to the individual services that the caGrid service
 * is composed of. 
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class CaGridService {
  ServiceInformation serviceInformation;
  
  public CaGridService(ServiceInformation serviceInformation) {
    this.serviceInformation = serviceInformation;
  }
  
  public Collection<Service> getServices() {
    int numServices;
    ServiceType[] serviceTypes = null;
    if(serviceInformation.getServices() == null) {
      numServices = 0;
    } else { 
      serviceTypes =  serviceInformation.getServices().getService();
      numServices = (serviceTypes == null) ? 0 : serviceTypes.length;
    }
    ArrayList<Service> services = new ArrayList<Service>(numServices);
    for(int i = 0; i < numServices; i++) {
      services.add(i, new Service(this, serviceTypes[i]));
    }
    return services;
  }

  public void executeOnEachService(ServiceProcessor serviceProcessor) throws CodegenExtensionException {
    for(Service service : getServices()) {
      try {
        serviceProcessor.execute(service);
      } catch(Exception e) {
        throw new CodegenExtensionException("Processor failed on service " + service.getName(), e);
      }
    }
  }
  
  
  /**
   * @return A Service object corresponding to the main entry
   * point service of the caGrid service, i.e. the one that the
   * other service's are service contexts of.
   */
  public Service getMainService() {
    // Is the main service always the first?
    ServiceType serviceType = serviceInformation.getServices().getService(0);
    return new Service(this, serviceType);
  }
  
  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.common.ServiceInformation#getBaseDirectory()
   */
  public File getBaseDirectory() {
    return serviceInformation.getBaseDirectory();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.common.ServiceInformation#getNamespaces()
   */
  public NamespacesType getNamespaces() {
    return serviceInformation.getNamespaces();
  }
  
  public File getLibDirectory() {
    return new File(getBaseDirectory(), "lib");
  }
  
  /**
   * @return The jar files from the caGrid service's lib directory
   */
  @SuppressWarnings("unchecked")
  public Collection<File> getJarFiles() {
    File libDir = getLibDirectory();
    return FileUtils.listFiles(libDir, new String[]{"jar"}, false);
  }
  
  /**
   * @return The eclipse classpath file
   */
  public File getClasspathFile() {
    return new File(getBaseDirectory().getAbsolutePath() + File.separator + ".classpath");
  }
  
  /**
   * Adds all of the jars from the specified directory
   * to the lib directory of the caGrid service. It also
   * adjusts the eclipse classpath of the caGrid service
   * to reflect any new jars.
   * 
   * @param directory The directory to add jars from
   * @throws Exception
   */
  public void addJarsFromDirectory(File directory) throws Exception {
    Collection<File> existingJars = getJarFiles();
    FileUtils.copyDirectory(directory, getLibDirectory());
    Collection<File> addedJars = getJarFiles();
    addedJars.removeAll(existingJars);
    ExtensionUtilities.syncEclipseClasspath(getClasspathFile(), addedJars.toArray(new File[]{}));
  }
  
  /**
   * @return A ClassLoader for the lib directory
   * of the caGrid service.
   */
  public ClassLoader getClassLoader() {
    Collection<File> jarFiles = getJarFiles();
    ArrayList<URL> jarUrls = new ArrayList<URL>(jarFiles.size());
    for(Object jarFileObject : jarFiles) {
      File jarFile = (File) jarFileObject;
      try {
        // Technique found at http://snippets.dzone.com/posts/show/3574
        jarUrls.add(new URL("jar:file://" + jarFile.getAbsolutePath() + "!/"));
      } catch(Exception e) {
        System.err.println("Failed to create URL for " + jarFile.getAbsoluteFile() + " it will not be searched");
      }
    }
    // Second argument to class loader is parent class loader. This service
    // may have been previously deployed which means an older version of the jar
    // we care about may be on introduces classpath, that is why it seems necessary 
    // to set the parent classpath loader to null.
    return new URLClassLoader(jarUrls.toArray(new URL[]{}), null);
  }

  /**
   * 
   * @param servicePropertyKey 
   * @return The path specified by the supplied service property.
   */
  public String getServicePropertyPath(String servicePropertyKey) {
    ServicePropertiesProperty property = getServiceProperty(servicePropertyKey);
    if(property == null) {
      return null;
    }
    String path = "";
    if(property.getIsFromETC()) {
      path = getBaseDirectory() + File.separator + "etc" + File.separator;
    }
    path = path + property.getValue();
    return path;
  }

  /**
   * Adds a service property to the caGrid service specified 
   * via Introduce's ServicePropertiesProperty class.
   * 
   * @param newProperty
   */
  public void addServiceProperty(ServicePropertiesProperty newProperty) {
    ServicePropertiesProperty[] properties;
    if(serviceInformation.getServiceProperties() != null && serviceInformation.getServiceProperties().getProperty() != null) {
      properties = serviceInformation.getServiceProperties().getProperty();
    } else {
      properties = new ServicePropertiesProperty[]{};
    }
    ServicePropertiesProperty[] newProperties = new ServicePropertiesProperty[properties.length+1];
    for(int i = 0; i< properties.length; i++) {
      newProperties[i] = properties[i];
    }
    newProperties[properties.length] = newProperty;
    ServiceProperties newPropertiesObject = new ServiceProperties();
    newPropertiesObject.setProperty(newProperties);
    serviceInformation.setServiceProperties(newPropertiesObject);
  }
  
  /**
   * Checks if the caGrid service has a service property corresponding
   * to the given key.
   * 
   * @param servicePropertyKey
   * @return
   */
  public boolean hasServiceProperty(String servicePropertyKey) {
    return getServiceProperty(servicePropertyKey) != null;
  }
  
  /**
   * 
   * @param servicePropertyKey
   * @return The ServicePropertiesProperty object corresponding
   * to the given key, or null if the caGrid service has no such 
   * property.
   */
  public ServicePropertiesProperty getServiceProperty(String servicePropertyKey) {
    if(serviceInformation.getServiceProperties() != null && serviceInformation.getServiceProperties().getProperty() != null) {
      for(ServicePropertiesProperty property : serviceInformation.getServiceProperties().getProperty()) {
        if(servicePropertyKey.equals(property.getKey())) {
          return property;
        }
      }
    }
    return null;
  }
  
}
