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
package edu.umn.msi.cagrid.introduce.interfaces;

import javax.xml.namespace.QName;

import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;

public class TypeUtils {
  public static final QName STRING_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "string");
  public static final QName BYTE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "byte");
  public static final QName BASE_64_BINARY_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "base64Binary");
  public static final QName HEX_BINARY_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "hexBinary");
  public static final QName ANY_SIMPLE_TYPE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");


  public static boolean getIsArray(QName qName, Class<?> class_) {
    if(qName.equals(TypeUtils.BASE_64_BINARY_QNAME) || qName.equals(TypeUtils.HEX_BINARY_QNAME)) {
      // These both map to byte[] which is an array in Java but not in AXIS SOAP.
      // I think (hope) this only special case like this. -John
      return !class_.getCanonicalName().equals("byte[]");
    } else {
      return class_.isArray();
    }
  }

  public static QName getQName(Class<?> class_, TypeMapping mapping) {
    String className = class_.getCanonicalName();
    QName qName = null;   
    if(className.equals("byte[]")) {
      switch(TypeUtils.byteArrayDisambiguation) {
      case BYTE_ARRAY:
        qName = TypeUtils.BYTE_QNAME;
        break;
      case BASE_64_BINARY:
        qName = TypeUtils.BASE_64_BINARY_QNAME;
        break;
      case HEX_BINARY:
        qName = TypeUtils.HEX_BINARY_QNAME;
        break;
      }
    } else if(className.equals("byte[][]")) {
      switch(TypeUtils.byteArrayArrayDisambiguation) {
      case BASE_64_BINARY_ARRAY:
        qName = TypeUtils.BASE_64_BINARY_QNAME;
        break;
      case HEX_BINARY_ARRAY:
        qName = TypeUtils.HEX_BINARY_QNAME;
        break;
      }
    } else if(className.equals("java.lang.String")) 
      switch(TypeUtils.stringDisambiguation) {
      case STRING:
        qName = TypeUtils.STRING_QNAME;
        break;
      case ANY_SIMPLE_TYPE:
        qName = TypeUtils.ANY_SIMPLE_TYPE_QNAME;
    } else {
      if(class_.isArray()) {
        qName = mapping.getQName(className.substring(0, className.length()-2));
      } else {
        qName = mapping.getQName(className);
      }
    }
    return qName;
  }

  /**
   * When inspecting method inputs and outputs, a Java type 
   * of byte[] could map to three different XML types - hexBinary,
   * base64Binary, or an array of type byte. This 
   * field decides which of these is mapped to by default.   
   */
  public static ByteArrayDisambiguation byteArrayDisambiguation = ByteArrayDisambiguation.BASE_64_BINARY;
  /**
   * When inspecting method inputs and outputs, a Java type 
   * of byte[][] could map to two XML types - an array of hexBinary 
   * objects or an array of base64 binary objects. This 
   * field decides which of these is mapped to by default. 
   */
  public static ByteArrayArrayDisambiguation byteArrayArrayDisambiguation = ByteArrayArrayDisambiguation.BASE_64_BINARY_ARRAY;
  /**
   * When inspecting method inputs and outputs, a Java type of 
   * java.lang.String could map to two different XML types - 
   * string or anySimpleType. This field decides which 
   * of these is mapped to by default. 
   */  
  public static StringDisambiguation stringDisambiguation = StringDisambiguation.STRING;

  public enum ByteArrayDisambiguation {
    BYTE_ARRAY, HEX_BINARY, BASE_64_BINARY
  }

  public enum ByteArrayArrayDisambiguation {
    HEX_BINARY_ARRAY, BASE_64_BINARY_ARRAY 
  }

  public enum StringDisambiguation {
    STRING, ANY_SIMPLE_TYPE;
  }

  
}

