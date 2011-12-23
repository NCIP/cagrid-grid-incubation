package edu.umn.msi.cagrid.introduce.interfaces;

import java.io.File;
import static java.io.File.separator;
import java.io.IOException;

import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;
import edu.umn.msi.cagrid.introduce.interfaces.services.CaGridService;
import edu.umn.msi.cagrid.introduce.interfaces.types.PreCodegenTypeBeanCollectionSupplierImpl;
import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;
import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMappingImpl;

import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/**
 * Entry-point for introduce. Offloads most of the work to other objects
 * but it is responsible for creating the basic objects that will do most of
 * the work TypeMapping, Service, etc. and for exception translation.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class CodegenProcessor implements CodegenExtensionPreProcessor, CodegenExtensionPostProcessor {
  private static final File SKELETON_LIB_DIRECTORY = new File("ext" + separator + "skeleton" + separator + "interfaces" + separator + "lib");
  private static boolean preCodegenCompleted;
  /**
   * Required to implement CodegenExensionPreProcessor, this will run prior to 
   * introduce's codegen routines.
   * 
   * For this particular extension, this method is responsible for adding/removing methods from the introduce
   * data structures to sync introduce with annotated interfaces and adding required imports and libraries.
   */
  public void preCodegen(ServiceExtensionDescriptionType description, ServiceInformation information) throws CodegenExtensionException {
    preCodegenCompleted = false;
    if(Constants.DEBUG) {
      System.out.println("Interfaces preCodegen executed");
    }
    CaGridService caGridService = new CaGridService(information);
    
    try {
      caGridService.addJarsFromDirectory(SKELETON_LIB_DIRECTORY);
    } catch(Exception e) {
      throw new CodegenExtensionException("InterfacesExtension: Failed to add required libraries to caGrid service library directory or eclipse classpath.",e);
    }
    
    caGridService.executeOnEachService(new AddImportsServiceProcessor(ImplementsForService.class));
    
    if(!PreCodegenTypeBeanCollectionSupplierImpl.canConstruct(information)) {
      // Type information is not available, don't attempt to run interface creation
      System.out.println("Interfaces: Type information not available, skipping precodgen.");
      return;
    }
    
    try {
      TypeMapping typeMap = new TypeMappingImpl(new PreCodegenTypeBeanCollectionSupplierImpl(information));
      caGridService.executeOnEachService(new PreCodegenServiceProcessor(typeMap));
    } catch (IOException e) {
      throw new CodegenExtensionException("InterfacesExtension: Failed to create type map.");
    }
    preCodegenCompleted = true;
  }

  /**
   * Required to implement CodegenExtensionPostProcessor, this will run after Introduce's
   * codegen routines. 
   * 
   * For this particular extension, this method is responsible for filling in the bodies and adding
   * annotation to each method that was created in preCodegen.
   * 
   */
  public void postCodegen(ServiceExtensionDescriptionType description, ServiceInformation information) throws CodegenExtensionException {
    if(!preCodegenCompleted) {
      System.out.println("Skipping Interfaces postCodegen, it appears as though preCodgen did not execute");
      // If preCodegen didn't run or failed to execute, there shouldn't
      // be anything to do.
      return;
    }
    if(Constants.DEBUG) {
      System.out.println("Interfaces postCodegen executed");
    }
    CaGridService caGridService = new CaGridService(information);
    caGridService.executeOnEachService(new PostCodegenServiceProcessor());
  }
  
}
