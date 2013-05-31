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

import edu.umn.msi.cagrid.introduce.interfaces.codegen.DocletUtils;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.MethodBody;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.Regexes;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.StringBufferUtils;
import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringFieldGenerator;
import edu.umn.msi.cagrid.introduce.interfaces.spring.SpringInitGenerator;

import static edu.umn.msi.cagrid.introduce.interfaces.Constants.NEWLINE;

public class ServiceImplProcessor {
  private String serviceName;
  private String mainServiceName;
  private SpringConfiguration springConfiguration;
  private StringBuffer source;
  
  public ServiceImplProcessor(StringBuffer source, String serviceName, String mainServiceName, SpringConfiguration springConfiguration) {
    this.source = source;
    this.serviceName = serviceName;
    this.mainServiceName = mainServiceName;
    this.springConfiguration = springConfiguration;
  }
  
  public void execute() throws Exception {
    if(serviceName.equals(mainServiceName)) {
      String constructorSignature = "public " + serviceName + "Impl()";
      MethodBody methodBody = new MethodBody(source, constructorSignature);
      if(!methodBody.contains(Constants.INIT_METHOD_NAME)) {
        methodBody.append("try { " + Constants.INIT_METHOD_NAME + "(getConfiguration()); } catch(Exception e) { throw new RemoteException(\"Failed to initialize beans.\", e); }\n", true);
      }
    }
    DocletUtils.eliminateFieldsWithTag(Constants.SPRING_EXTENSION_ANNOTATION, source);
    DocletUtils.eliminateSimpleMethodsWithTag(Constants.SPRING_EXTENSION_ANNOTATION, source);
    int classStart = source.indexOf("public class " + serviceName + "Impl");
    int bracketStart = source.indexOf("{", classStart);
    if(serviceName.equals(mainServiceName)) {
      SpringInitGenerator initGenerator = new SpringInitGenerator();
      initGenerator.setServiceName(mainServiceName);
      initGenerator.setBeans(springConfiguration.getBeansForService(serviceName));
      source.insert(bracketStart+1, initGenerator.generate());
    }
    SpringFieldGenerator fieldGenerator = new SpringFieldGenerator();
    fieldGenerator.setServiceName(mainServiceName);
    fieldGenerator.setBeans(springConfiguration.getBeansForService(serviceName));
    source.insert(bracketStart+1, fieldGenerator.generate());
    // TODO: Track down source of lines with only whitespace in them.
    StringBufferUtils.replaceAll(source, Regexes.MULTIPLE_WHITESPACE_ONLY_LINES, NEWLINE + NEWLINE);
  }
  
}
