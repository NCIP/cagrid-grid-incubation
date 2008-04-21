package org.cagrid.introduce.service1.common;

import javax.xml.namespace.QName;


public interface Service1Constants {
	public static final String SERVICE_NS = "http://service1.introduce.cagrid.org/Service1";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "Service1Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "Service1ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
