package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

public class BeanUtils {

  static String getSetterName(String propertyName) {
      return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

  static String getGetterName(String propertyName) {
      return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
  }

}
