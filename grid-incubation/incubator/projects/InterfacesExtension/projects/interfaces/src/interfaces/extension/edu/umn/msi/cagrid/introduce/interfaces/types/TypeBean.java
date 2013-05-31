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
package edu.umn.msi.cagrid.introduce.interfaces.types;

import javax.xml.namespace.QName;

/**
 * A Java Bean that describes one QName and one fully qualified Java class,
 * with convience methods for fetching the QName, canonical java class name, 
 * XML namespaceURI and localPart, and java class name and package.
 *   
 * This can be used to model the binding of a QName to a Java class.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
public class TypeBean {
  private String namespace;
  private String localPart;
  private String javaPackage;
  private String javaClassName;
  
  public TypeBean() {}
  
  public TypeBean(String namespace, String localPart, String javaPackage, String javaClassName) {
    init(namespace,localPart,javaPackage, javaClassName);
  }
  
  private void init(String namespace, String localPart, String javaPackage, String javaClassName) {
    this.namespace = namespace;
    this.localPart = localPart;
    this.javaPackage = javaPackage;
    this.javaClassName = javaClassName;
  }
  
  public TypeBean(String namespace, String localPart, Class<?> class_) {
    String fullClassName = class_.getName();
    int idx = fullClassName.lastIndexOf('.');
    String className = fullClassName.substring(idx+1);
    init(namespace, localPart, (class_.getPackage() != null) ? class_.getPackage().getName() : null, className);
  }

  public TypeBean(QName qName, Class<?> class_) {
    this(qName.getNamespaceURI(), qName.getLocalPart(), class_);
  }
  
  public TypeBean(QName qName, String javaPackage, String javaClassName) {
    this(qName.getNamespaceURI(), qName.getLocalPart(), javaPackage, javaClassName);
  }
  
  /**
   * @return The QName represented by this object.
   */
  public QName getQName() {
    return new QName(namespace, localPart);
  }

  /**
   * @return The canonical name of the java class represented 
   * by this object.
   */
  public String getJavaCanonicalName() {
    if(javaPackage == null || javaPackage.equals("")) {
      return getJavaClassName();
    } else {
      return getJavaPackage() + "." + getJavaClassName();
    }
  }  
  
  public String getJavaPackage() {
    return javaPackage;
  }

  public void setJavaPackage(String javaPackage) {
    this.javaPackage = javaPackage;
  }

  public String getJavaClassName() {
    return javaClassName;
  }

  public void setJavaClassName(String javaClassName) {
    this.javaClassName = javaClassName;
  }
  
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getLocalPart() {
    return localPart;
  }

  public void setLocalPart(String localPart) {
    this.localPart = localPart;
  }
  
}
