package org.cagrid.workflow.helper.util;

import javax.wsdl.Definition;

public class NamespaceUtil {

	
	public static String getNamespaceForService(String service_url){
		
		
		String namespace_uri = null;
		
		
		org.apache.axis.wsdl.gen.Parser parser = new org.apache.axis.wsdl.gen.Parser();
		try {
			parser.run(service_url+"?WSDL");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Definition defs = parser.getCurrentDefinition();
		namespace_uri = defs.getTargetNamespace();
		if( namespace_uri.matches(".*/service$") ){  // remove undesirable token from namespace
			
			namespace_uri = namespace_uri.substring(0, namespace_uri.length()- new String("/service").length());
		}
		
		return namespace_uri;
	}
}
