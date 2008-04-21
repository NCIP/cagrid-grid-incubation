package org.cagrid.introduce.service3.common;

import javax.xml.namespace.QName;


public interface Service3Constants {
	public static final String SERVICE_NS = "http://service3.introduce.cagrid.org/Service3";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service3Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service3ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
