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
package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import java.util.Collection;
import java.util.LinkedList;

import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;

public class FieldConfiguration { // TODO: Rename to DelegateConfiguration
  /**
   * Name of the Java class field that implements interfaces for the given
   * caGrid service.
   */
  private String name;

  /**
   * Is the delegate a method (otherwise its a field).
   */
  private boolean method;
  
  /**
   * Collection of configurations objects for the interfaces this field 
   * implements for the caGrid service.
   */
  private Collection<InterfaceConfiguration> interfaces = new LinkedList<InterfaceConfiguration>();
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * @return the interfaces
   */
  public Collection<InterfaceConfiguration> getInterfaces() {
    return interfaces;
  }
  
  /**
   * @param interfaces the interfaces to set
   */
  public void setInterfaces(Collection<InterfaceConfiguration> interfaces) {
    this.interfaces = interfaces;
  }
  
  public void setMethod(boolean method) {
    this.method = method;
  }
  
  public boolean isMethod() {
    return method;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping) {
    for(InterfaceConfiguration interface_ : interfaces) {
      interface_.addDefaultValues(defaultTypeMapping);
    }
  }

  public Collection<MethodConfiguration> getMethods() {
    LinkedList<MethodConfiguration> methods = new LinkedList<MethodConfiguration>();
    for(InterfaceConfiguration interface_ : interfaces) {
      methods.addAll(interface_.getMethods());
    }
    return methods;
  }
  
}
