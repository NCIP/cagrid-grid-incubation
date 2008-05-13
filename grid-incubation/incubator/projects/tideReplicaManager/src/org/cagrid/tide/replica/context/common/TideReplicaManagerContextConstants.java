package org.cagrid.tide.replica.context.common;

import javax.xml.namespace.QName;


public interface TideReplicaManagerContextConstants {
	public static final String SERVICE_NS = "http://replica.tide.cagrid.org/TideReplicaManager/Context";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "TideReplicaManagerContextKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "TideReplicaManagerContextResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName CURRENTTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
	public static final QName TERMINATIONTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime");
	public static final QName TIDEREPLICASDESCRIPTOR = new QName("http://tide.cagrid.org/TideDescriptor", "TideReplicasDescriptor");
	
}
