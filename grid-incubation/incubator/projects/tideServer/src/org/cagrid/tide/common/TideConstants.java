package org.cagrid.tide.common;

import javax.xml.namespace.QName;


public interface TideConstants {
	public static final String SERVICE_NS = "http://tide.cagrid.org/Tide";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "TideKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "TideResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
