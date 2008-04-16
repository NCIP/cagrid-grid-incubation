package org.cagrid.workflow.helper.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class ConversionUtil {

	/* Make the string representation of a (XML) Node object */
	public static String Node2String(Node node){

		String ret_val = null;

		// Use a Transformer for output
		StringWriter writer = new StringWriter();

		try {
			TransformerFactory tFactory =
				TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource node_source = new DOMSource(node);
			StreamResult str_result = new StreamResult(writer);
			transformer.transform(node_source, str_result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		ret_val = writer.getBuffer().toString();  

		// Remove <? xml version=... ?> from the string
		int location_to_remove = ret_val.indexOf("?>") + "?>".length();
		ret_val = ret_val.substring(location_to_remove);



		return ret_val;
	}


	/* Make the SOAPElement representation of an XML string*/
	public static Iterator String2SOAPElement(String str){

		str = "<abcdefghijklmnzwxyz>" + str + "</abcdefghijklmnzwxyz>";
		Iterator iter = null;

		try {
			SOAPElement converted = new org.apache.axis.message.SOAPBodyElement(new ByteArrayInputStream(str.getBytes("UTF-8")));
			iter = converted.getChildElements();


		} catch (IOException e) {
			e.printStackTrace();
		} 

		return iter;
	}

	
}
