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
/**
 * 
 */
package edu.uams.soulwing.cas.client;

import javax.servlet.http.HttpServlet;

import javax.net.ssl.HttpsURLConnection;

import javax.servlet.ServletConfig;

import javax.servlet.ServletException;


/**
 * @author Dan Sobkoviak
 * Initialize webapp to disable host name checking
 *
 */
public class HostNameCheckDisablingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException { 

	HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
	}
}
