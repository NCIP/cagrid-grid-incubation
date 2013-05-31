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
package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import javax.xml.namespace.QName;

import edu.umn.msi.cagrid.introduce.interfaces.Constants;
import edu.umn.msi.cagrid.introduce.interfaces.TypeUtils;
import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;

public class ParameterConfiguration {
  /**
   * XML type of the parameter to the service operation.
   */
  private QName xmlType;
  
  /**
   * Java type of the parameter to the service operation.
   */
  private Class<?> javaType;
  
  /**
   * Name to give the parameter to the service operation.
   */
  private String name;
  
  /**
   * Description to give the parameter to the service operation.
   */
  private String description;
  
  /**
   * Parent configuration object.
   */
  private MethodConfiguration methodConfiguration;
  
  public QName getXmlType() {
    return xmlType;
  }
  
  public void setXmlType(QName xmlType) {
    this.xmlType = xmlType;
  }
  
  public Class<?> getJavaType() {
    return javaType;
  }
  
  public void setJavaType(Class<?> javaType) {
    this.javaType = javaType;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  public MethodConfiguration getMethodConfiguration() {
    return methodConfiguration;
  }

  public void setMethodConfiguration(MethodConfiguration methodConfiguration) {
    this.methodConfiguration = methodConfiguration;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping, int argumentIndex) {
    if(description == null) {
      description = Constants.DEFAULT_PARAMETER_DESCRIPTION;
    }
    if(name == null || name.equals("")) {
      name = "arg" + (argumentIndex+1);
    }
    if(xmlType == null && defaultTypeMapping != null) { 
      xmlType = TypeUtils.getQName(javaType, defaultTypeMapping);
      if(xmlType == null) {
        throw new IllegalStateException("Unknown QName corresponding to java type " + javaType.getCanonicalName());
      }
    }
  }

  public MethodTypeInputsInput getIntroduceMethodTypeInputsInput() {
    MethodTypeInputsInput input = new MethodTypeInputsInput();
    input.setQName(xmlType);
    input.setIsArray(TypeUtils.getIsArray(xmlType, javaType));
    input.setName(name);
    input.setDescription(description);
    return input;
  }
  
}
