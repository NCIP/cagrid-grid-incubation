package org.cagrid.iso21090.portal.discovery.constants;

public interface Constants {

    public static final String EXTENSION_NAME = "ISO21090-discovery";

    /*
    public static final String DATATYPES_NAMESPACE_KEY = "ISO21090_DATATYPES_NAMESPACE";
    public static final String DATATYPES_PACKAGE_KEY = "ISO21090_DATATYPES_PACKAGE";
    */
    public static final String DATATYPES_FILENAME_KEY = "ISO21090_DATATYPES_FILENAME";
    
    public static final String EXTENSION_NAMESPACE_KEY = "ISO21090_EXTENSION_NAMESPACE";
    public static final String EXTENSION_PACKAGE_KEY = "ISO21090_EXTENSION_PACKAGE";
    public static final String EXTENSION_FILENAME_KEY = "ISO21090_EXTENSION_FILENAME";
    public static final String INT_ELEMENT_NAME_FROM_SCHEMA = "ISO21090_INT_ELEMENT_NAME";

    public static final String DESERIALIZER_FACTORY_CLASSNAME = "gov.nih.nci.iso21090.grid.ser.JaxbDeserializerFactory";
    public static final String SERIALIZER_FACTORY_CLASSNAME = "gov.nih.nci.iso21090.grid.ser.JaxbSerializerFactory";
}
