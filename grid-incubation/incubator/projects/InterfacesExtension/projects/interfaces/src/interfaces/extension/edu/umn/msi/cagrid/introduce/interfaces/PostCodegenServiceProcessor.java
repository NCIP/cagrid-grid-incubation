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
package edu.umn.msi.cagrid.introduce.interfaces;

import static com.google.common.collect.Iterators.filter;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.MethodBody;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.ConfigurationFactory;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.MethodConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.configuration.ServiceConfiguration;
import edu.umn.msi.cagrid.introduce.interfaces.services.Service;
import edu.umn.msi.cagrid.introduce.interfaces.services.ServiceProcessor;
import edu.umn.msi.cagrid.introduce.interfaces.ImplementingClientGenerator;
import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

/**
 * This class is used to mark methods as having been created
 * by the Introduce interface extension, and to remove these 
 * markings.
 * 
 * These methods need to be marked so that consistency is maintained
 * with respect to the interface. Clearly we don't want to delete 
 * every method each save that doesn't appear in an implementing interface,
 * that might result in deleting methods created by the user or another
 * extension. 
 * 
 * But if an interface has a method myFoo(), and later renames it 
 * to foo(), the extension should know that myFoo() was generated 
 * with the extension so that it may be removed.
 */
public class PostCodegenServiceProcessor implements ServiceProcessor {

  public void execute(Service service) throws Exception {
    if(Constants.DEBUG) {
      System.out.println("Interfaces postprocessing service " + service.getName());
    }
    //InterfaceToVariableMap map = InterfaceToVariableMap.build(service);
    StringBuffer contentsBuffer = new StringBuffer(service.getServiceImplContents());
    ServiceConfiguration serviceConfiguration = new ConfigurationFactory().getServiceConfiguration(service);
    serviceConfiguration.addDefaultValues(null);
    Collection<MethodConfiguration> allMethods = serviceConfiguration.getMethods();
    Iterator<MethodConfiguration> methodsToImplement = filter(allMethods.iterator(), MethodConfiguration.getIncludedPredicate());

    ImplementingClientGenerator generator = new ImplementingClientGenerator();
    generator.setServiceConfiguration(serviceConfiguration);
    generator.setService(service);
    File clientFile = new File(service.getClientDirectory(), generator.getClientClassName() + ".java");
    FileUtils.writeStringToFile(clientFile, generator.generate());      

    while(methodsToImplement.hasNext()) {
      MethodConfiguration methodConfiguration = methodsToImplement.next();
      addMethodBody(methodConfiguration.getInterfaceConfiguration().getFieldConfiguration().getName(), methodConfiguration, contentsBuffer);        
      addImplementedTag(methodConfiguration, contentsBuffer);
    }
    service.setServiceImplContents(contentsBuffer.toString());
  }

  /**
   * Adds a Doclet to the given method in the given
   * source file to indicate that it was created by this
   * extension.
   * 
   * @param method Method to tag.
   * @param stringBuffer Contents of the source file.
   */ 
  public static void addImplementedTag(MethodConfiguration methodConfiguration, StringBuffer stringBuffer) {
    String methodSignature = methodConfiguration.buildMethodSignature(false);
    int startOfSignature = SyncHelper.startOfSignature(stringBuffer, methodSignature);
    stringBuffer.insert(startOfSignature, Constants.IMPLEMENTED_JAVA_DOC);
    return;
  }

  public static String identation(int identLevel) {
    StringBuffer identation = new StringBuffer();
    for(int i = 0; i < identLevel; i++) {
      identation.append(Constants.DEFAULT_IDENTATION);
    }
    return identation.toString();
  }

  // TODO: Jet Template perhaps
  public static String getWrappedDelegatedCallStatement(String delegate, MethodConfiguration methodConfiguration) {
    StringBuffer statement = new StringBuffer();
    if(!RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(methodConfiguration.getMethod(), null)) {
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append(methodConfiguration.getDelegatedCallStatement(delegate, true));
      statement.append(Constants.NEWLINE);
    } else {
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("try");
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("{");
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(3));
      statement.append(methodConfiguration.getDelegatedCallStatement(delegate, true));
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("}");
      statement.append(Constants.NEWLINE);
      // If the method throws a RemoteException, no need to wrap it, just throw as is.
      if(RemoteExceptionUtils.throwsRemoteException(methodConfiguration.getMethod())) {
        statement.append(PostCodegenServiceProcessor.identation(2));
        statement.append("catch(RemoteException e)");
        statement.append(Constants.NEWLINE);
        statement.append(PostCodegenServiceProcessor.identation(2));
        statement.append("{");
        statement.append(Constants.NEWLINE);
        statement.append(PostCodegenServiceProcessor.identation(3));
        statement.append("throw e;");
        statement.append(Constants.NEWLINE);
        statement.append(PostCodegenServiceProcessor.identation(2));
        statement.append("}");
        statement.append(Constants.NEWLINE);
      }
      // If the method throws something that is not a remote exception wrap it.
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("catch(Throwable e)");
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("{");
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(3));
      statement.append("throw new RemoteException(\"" + Constants.EXCEPTION_MESSAGE + "\",e);");
      statement.append(Constants.NEWLINE);
      statement.append(PostCodegenServiceProcessor.identation(2));
      statement.append("}");              
      statement.append(Constants.NEWLINE);
    }
    return statement.toString();
  }

  /**
   * Used to fill in the bodies of the delegated methods created by the 
   * interfaces extension.
   *
   * @param delegate Name of the variable that implements the interface
   * which declares the given method.
   * @param method The delegated method.
   * @param contents The contents of the source file to add the method too.
   */
  public static void addMethodBody(String delegate, MethodConfiguration methodConfiguration, StringBuffer contents) {
    MethodBody methodBody = new MethodBody(contents, methodConfiguration.buildMethodSignature(false));
    methodBody.setContents(PostCodegenServiceProcessor.getWrappedDelegatedCallStatement(delegate, methodConfiguration));
  }

}
