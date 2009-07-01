package org.cagrid.identifiers.resolver;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.StringBufferInputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.cagrid.identifiers.core.IdentifierValues;


public class ResolverUtil {

	public static String getNamingAuthorityURL( String identifier ) {
		
		String identifierURL = "";
		String naURL = "";
		
		HttpClient client = new HttpClient();
		
		HttpMethod method = new GetMethod( identifier );
		
		method.setFollowRedirects(false);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    try {
	    	System.out.println("Connecting to " + identifier);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	System.out.println("Status code: " + statusCode);
	    	
	    	if (statusCode == 302) {
	    		// Expected redirect
	    		identifierURL = method.getResponseHeader("Location").getValue();
	    	
	    	} else {
	    		// No redirect. Assume identifier already points to NA
	    		identifierURL = identifier;
	    	}
	    	
	    	System.out.println("identifierURI="+identifierURL);
	    	
	    	URI naURI = new URI( identifierURL );
//	    	System.out.println("host="+naURI.getHost());
//	    	System.out.println("name="+naURI.getName());
//	    	System.out.println("path="+naURI.getPath());
//	    	System.out.println("pathquery="+naURI.getPathQuery());
//	    	System.out.println("port="+naURI.getPort());
//	    	System.out.println("fragment="+naURI.getFragment());
//	    	System.out.println("charset="+naURI.getProtocolCharset());
//	    	System.out.println("query="+naURI.getQuery());
	    	
	    	naURL = identifierURL.replaceFirst(naURI.getPath(), "");

	    } catch (HttpException e) {
	        System.err.println("Fatal protocol violation: " + e.getMessage());
	        e.printStackTrace();
	    } catch (IOException e) {
	        System.err.println("Fatal transport error: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	    	System.err.println("Fatal unexpected exception: " + e.getMessage());
	    	e.printStackTrace();
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return naURL;
	}
	
	public static void resolveGrid( String identifier ) {
		String URI = getNamingAuthorityURL( identifier );
	}
	
	public static IdentifierValues resolveHttp( String identifier ) {
		HttpClient client = new HttpClient();
		
		HttpMethod method = new GetMethod( identifier );
		
		method.setFollowRedirects(true);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    IdentifierValues ivs = null;
	    
	    try {
	    	System.out.println("Connecting to " + identifier);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	System.out.println("Status code: " + statusCode);
	    	
	    	Header ctHeader = method.getResponseHeader("Content-Type");
	    	if (ctHeader == null || ctHeader.getValue() == null || 
	    			ctHeader.getValue().indexOf("application/xml") == -1) {
	    		System.out.println("Response has no XML content");
	    		return null;
	    	}
	   
	   // 	byte[] responseBody = method.getResponseBodyAsString();
	    	
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
		    
		    for( String type : ivs.getTypes() ) {
		    	for( String value : ivs.getValues( type )) {
		    		System.out.println("["+type+"]=["+value+"]");
		    	}
		    }
		    
	
		    
//	    	// Deal with the response.
//	        // Use caution: ensure correct character encoding and is not binary data
//	    	System.out.println("Body[" + new String(responseBody) + "]");
//	
//	    	System.out.println("StatusCode="+method.getStatusCode());
//	    	System.out.println("StatusText="+method.getStatusText());
//	    	System.out.println("StatusLine="+method.getStatusLine());
//	    	System.out.println("Content type=["+method.getResponseHeader("Content-Type").getValue()+"]");
//	    	for( Header h : method.getResponseHeaders()) {
//	    		System.out.println("Response Header[" + h.getName() + "]=[" + h.getValue() + "]");
//	    		for( HeaderElement he : h.getElements() ) {
//	    			System.out.println("Response Header Element[" + he.getName() + "]=[" + he.getValue() + "]");
//	    			NameValuePair[] nvps = he.getParameters();
//	    			if (nvps != null) {
//	    				for( NameValuePair nvp : nvps) {
//	    					System.out.println("NameValuePair: [" + nvp.getName() + "]=[" + nvp.getValue() + "]");
//	    				}
//	    			}
//	    		}
//	    	}
	    } catch (HttpException e) {
	        System.err.println("Fatal protocol violation: " + e.getMessage());
	        e.printStackTrace();
	    } catch (IOException e) {
	        System.err.println("Fatal transport error: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	    	System.err.println("Fatal unexpected exception: " + e.getMessage());
	    	e.printStackTrace();
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return ivs;
	}
	
	public static void main(String[] args) {
		String purl="http://purl.cagrid.org:8090/cagrid/b14f8d9c-6f6e-43c3-8fa5-aac049b1c80a";
		//ResolverUtil.resolveHttp(purl);
		System.out.println("NA="+ResolverUtil.getNamingAuthorityURL(purl));
	}
}
