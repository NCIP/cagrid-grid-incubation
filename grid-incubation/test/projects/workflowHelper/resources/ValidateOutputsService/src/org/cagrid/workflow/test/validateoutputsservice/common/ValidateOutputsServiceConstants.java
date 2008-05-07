package org.cagrid.workflow.test.validateoutputsservice.common;

import javax.xml.namespace.QName;


public interface ValidateOutputsServiceConstants {
	public static final String SERVICE_NS = "http://validateoutputsservice.test.workflow.cagrid.org/ValidateOutputsService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "ValidateOutputsServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "ValidateOutputsServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
