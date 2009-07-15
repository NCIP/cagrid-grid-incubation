package org.cagrid.identifiers.namingauthority.impl;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NamingAuthorityService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private NamingAuthorityImpl na;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NamingAuthorityService() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println("NamingAuthorityService initializing...");
		
		String identifiersNaDbUrl = config.getInitParameter("identifiersNaDbUrl");
		String identifiersNaHttpServerPort = config.getInitParameter("identifiersNaHttpServerPort");
		String identifiersNaDbUser = config.getInitParameter("identifiersNaDbUser");
		String identifiersNaDbPassword = config.getInitParameter("identifiersNaDbPassword");
		String identifiersNaPrefix = config.getInitParameter("identifiersNaPrefix");
		
		//
		// Start Naming Authority
		//
		NamingAuthorityConfigImpl naConfig = new NamingAuthorityConfigImpl();
		naConfig.setPrefix(identifiersNaPrefix);
		naConfig.setHttpServerPort(Integer.valueOf(identifiersNaHttpServerPort));
		naConfig.setDbUrl(identifiersNaDbUrl);
		naConfig.setDbUser(identifiersNaDbUser);
		naConfig.setDbPassword(identifiersNaDbPassword);

		na = new NamingAuthorityImpl(naConfig);
		na.startHttpServer();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("getPathInfo["+request.getPathInfo()+"]");
//		System.out.println("getQueryString["+request.getQueryString()+"]");
//		System.out.println("getRequestURI["+request.getRequestURI()+"]");
//		System.out.println("getRequestURL["+request.getRequestURL()+"]");
//		System.out.println("getServerName["+request.getServerName()+"]");
//		System.out.println("getServerPort["+request.getServerPort()+"]");
//		System.out.println("getServletPath["+request.getServletPath()+"]");
		
		String site = "http://" + request.getServerName();
		
		if ( na.getConfig().getHttpServerPort() != null )
			site += ":" + na.getConfig().getHttpServerPort();
		
		if ( request.getQueryString() != null ) 
			site += "/" + request.getQueryString();
		
		response.sendRedirect(site);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        System.out.println("doPost not implemented...");
	}

}
