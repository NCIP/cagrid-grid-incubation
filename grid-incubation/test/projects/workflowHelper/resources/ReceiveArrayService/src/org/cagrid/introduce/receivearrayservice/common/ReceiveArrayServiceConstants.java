package org.cagrid.introduce.receivearrayservice.common;

import javax.xml.namespace.QName;


public interface ReceiveArrayServiceConstants {
	public static final String SERVICE_NS = "http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "ReceiveArrayServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "ReceiveArrayServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
