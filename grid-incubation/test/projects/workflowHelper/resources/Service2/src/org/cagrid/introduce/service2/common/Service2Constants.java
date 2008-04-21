package org.cagrid.introduce.service2.common;

import javax.xml.namespace.QName;


public interface Service2Constants {
	public static final String SERVICE_NS = "http://service2.introduce.cagrid.org/Service2";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service2Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service2ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
