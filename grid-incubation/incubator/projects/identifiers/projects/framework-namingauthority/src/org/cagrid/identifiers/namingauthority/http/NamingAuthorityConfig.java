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
