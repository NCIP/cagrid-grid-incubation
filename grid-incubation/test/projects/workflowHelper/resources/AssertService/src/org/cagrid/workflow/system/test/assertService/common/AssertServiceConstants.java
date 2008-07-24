package org.cagrid.workflow.system.test.assertService.common;

import javax.xml.namespace.QName;


public interface AssertServiceConstants {
	public static final String SERVICE_NS = "http://assertService.test.system.workflow.cagrid.org/AssertService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "AssertServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "AssertServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
