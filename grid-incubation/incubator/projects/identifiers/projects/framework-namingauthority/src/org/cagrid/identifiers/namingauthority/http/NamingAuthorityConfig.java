/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.identifiers.namingauthority.http;

//
// This class holds configuration that can be made
// publicly available via the HTTP port.
//
public class NamingAuthorityConfig implements java.io.Serializable {
	
	private String gridSvcUrl;
	
	public NamingAuthorityConfig() {
		
	}
	
	public NamingAuthorityConfig( org.cagrid.identifiers.namingauthority.NamingAuthorityConfig config) {
		setGridSvcUrl(config.getGridSvcUrl());
	}
	
	public void setGridSvcUrl( String gridSvcUrl ) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public String getGridSvcUrl() {
		return this.gridSvcUrl;
	}

}
