package org.cagrid.workflow.helper.common;

import javax.xml.namespace.QName;


public interface WorkflowHelperConstants {
	public static final String SERVICE_NS = "http://helper.workflow.cagrid.org/WorkflowHelper";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowHelperKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowHelperResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
