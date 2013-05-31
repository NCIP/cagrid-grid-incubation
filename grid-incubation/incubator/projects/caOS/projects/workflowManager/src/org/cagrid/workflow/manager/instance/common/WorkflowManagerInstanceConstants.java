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
package org.cagrid.workflow.manager.instance.common;

import javax.xml.namespace.QName;


public interface WorkflowManagerInstanceConstants {
	public static final String SERVICE_NS = "http://manager.workflow.cagrid.org/WorkflowManagerService/Context";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowManagerInstanceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowManagerInstanceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName CURRENTTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
	public static final QName TERMINATIONTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime");
	public static final QName WORKFLOWMANAGERINSTANCEDESCRIPTOR = new QName("http://workflowmanagerservice.workflow.cagrid.org/WorkflowManagerService", "WorkflowManagerInstanceDescriptor");
	public static final QName TIMESTAMPEDSTATUS = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "TimestampedStatus");
	public static final QName INSTRUMENTATIONRECORD = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "InstrumentationRecord");
	public static final QName OUTPUTREADY = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "OutputReady");
	public static final QName LOCALWORKFLOWINSTRUMENTATIONRECORD = new QName("http://workflowhelperservice.workflow.cagrid.org/WorkflowHelperService", "LocalWorkflowInstrumentationRecord");
	
}
