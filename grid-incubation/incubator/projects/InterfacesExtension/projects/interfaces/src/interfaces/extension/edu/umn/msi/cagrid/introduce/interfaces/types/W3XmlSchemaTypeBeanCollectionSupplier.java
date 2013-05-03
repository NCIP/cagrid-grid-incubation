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
package edu.umn.msi.cagrid.introduce.interfaces.types;

import java.util.Collection;
import java.util.HashSet;

public class W3XmlSchemaTypeBeanCollectionSupplier implements TypeBeanCollectionSupplier {
  public static final String W3_XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema"; 
  private static Collection<TypeBean> typeMappingBeans;
  
  static {
    typeMappingBeans = new HashSet<TypeBean>();
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "anyType",            java.lang.Object.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "string",             java.lang.String.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "boolean",            boolean.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "int",                int.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "integer",            java.math.BigInteger.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "decimal",            java.math.BigDecimal.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "float",              float.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "double",             double.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "short",              short.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "long",               long.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "byte",               byte.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "base64Binary",       byte[].class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "dateTime",           java.util.Calendar.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "date",               java.util.Date.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "QName",              javax.xml.namespace.QName.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "anySimpleType",      java.lang.String.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "duration",           org.apache.axis.types.Duration.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "time",               org.apache.axis.types.Time.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "gYearMonth",         org.apache.axis.types.YearMonth.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "gYear",              org.apache.axis.types.Year.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "gMonthDay",          org.apache.axis.types.MonthDay.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "gMonth",             org.apache.axis.types.Month.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "hexBinary",          byte[].class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "NOTATION",           org.apache.axis.types.Notation.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "anyURI",             org.apache.axis.types.URI.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "normalizedString",   org.apache.axis.types.NormalizedString.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "token",              org.apache.axis.types.Token.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "nonPositiveInteger", org.apache.axis.types.NonPositiveInteger.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "nonNegativeInteger", org.apache.axis.types.NonNegativeInteger.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "language",           org.apache.axis.types.Language.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "Name",               org.apache.axis.types.Name.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "NMTOKEN",            org.apache.axis.types.NMToken.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "NMTOKENS",           org.apache.axis.types.NMTokens.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "negativeInteger",    org.apache.axis.types.NegativeInteger.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "positiveInteger",    org.apache.axis.types.PositiveInteger.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "unsignedLong",       org.apache.axis.types.UnsignedLong.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "unsignedInt",        org.apache.axis.types.UnsignedInt.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "unsignedShort",      org.apache.axis.types.UnsignedShort.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "unsignedByte",       org.apache.axis.types.UnsignedByte.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "NCName",             org.apache.axis.types.NCName.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "ID",                 org.apache.axis.types.Id.class ));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "IDREF",              org.apache.axis.types.IDRef.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "IDREFS",             org.apache.axis.types.IDRefs.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "ENTITY",             org.apache.axis.types.Entity.class));
    typeMappingBeans.add(new TypeBean(W3_XML_SCHEMA_NAMESPACE, "ENTITIES",           org.apache.axis.types.Entities.class));
  }

  public Collection<TypeBean> get() {
    return typeMappingBeans;
  }
}
