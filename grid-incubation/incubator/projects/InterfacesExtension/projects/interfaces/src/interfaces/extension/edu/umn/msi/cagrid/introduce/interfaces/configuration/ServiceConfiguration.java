package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import java.util.Collection;
import java.util.LinkedList;

import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;

public class ServiceConfiguration {
  /**
   * Fields that implement interfaces for this service.
   */
  private Collection<FieldConfiguration> fields;

  public Collection<FieldConfiguration> getFields() {
    return fields;
  }

  public void setFields(Collection<FieldConfiguration> fields) {
    this.fields = fields;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping) {
    for(FieldConfiguration field : fields) {
      field.addDefaultValues(defaultTypeMapping);
    }
  }
  
  public Collection<InterfaceConfiguration> getInterfaces() {
    LinkedList<InterfaceConfiguration> interfaces = new LinkedList<InterfaceConfiguration>();
    for(FieldConfiguration field : fields) {
      interfaces.addAll(field.getInterfaces());
    }
    return interfaces;
  }
  
  public Collection<MethodConfiguration> getMethods() {
    LinkedList<MethodConfiguration> methods = new LinkedList<MethodConfiguration>();
    for(FieldConfiguration field : fields) {
      methods.addAll(field.getMethods());
    }
    return methods;
  }
  
}
