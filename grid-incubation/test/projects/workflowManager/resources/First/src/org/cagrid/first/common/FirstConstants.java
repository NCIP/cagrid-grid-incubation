package org.cagrid.first.common;

import javax.xml.namespace.QName;


public interface FirstConstants {
	public static final String SERVICE_NS = "http://first.cagrid.org/First";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "FirstKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "FirstResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
