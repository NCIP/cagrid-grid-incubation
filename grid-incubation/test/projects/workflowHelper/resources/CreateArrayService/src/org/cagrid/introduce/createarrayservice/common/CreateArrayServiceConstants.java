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
package org.cagrid.introduce.createarrayservice.common;

import javax.xml.namespace.QName;


public interface CreateArrayServiceConstants {
	public static final String SERVICE_NS = "http://createarrayservice.introduce.cagrid.org/CreateArrayService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "CreateArrayServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "CreateArrayServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
