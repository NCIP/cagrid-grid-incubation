package org.cagrid.i2b2.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class I2B2DomainDeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(I2B2DomainDeserializerFactory.class.getName());


	public I2B2DomainDeserializerFactory(Class<?> javaType, QName xmlType) {
		super(I2B2DomainDeserializer.class, xmlType, javaType);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Initializing " + I2B2DomainDeserializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
