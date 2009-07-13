package org.cagrid.identifiers.namingauthority.http;

import org.cagrid.identifiers.core.IdentifierUser;
import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.*;

public class HttpServer implements Runnable {
	
	public static String HTTP_ACCEPT_HDR = "Accept";
	public static String HTTP_ACCEPT_HTML = "text/html";
	public static String HTTP_ACCEPT_XML = "application/xml";
	public static String HTTP_ACCEPT_ANY = "*/*";
	

	private int _port;
	private NamingAuthority _na;
	
	public HttpServer(NamingAuthority na, int port) {
		_port = port;
		_na = na;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	public boolean xmlResponseRequired(HttpServletRequest req) {
		boolean htmlOk = false;
		boolean xmlOk = false;
		boolean anyOk = false;
		
		String resType = req.getHeader(HTTP_ACCEPT_HDR);
		
		System.out.println("ACCEPT["+resType+"]");
		
		if (resType != null) {
			
			// I found that each browser specifies Accept header
			// differently (see below). For example, Safari specifies
			// application/xml as the first accepted format, so
			// obviously, if we just take into account the first
			// format, we'd always return XML to Safari, which is
			// undesired. So what we'll do is that we'll return
			// HTML unless it is not listed as one of the accepted
			// formats. "*/*" will also override XML. Client programs 
			// other than browsers wishing to retrieve XML should list 
			// XML only. 
			//
			// Safari
			// application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
			//
			// Firefox
			// text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
			//
			// IE
			// */*
			
			String[] resTypeArr = resType.split(",");
			for( String type : resTypeArr ) {
				if (type.contains(HTTP_ACCEPT_HTML))
					htmlOk = true;
				else if (type.contains(HTTP_ACCEPT_ANY))
					anyOk = true;
				else if (type.contains(HTTP_ACCEPT_XML))
					xmlOk = true;
			}
			
			if (!htmlOk && !anyOk && xmlOk) 
				return true;
		}
		
		return false;
	}
	
//	public boolean isBrowser(HttpServletRequest request) {
//		String ua = request.getHeader("User-Agent");
//		System.out.println("User-Agent:" + ua);
//		if (ua != null) {
//			if (ua.indexOf( "Firefox/" ) != -1 ||
//					ua.indexOf( "MSIE" ) != -1 ||
//					ua.indexOf( "Safari/" ) != -1)
//				return true;
//		}
//
//		return false;
//	}
	
	public String htmlResponse(String idStr, IdentifierValues ivs) {
		StringBuffer msg = new StringBuffer();
		
		if (ivs == null)
		{
			msg.append("<h2>Identifier [" + idStr + "] could not be found</h2>\n");
		}
		else {
    		msg.append("<h3>" + idStr + "</h3>\n<hr>\n");
    		
    		for( String type : ivs.getTypes()) {
    			msg.append("<b>Type: &nbsp;</b>" + type + "<br>\n");
            	for( String value : ivs.getValues(type) ) {
            		msg.append("<b>Data: &nbsp;</b>" + escape(value) + "<br>\n");
            	}
            	msg.append("<hr>\n");
            }
		}
		
		return msg.toString();
	}
	
	public String xmlResponse(IdentifierValues ivs) {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(baos);
        encoder.writeObject(ivs);
        encoder.close();
      
        return baos.toString();
	}

	public String xmlConfigResponse() {
		NamingAuthorityConfig publicConfig = new NamingAuthorityConfig( this._na.getConfig() );
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(baos);
        encoder.writeObject(publicConfig);
        encoder.close();
      
        return baos.toString();
	}
	
	public void run() {
		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
		        throws java.io.IOException, javax.servlet.ServletException
		    {
		    	StringBuffer msg = new StringBuffer();
		    	
		    	//
		    	// ?config causes to ignore resolution and
		    	// return naming authority configuration
		    	// instead
		    	//
		    	String config = request.getParameter("config");
		    	if (config != null) {
		    		msg.append(xmlConfigResponse());
	    			response.setContentType("application/xml");
		    	} else {
		    		//
			    	// Specifying ?xml in the URL forces XML output
			    	// (Usefull for a user debugging from a web
			    	// browser)
			    	//
			    	boolean forceXML = false;
			    	if (request.getParameter("xml") != null) {
			    		forceXML = true;
			    	}
			    	
		    		boolean xmlResponse = forceXML || xmlResponseRequired(request);
		    		boolean noErrors = true;
		    		
		    		String uri = request.getRequestURI();
				    System.out.println("URI["+uri+"]");
				    String idStr = null;
				    if (uri == null || uri.length() <= 1 || !uri.startsWith("/")) {
				    	msg.append("<h1>No identifier provided</h1>");
				    	noErrors = false;
				    	response.setContentType("text/html");
				    } else {
				    	idStr = IdentifierUtil.generate(_na.getConfig().getPrefix(), uri.substring(1));
				    }
		    		
		    		if (noErrors) {
		    		
			    		IdentifierValues ivs = ((IdentifierUser)_na).getValues(idStr);
			    		
			    		if (xmlResponse) {
			    			msg.append(xmlResponse(ivs));
			    			response.setContentType("application/xml");
			    		} else {
			    			msg.append(htmlResponse(idStr, ivs));
			    			response.setContentType("text/html");
			    		}
			    	}
		    	}
		    	    
		        response.setStatus(HttpServletResponse.SC_OK);
		        response.getWriter().println(msg.toString());
		        ((Request)request).setHandled(true);
		    }
		};
		
		Server server = new Server( _port );
		server.setHandler(handler);
		try {
			server.start();
			
			System.out.println("HTTP Jetty Server Started on port " + _port);
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to start Jetty HTTP Server: " + e);
			e.printStackTrace();
		}
	}
	
	public String escape(String inStr) {
		String outStr = inStr.replaceAll("<", "&lt;");
		outStr = outStr.replaceAll(">", "&gt;");
		return outStr;
	}
}
