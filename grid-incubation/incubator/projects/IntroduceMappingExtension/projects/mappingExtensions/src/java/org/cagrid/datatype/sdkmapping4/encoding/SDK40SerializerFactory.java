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
package org.cagrid.datatype.sdkmapping4.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK40SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK40SerializerFactory.class.getName());


	public SDK40SerializerFactory(Class javaType, QName xmlType) {
		super(SDK40Serializer.class, xmlType, javaType);
		LOG.debug("Initializing " + SDK40Serializer.class.getSimpleName() + " for class:" + javaType + " and QName:" + xmlType);
	}
}
