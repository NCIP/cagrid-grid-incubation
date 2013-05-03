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
package gov.nih.nci.cagrid.mapping.extension;


/**
 * Lists out the names of jar files needed for the data service that
 * aren't part of the regular service deployment
 */
public class AdditionalLibraries  implements java.io.Serializable {
    private java.lang.String[] jarName;

    public AdditionalLibraries() {
    }

    public AdditionalLibraries(
           java.lang.String[] jarName) {
           this.jarName = jarName;
    }


    /**
     * Gets the jarName value for this AdditionalLibraries.
     * 
     * @return jarName
     */
    public java.lang.String[] getJarName() {
        return jarName;
    }


    /**
     * Sets the jarName value for this AdditionalLibraries.
     * 
     * @param jarName
     */
    public void setJarName(java.lang.String[] jarName) {
        this.jarName = jarName;
    }

    public java.lang.String getJarName(int i) {
        return this.jarName[i];
    }

    public void setJarName(int i, java.lang.String _value) {
        this.jarName[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AdditionalLibraries)) return false;
        AdditionalLibraries other = (AdditionalLibraries) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.jarName==null && other.getJarName()==null) || 
             (this.jarName!=null &&
              java.util.Arrays.equals(this.jarName, other.getJarName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getJarName() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getJarName());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getJarName(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AdditionalLibraries.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.data.extension", "AdditionalLibraries"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jarName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.data.extension", "JarName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
