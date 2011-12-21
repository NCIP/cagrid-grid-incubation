package org.cagrid.workflow.helper.invocation.common;

import javax.xml.namespace.QName;


public interface WorkflowInvocationHelperConstants {
	public static final String SERVICE_NS = "http://helper.workflow.cagrid.org/WorkflowHelper/Invocation";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowInvocationHelperKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowInvocationHelperResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName CURRENTTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
	public static final QName TERMINATIONTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime");
	public static final QName WORKFLOWINVOCATIONHELPERDESCRIPTOR = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "WorkflowInvocationHelperDescriptor");
	public static final QName OPERATIONINPUTMESSAGEDESCRIPTOR = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "OperationInputMessageDescriptor");
	public static final QName OPERATIONOUTPUTTRANSPORTDESCRIPTOR = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "OperationOutputTransportDescriptor");
	public static final QName TIMESTAMPEDSTATUS = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "TimestampedStatus");
	public static final QName INSTRUMENTATIONRECORD = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "InstrumentationRecord");
	public static final QName OUTPUTREADY = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "OutputReady");
	
}
