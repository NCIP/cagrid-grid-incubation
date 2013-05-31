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
package edu.umn.msi.cagrid.introduce.interfaces.services;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

/**
 * Introduce has two concepts of a service, captured by the types
 * ServiceInformation and ServiceType. ServiceInformation encapsulates
 * the whole service, i.e. the actual main entry point service and 
 * any contained service contexts. ServiceType captures these individual
 * services.
 * 
 * This class is abstraction around the ServiceType class and
 * is meant to encapsulate the individual Introduce generated 
 * services, i.e. either the main service or one of its Service contexts.
 * 
 * This class contains delegated calls to ServiceTypes as well
 * as convenience methods for getting package names and directories.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class Service {
  private ServiceType service;
  /**
   * Parent CaGridService
   */
  private CaGridService caGridService;

  public Service(CaGridService caGridService, ServiceType service) { 
    this.service = service;
    this.caGridService = caGridService;
  }

  /**
   * @return Java package name of the client code for this service.
   */
  public String getClientPackageName() {
    return getPackageName() + ".client";
  }

  /**
   * @return Java package name of the main server side code for this 
   * service.
   */
  public String getServicePackageName() {
    return getPackageName() + ".service";
  }

  /**
   * @return Java package name of the common code shared by client
   * and server for this service.
   */
  public String getCommonPackageName() {
    return getPackageName() + ".common";
  }

  public File getClientDirectory() {
    return new File(getDirectory(), "client");
  }

  public File getServiceDirectory() {
    return new File(getDirectory(), "service");
  }  

  public File getCommonDirectory() {
    return new File(getDirectory(), "common");
  }



  /**
   * @return Path to base directory for the Java source files
   * corresponding to this service.
   */
  public String getPath() {
    return caGridService.getBaseDirectory() + File.separator + "src" + File.separator + CommonTools.getPackageDir(service) + File.separator;
  }

  /**
   * @return Base directory for the Java source files
   * corresponding to this service.
   */
  public File getDirectory() {
    return new File(getPath());
  }

  /**
   * 
   * @return Path to the ServiceNameImpl.java file).
   */
  public String getServiceImplPath() {
    return getServiceImplFile().getAbsolutePath();
  }

  /**
   * 
   * @return File object corresponding to the 
   * ServiceNameImpl.java file.
   */
  public File getServiceImplFile() {
    return new File(getServiceDirectory(), service.getName() + "Impl.java");
  }

  /**
   * 
   * @return The contents of the ServiceNameImpl.java file.
   * @throws IOException
   */
  public String getServiceImplContents() throws IOException {
    return FileUtils.readFileToString(getServiceImplFile());
  }

  /**
   * Replaces the contents of the ServiceNameImpl.java file.
   * 
   * @param contents
   * @throws IOException
   */
  public void setServiceImplContents(String contents) throws IOException {
    FileUtils.writeStringToFile(getServiceImplFile(), contents);
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getDescription()
   */
  public String getDescription() {
    return service.getDescription();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getMethods()
   */
  public MethodsType getMethods() {
    return service.getMethods();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getName()
   */
  public String getName() {
    return service.getName();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getNamespace()
   */
  public String getNamespace() {
    return service.getNamespace();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getPackageName()
   */
  public String getPackageName() {
    return service.getPackageName();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getResourcePropertiesList()
   */
  public ResourcePropertiesListType getResourcePropertiesList() {
    return service.getResourcePropertiesList();
  }

  /**
   * @return
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#getServiceSecurity()
   */
  public ServiceSecurity getServiceSecurity() {
    return service.getServiceSecurity();
  }

  /**
   * @param description
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setDescription(java.lang.String)
   */
  public void setDescription(String description) {
    service.setDescription(description);
  }

  /**
   * @param methods
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setMethods(gov.nih.nci.cagrid.introduce.beans.method.MethodsType)
   */
  public void setMethods(MethodsType methods) {
    service.setMethods(methods);
  }

  /**
   * @param name
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setName(java.lang.String)
   */
  public void setName(String name) {
    service.setName(name);
  }

  /**
   * @param namespace
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setNamespace(java.lang.String)
   */
  public void setNamespace(String namespace) {
    service.setNamespace(namespace);
  }

  /**
   * @param packageName
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setPackageName(java.lang.String)
   */
  public void setPackageName(String packageName) {
    service.setPackageName(packageName);
  }

  /**
   * @param resourcePropertiesList
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setResourcePropertiesList(gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType)
   */
  public void setResourcePropertiesList(
      ResourcePropertiesListType resourcePropertiesList) {
    service.setResourcePropertiesList(resourcePropertiesList);
  }

  /**
   * @param serviceSecurity
   * @see gov.nih.nci.cagrid.introduce.beans.service.ServiceType#setServiceSecurity(gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity)
   */
  public void setServiceSecurity(ServiceSecurity serviceSecurity) {
    service.setServiceSecurity(serviceSecurity);
  }

  /**
   * @return The underlying ServiceType object corresponding to this
   * service.
   */
  public ServiceType getService() {
    return service;
  }

  /**
   * @return The parent caGridService service.
   */
  public CaGridService getCaGridService() {
    return caGridService;
  }

}
