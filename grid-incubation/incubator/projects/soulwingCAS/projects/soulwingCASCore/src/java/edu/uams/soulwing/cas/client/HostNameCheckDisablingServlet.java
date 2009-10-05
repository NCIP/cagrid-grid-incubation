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
