package org.cagrid.tide.context.common;

import javax.xml.namespace.QName;


public interface TideContextConstants {
	public static final String SERVICE_NS = "http://tide.cagrid.org/Tide/Context";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "TideContextKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "TideContextResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName CURRENTTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
	public static final QName TERMINATIONTIME = new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime");
	public static final QName TIDEDESCRIPTOR = new QName("http://tide.cagrid.org/TideDescriptor", "TideDescriptor");
	
}
