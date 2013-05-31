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
package org.cagrid.identifiers.namingauthority;

public interface NamingAuthorityConfig {
	
	public String getPrefix();
	public Integer getHttpServerPort();
	public String getGridSvcUrl();
	public String getDbUserName();
	public String getDbPassword();
	public String getDbUrl();
}
