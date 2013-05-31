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
package org.cagrid.datatype.sdkmapping4.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK40DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK40DeserializerFactory.class.getName());


	public SDK40DeserializerFactory(Class javaType, QName xmlType) {
		super(SDK40Deserializer.class, xmlType, javaType);
		LOG.debug("Initializing " + SDK40Deserializer.class.getSimpleName() + " for class:" + javaType + " and QName:" + xmlType);
	}
}
