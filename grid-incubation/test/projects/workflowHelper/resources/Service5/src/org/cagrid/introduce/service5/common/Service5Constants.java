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
package org.cagrid.introduce.service5.common;

import javax.xml.namespace.QName;


public interface Service5Constants {
	public static final String SERVICE_NS = "http://service5.introduce.cagrid.org/Service5";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service5Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service5ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
