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
package org.cagrid.identifiers.namingauthority;

public abstract class NamingAuthority {
	
	private NamingAuthorityConfig config;
	
	public NamingAuthority( NamingAuthorityConfig config ) {
		this.config = config;
	}
	
	public NamingAuthorityConfig getConfig() { return this.config; }
}
