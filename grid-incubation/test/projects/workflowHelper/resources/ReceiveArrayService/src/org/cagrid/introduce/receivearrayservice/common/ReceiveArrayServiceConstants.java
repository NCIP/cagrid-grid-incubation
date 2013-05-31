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
package org.cagrid.introduce.receivearrayservice.common;

import javax.xml.namespace.QName;


public interface ReceiveArrayServiceConstants {
	public static final String SERVICE_NS = "http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "ReceiveArrayServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "ReceiveArrayServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
