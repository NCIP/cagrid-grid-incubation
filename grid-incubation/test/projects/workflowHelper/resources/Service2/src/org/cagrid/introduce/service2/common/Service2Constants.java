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
package org.cagrid.introduce.service2.common;

import javax.xml.namespace.QName;


public interface Service2Constants {
	public static final String SERVICE_NS = "http://service2.introduce.cagrid.org/Service2";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service2Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service2ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
