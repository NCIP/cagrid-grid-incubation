package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.Type;
import gov.nih.nci.cagrid.identifiers.TypeValues;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.namingauthority.http.NamingAuthorityConfig;


public class ResolverUtil {

	//
	// Given an identifier, it returns the real naming authority URL
	//
	public static String getNamingAuthorityURL( String identifier ) throws HttpException, IOException {
		
		String identifierURL = "";
		
		HttpClient client = new HttpClient();
		
		HttpMethod method = new GetMethod( identifier );
		
		method.setFollowRedirects(false);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    try {
	    	System.out.println("Connecting to " + identifier);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	if (statusCode ==  HttpStatus.SC_MOVED_TEMPORARILY) {
	    		// Expected redirect
	    		identifierURL = method.getResponseHeader("Location").getValue();
	    	} else {
	    		// No redirect. Assume identifier already points to NA
	    		identifierURL = identifier;
	    	}
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return identifierURL;
	}
	
	//
	// Given the naming authority URL, it returns the naming
	// authority configuration object.
	//
	public static NamingAuthorityConfig getNamingAuthorityConfig(String url) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod( url );
		method.setFollowRedirects(true);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    NamingAuthorityConfig config = null;
	    
	    try {
	    	System.out.println("Connecting to " + url);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	if (statusCode != HttpStatus.SC_OK)
	    		throw new HttpException("Unable to retrieve naming authority config from " 
	    				+ url + " [" + statusCode + ":" + method.getStatusLine() + "]");
	    	
	    	Header ctHeader = method.getResponseHeader("Content-Type");
	    	if (ctHeader == null || ctHeader.getValue() == null || 
	    			ctHeader.getValue().indexOf("application/xml") == -1) {
	    		throw new HttpException("Unable to retrieve naming authority config from " 
	    				+ url + ". Response has no XML content.");
	    	}
	   
	    	String response = method.getResponseBodyAsString();

	    	// Deserialize response
			XMLDecoder decoder = new XMLDecoder(new StringBufferInputStream(
					response));
		    
		    config = (NamingAuthorityConfig)decoder.readObject();
		    decoder.close();
		    
		    if (config == null) {
		    	System.out.println("No data found for specified identifier");
		    	return null;
		    }
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return config;
	}
	
	public static IdentifierValues convert( TypeValues[] tvsArr ) {
		if (tvsArr == null)
			return null;
		
		IdentifierValues ivs = new IdentifierValues();
		
		for( TypeValues tvs : tvsArr ) {
			Type type = tvs.getType();
			Values values = tvs.getValues();
			for( String value : values.getValue() ) {
				ivs.add(type.getValue(), value);
			}
		}
		
		return ivs;
	}
	
	public static IdentifierValues resolveGrid( String identifier ) throws HttpException, IOException {
		String url = getNamingAuthorityURL( identifier );
		NamingAuthorityConfig config = getNamingAuthorityConfig( url + "?config" );
		if (config == null) {
			throw new HttpException("Unable to retrieve naming authority configuration from " + url);
		}
		
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );
		
		System.out.println("Connecting to " + config.getGridSvcUrl() + " to retrieve values for identifier " + identifier);
		return gov.nih.nci.cagrid.identifiers.common.MappingUtil.toIdentifierValues(
				client.getTypeValues(identifier) );
	}
	
	public static IdentifierValues resolveHttp( String identifier ) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		
		HttpMethod method = new GetMethod( identifier );
		
		method.setFollowRedirects(true);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    method.addRequestHeader(new Header("Accept", "application/xml"));
	    
	    IdentifierValues ivs = null;
	    
	    try {
	    	System.out.println("Connecting to " + identifier);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	if (statusCode != HttpStatus.SC_OK) {
	    		throw new HttpException(identifier + " returned [" + statusCode + ":" + method.getStatusLine() + "]");
	        }
	    	
	    	Header ctHeader = method.getResponseHeader("Content-Type");
	    	if (ctHeader == null || ctHeader.getValue() == null || 
	    			ctHeader.getValue().indexOf("application/xml") == -1) {
	    		throw new HttpException(identifier + " returned no XML content (Content-Type: "
	    				+ (ctHeader != null ? ctHeader.getValue() : "null") + ")");
	    	}
	   
	    	String response = method.getResponseBodyAsString();
	    	System.out.println(response);
	    	// Deserialize response
			XMLDecoder decoder = new XMLDecoder(new StringBufferInputStream(
					response));
		    
		    ivs = (IdentifierValues)decoder.readObject();
		    decoder.close();
		    
		    if (ivs == null) {
		    	System.out.println("No data found for specified identifier");
		    	return null;
		    }
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return ivs;
	}
}
