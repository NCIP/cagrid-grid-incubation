/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.gaards.cds.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://cds.gaards.cagrid.org/CredentialDelegationService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "CredentialDelegationServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "CredentialDelegationServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA_Value_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
