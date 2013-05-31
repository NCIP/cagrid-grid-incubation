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

import java.io.IOException;

import edu.umn.msi.cagrid.introduce.interfaces.codegen.SourceUtils;
import edu.umn.msi.cagrid.introduce.interfaces.services.Service;
import edu.umn.msi.cagrid.introduce.interfaces.services.ServiceProcessor;

public class AddImportsServiceProcessor implements ServiceProcessor {
  private String[] classNames;
  public AddImportsServiceProcessor(String className) {
    this(new String[]{className});
  }
  
  public AddImportsServiceProcessor(String[] classNames) {
    this.classNames = classNames;
  }
  
  public AddImportsServiceProcessor(Class<?> class_) {
    this(new Class<?>[]{class_});
  }
  
  public AddImportsServiceProcessor(Class<?>[] classes) {
    classNames = new String[classes.length];
    for(int i = 0; i < classes.length; i++) {
      classNames[i] = classes[i].getCanonicalName();
    }
  }
 
  public void execute(Service service) throws IOException {
    StringBuffer sourceContents = new StringBuffer(service.getServiceImplContents());
    for(String className : classNames) {
      SourceUtils.addImport(className, sourceContents);
    }
    service.setServiceImplContents(sourceContents.toString());
  }

}
