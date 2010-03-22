package org.cagrid.i2b2.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class I2B2DomainSerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(I2B2DomainSerializerFactory.class.getName());


	public I2B2DomainSerializerFactory(Class<?> javaType, QName xmlType) {
		super(I2B2Serializer.class, xmlType, javaType);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Initializing " + I2B2Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
