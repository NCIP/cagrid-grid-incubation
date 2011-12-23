package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import java.util.Collection;
import java.util.LinkedList;

import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;

public class InterfaceConfiguration {
  /**
   * The Java class object that specified this interface.
   */
  private Class<?> class_;
  
  /**
   * Fully qualified Java name of the interface represented by this
   * configuration object.
   */
  private String name;
  
  /**
   * Collection of configuration objects for the methods this interface
   * is composed of.
   */
  private Collection<MethodConfiguration> methods = new LinkedList<MethodConfiguration>();

  /**
   * Parent configuration object.
   */
  private FieldConfiguration fieldConfiguration;
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<MethodConfiguration> getMethods() {
    return methods;
  }

  public void setMethods(Collection<MethodConfiguration> methods) {
    this.methods = methods;
  }

  public Class<?> getJavaClass() {
    return class_;
  }

  public void setJavaClass(Class<?> class_) {
    this.class_ = class_;
  }

  public FieldConfiguration getFieldConfiguration() {
    return fieldConfiguration;
  }

  public void setFieldConfiguration(FieldConfiguration fieldConfiguration) {
    this.fieldConfiguration = fieldConfiguration;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping) {
    if(name == null || name.equals("")) {
      name = class_.getCanonicalName();
    }
    for(MethodConfiguration method : methods) {
      method.addDefaultValues(defaultTypeMapping);
    }
  }

}
