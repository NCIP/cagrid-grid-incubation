package org.cagrid.workflow.manager.common;

import javax.xml.namespace.QName;


public interface WorkflowManagerServiceConstants {
	public static final String SERVICE_NS = "http://manager.workflow.cagrid.org/WorkflowManagerService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowManagerServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowManagerServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
