package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.GSISecureConversation;
import gov.nih.nci.cagrid.metadata.security.GSISecureMessage;
import gov.nih.nci.cagrid.metadata.security.GSITransport;
import gov.nih.nci.cagrid.metadata.security.None;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadataOperations;

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

import org.cagrid.workflow.helper.descriptor.ChannelProtection;
import org.cagrid.workflow.helper.descriptor.SecureConversationInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.SecureMessageInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.TLSInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
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


	/** Create an instance of ServiceSecurityMetadata using a WorkflowInvocationSecurityDescriptor
	 * @param workflowInvocationSecurityDescriptor Service security settings description
	 * 
	 * @return an instance of ServiceSecurityMetadata with information according to the security descriptor received  
	 * */
	public static ServiceSecurityMetadata createServiceSecurityMetadata(
			WorkflowInvocationSecurityDescriptor workflowInvocationSecurityDescriptor, String operationName) {
		
		System.out.println("[createServiceSecurityMetadata] BEGIN"); //DEBUG
		
		// Info for initializing default communication mechanism
		CommunicationMechanism commMechanism = new CommunicationMechanism();
		
		
		// Info for initializing ServiceSecurityMetadataOperations
		Operation[] operationArray;
		
		
		
		// (1) Get all the information from the WorkflowInvocationSecurityDescriptor. Only one of the three getter methods is supposed
		// to return a non-null value. So, we need to figure out which one it is. 
		if( workflowInvocationSecurityDescriptor.getSecureConversationInvocationSecurityDescriptor() != null ){ // Secure Conversation used
			
			System.out.println("[createServiceSecurityMetadata] Setting Secure Conversation"); //DEBUG
			
			SecureConversationInvocationSecurityDescriptor sec_desc = workflowInvocationSecurityDescriptor.getSecureConversationInvocationSecurityDescriptor();
			ChannelProtection cp = sec_desc.getChannelProtection();
			ProtectionLevelType protectionLevel = ProtectionLevelType.fromValue(cp.getValue());
			GSISecureConversation gsiSecureConversation = new GSISecureConversation();
			gsiSecureConversation.setProtectionLevel(protectionLevel);
			
			//anonymousPermitted = true;
			
			commMechanism.setGSISecureConversation(gsiSecureConversation);
			commMechanism.setAnonymousPermitted(true);
		}
		else if( workflowInvocationSecurityDescriptor.getSecureMessageInvocationSecurityDescriptor() != null ){ // Secure Message used
			
			System.out.println("[createServiceSecurityMetadata] Setting Secure Message"); //DEBUG
			
			SecureMessageInvocationSecurityDescriptor sec_desc = workflowInvocationSecurityDescriptor.getSecureMessageInvocationSecurityDescriptor();
			ChannelProtection cp = sec_desc.getChannelProtection();
			ProtectionLevelType protectionLevel = ProtectionLevelType.fromValue(cp.getValue());
			GSISecureMessage gsiSecureMessage = new GSISecureMessage();
			gsiSecureMessage.setProtectionLevel(protectionLevel);
			
			//anonymousPermitted = false;
			
			commMechanism.setGSISecureMessage(gsiSecureMessage);
			commMechanism.setAnonymousPermitted(false);
		}
		else if( workflowInvocationSecurityDescriptor.getTLSInvocationSecurityDescriptor() != null ){  // TLS used
			
			System.out.println("[createServiceSecurityMetadata] Setting TLS"); //DEBUG
			
			TLSInvocationSecurityDescriptor sec_desc = workflowInvocationSecurityDescriptor.getTLSInvocationSecurityDescriptor();
			ChannelProtection cp = sec_desc.getChannelProtection();
			ProtectionLevelType protectionLevel = null;
			
			// Figure out what kind of channel protection is required
			if( cp.equals(ChannelProtection.Integrity) ){
				protectionLevel = ProtectionLevelType.integrity;
				System.out.println("[createServiceSecurityMetadata] Integrity set"); // DEBUG
			}
			else if( cp.equals(ChannelProtection.Privacy) ){
				protectionLevel = ProtectionLevelType.privacy;
				System.out.println("[createServiceSecurityMetadata] Privacy set"); // DEBUG
			}
			else {
				System.err.println("[createServiceSecurityMetadata] Unrecognized channel protection mechanism");
			}
			
			
			GSITransport gsiTransport = new GSITransport();
			gsiTransport.setProtectionLevel(protectionLevel);
			
			//anonymousPermitted = true;
			
			commMechanism.setGSITransport(gsiTransport);
			commMechanism.setAnonymousPermitted( (sec_desc.getAnonymousAuthenticationMethod() != null) );
			
		}
		else {  // No security mechanisms used
			
			System.out.println("[createServiceSecurityMetadata] Setting none"); //DEBUG
			
			None none = new None();
			//anonymousPermitted = true;
			
			commMechanism.setNone(none);
			commMechanism.setAnonymousPermitted(true);
		}
		
		
		
		// (1.1) Set the operations' metadata 
		operationArray = new Operation[1];
		operationArray[0] = new Operation();
		operationArray[0].setCommunicationMechanism(commMechanism);
		operationArray[0].setName(operationName);
		
		
		// (2) Use the retrieved information to initialize the ServiceSecurityMetadata 
		// Initialize the communication mechanism 
		CommunicationMechanism defaultCommunicationMechanism = commMechanism;
		
		// Initialize the sevice's operations 
		ServiceSecurityMetadataOperations operations = new ServiceSecurityMetadataOperations();
		operations.setOperation(operationArray);
		
		
		// Finally, create the service security metadata
		ServiceSecurityMetadata secDesc = new ServiceSecurityMetadata();
		secDesc.setDefaultCommunicationMechanism(defaultCommunicationMechanism);
		secDesc.setOperations(operations);
		
		
		System.out.println("[createServiceSecurityMetadata] END"); //DEBUG
		
		return secDesc;
	} // */

	
}
