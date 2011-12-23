package edu.umn.msi.cagrid.introduce.interfaces.types.mapping;

import javax.xml.namespace.QName;

public interface TypeMapping {
  public QName getQName(String canonicalName);
  public QName getQName(Class<? extends Object> class_);
}
