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
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;

public class ResultConfiguration {
  /**
   * Description to give the result of the service operation.
   */
  private String description;
  
  /**
   * XML Type of the operation.
   */
  private QName xmlType;
  
  /**
   * Java type of the operation;
   */
  private Class<?> javaType;

  /**
   * Parent configuration object.
   */
  private MethodConfiguration methodConfiguration;
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

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

  public MethodConfiguration getMethodConfiguration() {
    return methodConfiguration;
  }

  public void setMethodConfiguration(MethodConfiguration methodConfiguration) {
    this.methodConfiguration = methodConfiguration;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping) {
    if(description == null) {
      description = Constants.DEFAULT_METHOD_RETURN_DESCRIPTION;
    }
    if(xmlType == null && defaultTypeMapping != null) {
      xmlType = TypeUtils.getQName(javaType, defaultTypeMapping);
    } 
  }

  public MethodTypeOutput getIntroduceMethodTypeOutput() {
    MethodTypeOutput output = new MethodTypeOutput();
    if(javaType.equals(Void.TYPE)) {
      output.setQName(QName.valueOf("void"));
      output.setIsArray(false);
    } else {
      output.setQName(xmlType);
      output.setIsArray(TypeUtils.getIsArray(xmlType, javaType));
    }
    output.setDescription(description);
    return output;
  }
}
