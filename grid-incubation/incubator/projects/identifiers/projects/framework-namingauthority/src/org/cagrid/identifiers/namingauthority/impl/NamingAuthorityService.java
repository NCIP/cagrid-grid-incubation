package org.cagrid.identifiers.namingauthority.impl;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cagrid.identifiers.namingauthority.http.HttpProcessor;

public class NamingAuthorityService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private NamingAuthorityImpl namingAuthority;
	private HttpProcessor httpProcessor;
       
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
		String identifiersNaDbUser = config.getInitParameter("identifiersNaDbUser");
		String identifiersNaDbPassword = config.getInitParameter("identifiersNaDbPassword");
		String identifiersNaPrefix = config.getInitParameter("identifiersNaPrefix");
		String identifiersNaGridSvcUrl = config.getInitParameter("identifiersNaGridSvcUrl");
		
		//
		// Start Naming Authority
		//
		NamingAuthorityConfigImpl naConfig = new NamingAuthorityConfigImpl();
		naConfig.setPrefix(identifiersNaPrefix);
		naConfig.setDbUrl(identifiersNaDbUrl);
		naConfig.setDbUser(identifiersNaDbUser);
		naConfig.setDbPassword(identifiersNaDbPassword);
		naConfig.setGridSvcUrl(identifiersNaGridSvcUrl);

		this.namingAuthority = new NamingAuthorityImpl(naConfig);
		this.httpProcessor = new HttpProcessor( this.namingAuthority );
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("getPathInfo["+request.getPathInfo()+"]");
		System.out.println("getQueryString["+request.getQueryString()+"]");
		System.out.println("getRequestURI["+request.getRequestURI()+"]");
		System.out.println("getRequestURL["+request.getRequestURL()+"]");
		System.out.println("getServerName["+request.getServerName()+"]");
		System.out.println("getServerPort["+request.getServerPort()+"]");
		System.out.println("getServletPath["+request.getServletPath()+"]");
		
		
		httpProcessor.processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        System.out.println("doPost not implemented...");
	}

}
