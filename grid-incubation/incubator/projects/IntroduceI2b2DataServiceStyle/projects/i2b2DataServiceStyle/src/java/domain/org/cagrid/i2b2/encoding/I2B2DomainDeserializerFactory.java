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
