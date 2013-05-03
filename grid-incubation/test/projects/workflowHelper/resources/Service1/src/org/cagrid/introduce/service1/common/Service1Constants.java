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
package org.cagrid.introduce.service1.common;

import javax.xml.namespace.QName;


public interface Service1Constants {
	public static final String SERVICE_NS = "http://service1.introduce.cagrid.org/Service1";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service1Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service1ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
