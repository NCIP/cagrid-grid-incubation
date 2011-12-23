package edu.umn.msi.cagrid.introduce.interfaces.spring;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import edu.umn.msi.cagrid.introduce.interfaces.AddImportsServiceProcessor;
import edu.umn.msi.cagrid.introduce.interfaces.services.CaGridService;
import edu.umn.msi.cagrid.introduce.interfaces.spring.ServiceApplicationContextGenerator;
import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringConfigFileGenerator;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;

public class CodegenProcessor implements CodegenExtensionPreProcessor, CodegenExtensionPostProcessor {
  edu.umn.msi.cagrid.introduce.interfaces.CodegenProcessor interfacesProcessor = new edu.umn.msi.cagrid.introduce.interfaces.CodegenProcessor();
  private static final File SKELETON_LIB_DIRECTORY = new File("ext" + File.separator + "skeleton" + File.separator + "spring" + File.separator + "lib");

  public void preCodegen(ServiceExtensionDescriptionType description, ServiceInformation information) throws CodegenExtensionException {
    CaGridService caGridService = new CaGridService(information);
    try {
      initServiceApplicationContextSourceFile(caGridService);
      caGridService.addJarsFromDirectory(SKELETON_LIB_DIRECTORY);
      File applicationContextFile = initApplicationContext(caGridService);
      SpringConfiguration springConfiguration = SpringConfigurationFactory.get(applicationContextFile);
      springConfiguration.addDefaultFieldValues(caGridService.getMainService().getName());
      caGridService.executeOnEachService(new SpringServiceProcessor(caGridService, springConfiguration));
      
      String servicePackage = caGridService.getMainService().getServicePackageName();
      String mainServiceName = caGridService.getMainService().getName();
      String[] imports = new String[]{servicePackage + "." + mainServiceName + "ApplicationContext", servicePackage + "." + mainServiceName + "Configuration"};
      caGridService.executeOnEachService(new AddImportsServiceProcessor(imports));
    } catch(Exception e) {
      throw new CodegenExtensionException(e);
    }
    interfacesProcessor.preCodegen(description, information);
  }

  public void postCodegen(ServiceExtensionDescriptionType description, ServiceInformation information) throws CodegenExtensionException {
    interfacesProcessor.postCodegen(description, information);
  }
  
  /**
   * Initializes the source file corresponding to the class
   * ServiceApplicationContext. This is used as a centralized
   * spot to initialize and reference the Spring context at
   * runtime.
   * 
   * @param caGridService
   * @throws IOException
   */
  private void initServiceApplicationContextSourceFile(CaGridService caGridService) throws IOException {
    File serviceApplicationContextFile = new File(caGridService.getMainService().getServiceDirectory(), caGridService.getMainService().getName() + "ApplicationContext.java");
    ServiceApplicationContextGenerator generator = new ServiceApplicationContextGenerator();
    generator.setCaGridService(caGridService);
    FileUtils.writeStringToFile(serviceApplicationContextFile, generator.generate());    
  }
  
  /**
   * Initializes the actual Spring configuration file (by default 
   * etc/applicationContext.xml) and the Introduce service property
   * that points to it.
   * 
   * @param caGridService
   * @return
   * @throws IOException
   */
  private File initApplicationContext(CaGridService caGridService) throws IOException {
    if(!caGridService.hasServiceProperty("applicationContext")) {
      ServicePropertiesProperty property = new ServicePropertiesProperty();
      property.setKey("applicationContext");
      property.setDescription("Location of Spring application context configuration file.");
      property.setIsFromETC(true);
      property.setValue("applicationContext.xml");
      caGridService.addServiceProperty(property);
    }
    String applicationContextPath = caGridService.getServicePropertyPath("applicationContext");
    File applicationContextFile = new File(applicationContextPath);
    if(!applicationContextFile.exists()) {
      SpringConfigFileGenerator generator = new SpringConfigFileGenerator();
      FileUtils.writeStringToFile(applicationContextFile, generator.generate());
    }    
    return applicationContextFile;
  }  
}
