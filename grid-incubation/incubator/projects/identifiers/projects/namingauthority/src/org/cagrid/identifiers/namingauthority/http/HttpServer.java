package org.cagrid.identifiers.namingauthority.http;

import org.cagrid.identifiers.namingauthority.IdentifierUser;
import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.*;

public class HttpServer implements Runnable {

	private int _port;
	private NamingAuthority _na;
	
	public HttpServer(NamingAuthority na, int port) {
		_port = port;
		_na = na;
	}
	
	public void start() {
		new Thread(this).start();
	}

	public void run() {
		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
		        throws java.io.IOException, javax.servlet.ServletException
		    {
		    	StringBuffer msg = new StringBuffer();
		    	
		    	String uri = request.getRequestURI();
		    	if (uri == null || uri.length() <= 1 || !uri.startsWith("/")) {
		    		msg.append("<h1>No identifier provided</h1>");
		    	}
		    	else
		    	{
		    		String idStr = IdentifierUtil.generate(_na.getPrefix(), uri.substring(1));
		    		IdentifierValues ivs = ((IdentifierUser)_na).getValues(idStr);
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
		    	}
		    	
		    	response.setContentType("text/html");
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
