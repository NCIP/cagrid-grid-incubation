package org.cagrid.helloworld.common;

import javax.xml.namespace.QName;


public interface HelloWorldSecureConstants {
	public static final String SERVICE_NS = "http://helloworld.cagrid.org/HelloWorldSecure";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "HelloWorldSecureKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "HelloWorldSecureResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
