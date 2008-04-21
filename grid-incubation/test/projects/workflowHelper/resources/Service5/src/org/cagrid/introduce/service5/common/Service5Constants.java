package org.cagrid.introduce.service5.common;

import javax.xml.namespace.QName;


public interface Service5Constants {
	public static final String SERVICE_NS = "http://service5.introduce.cagrid.org/Service5";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service5Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service5ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
