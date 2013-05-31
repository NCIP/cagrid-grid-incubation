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
package org.cagrid.data.sdkquery42.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK42DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK42DeserializerFactory.class.getName());


	public SDK42DeserializerFactory(Class<?> javaType, QName xmlType) {
		super(SDK42Deserializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK42Deserializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
