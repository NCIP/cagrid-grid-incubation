package org.cagrid.tide.replica.common;

import javax.xml.namespace.QName;


public interface TideReplicaManagerConstants {
	public static final String SERVICE_NS = "http://replica.tide.cagrid.org/TideReplicaManager";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "TideReplicaManagerKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "TideReplicaManagerResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
