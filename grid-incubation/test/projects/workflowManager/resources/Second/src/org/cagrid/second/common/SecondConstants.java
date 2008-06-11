package org.cagrid.second.common;

import javax.xml.namespace.QName;


public interface SecondConstants {
	public static final String SERVICE_NS = "http://second.cagrid.org/Second";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "SecondKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "SecondResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
