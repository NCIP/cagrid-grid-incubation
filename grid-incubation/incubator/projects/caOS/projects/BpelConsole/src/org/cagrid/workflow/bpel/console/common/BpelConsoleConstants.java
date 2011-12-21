package org.cagrid.workflow.bpel.console.common;

import javax.xml.namespace.QName;


public interface BpelConsoleConstants {
	public static final String SERVICE_NS = "http://console.bpel.workflow.cagrid.org/BpelConsole";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "BpelConsoleKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "BpelConsoleResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
