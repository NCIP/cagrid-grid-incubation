package org.cagrid.workflow.helper.instance.common;

import javax.xml.namespace.QName;


public interface WorkflowInstanceHelperConstants {
	public static final String SERVICE_NS = "http://helper.workflow.cagrid.org/WorkflowHelper/Instance";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowInstanceHelperKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowInstanceHelperResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName CURRENTTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
	public static final QName TERMINATIONTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime");
	public static final QName WORKFLOWINSTANCEHELPERDESCRIPTOR = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "WorkflowInstanceHelperDescriptor");
	public static final QName TIMESTAMPEDSTATUS = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "TimestampedStatus");
	
}
