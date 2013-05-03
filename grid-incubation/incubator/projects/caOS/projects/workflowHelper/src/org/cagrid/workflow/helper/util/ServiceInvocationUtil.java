/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.helper.util;



import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.NamespaceConstants;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.Text;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ServiceInvocationUtil {


	private static Log logger = LogFactory.getLog(ServiceInvocationUtil.class);

	public class SecureServiceInvocationUtil {

	}


	public static Node generateUnsecureRequest(WorkflowInvocationHelperDescriptor workflowDescriptor, OperationInputMessageDescriptor input_desc,
			OperationOutputTransportDescriptor output_desc, InputParameter[] paramData, WorkflowInvocationHelperClient operationClient) throws Exception{

		logger.info("Generating unsecure request"); 
		return generateRequest(workflowDescriptor, input_desc, output_desc, paramData, null, operationClient);
	}


	public static Node generateSecureRequest(WorkflowInvocationHelperDescriptor workflowDescriptor, OperationInputMessageDescriptor input_desc,
			OperationOutputTransportDescriptor output_desc, InputParameter[] paramData, GlobusCredential proxy, WorkflowInvocationHelperClient operationClient) throws Exception{

		logger.info("Generating Secure request"); 
		return generateRequest(workflowDescriptor, input_desc, output_desc, paramData, proxy, operationClient);
	}




	/** Generate a request to a service, invoke it and grab its output
	 * 
	 * @param workflowDescriptor Description of operation to invoke
	 * @param input_desc Description of operation's inputs
	 * @param output_desc Description of operation's output
	 * @param paramData Values of operation's parameters
	 * @param operationClient 
	 * 
	 * @return The operation's output value
	 * 
	 * */
	private static Node generateRequest(final WorkflowInvocationHelperDescriptor workflowDescriptor, final OperationInputMessageDescriptor input_desc,
			final OperationOutputTransportDescriptor output_desc, final InputParameter[] paramData, final GlobusCredential proxy, WorkflowInvocationHelperClient operationClient) throws Exception{



		SOAPEnvelope ret = null;
		Node response = null;
		boolean hasCredential = (proxy != null); // (workflowDescriptor.getWorkflowInvocationSecurityDescriptor() != null)


		String serviceNamespace = workflowDescriptor.getOperationQName().getNamespaceURI();
		String action_name = serviceNamespace+'/'+workflowDescriptor.getOperationQName().getLocalPart();
		QName operationQname = null;

		logger.info("Creating XML request for: "+ serviceNamespace + " - " + workflowDescriptor.getOperationQName().getLocalPart());


		/** Create invocation message */
		SOAPEnvelope message = new SOAPEnvelope();
		message.addAttribute(new PrefixedQName(new QName("xmlns")), serviceNamespace);
		message.addAttribute(new PrefixedQName(new QName("xmlns:wsa")), 
		"http://schemas.xmlsoap.org/ws/2004/03/addressing");



		/* Create SOAP Header */
		SOAPHeader header = message.getHeader();


		// 'To' element
		SOAPHeaderElement to = new SOAPHeaderElement(new PrefixedQName(new QName("wsa:To")));
		to.setValue( workflowDescriptor.getServiceURL());
		to.addAttribute(new PrefixedQName(new QName("xmlns")), serviceNamespace);
		to.addAttribute(new PrefixedQName(new QName("xmlns:wsa")), 
		"http://schemas.xmlsoap.org/ws/2004/03/addressing");
		header.addChildElement(to);


		// 'Action' element 
		SOAPHeaderElement action = new SOAPHeaderElement(new PrefixedQName(new QName("wsa:Action")));
		action.setValue(action_name);
		action.addAttribute(new PrefixedQName(new QName("xmlns")), serviceNamespace);
		action.addAttribute(new PrefixedQName(new QName("xmlns:wsa")), "http://schemas.xmlsoap.org/ws/2004/03/addressing");
		header.addChildElement(action);


		// 'From' element 
		SOAPHeaderElement from = new SOAPHeaderElement(new PrefixedQName(new QName("wsa:From")));
		from.addAttribute(new PrefixedQName(new QName("xmlns")), serviceNamespace);
		from.addAttribute(new PrefixedQName(new QName("xmlns:wsa")), 
		"http://schemas.xmlsoap.org/ws/2004/03/addressing");

		SOAPHeaderElement address = new SOAPHeaderElement(new PrefixedQName(new QName("wsa:Address"))); // 'Address' element
		address.setValue("http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous");
		address.addAttribute(new PrefixedQName(new QName("xmlns")), serviceNamespace);
		address.addAttribute(new PrefixedQName(new QName("xmlns:wsa")), 
		"http://schemas.xmlsoap.org/ws/2004/03/addressing");
		from.addChildElement(address);
		header.addChildElement(from);




		// Create SOAP body 
		javax.xml.soap.SOAPBody body = message.getBody();


		// Set method name
		QName operation_name = workflowDescriptor.getOperationQName();
		SOAPBodyElement operation = body.addBodyElement(new PrefixedQName(new QName(operation_name.getLocalPart())));
		operation.setAttribute("xmlns", serviceNamespace);


		// Set method parameters
		final int numParams = (input_desc.getInputParam() != null)? input_desc.getInputParam().length : 0;
		paramsLoop:	for(int i=0; i < numParams; i++){


			/* Convert the user's data into one single tree of SOAPElements. 
				 We would need to get rid of the root, that's something like <....Response>.
				 Although, the root is useful to get information about the parameter's type. 
			 */
			final QName cur_param_qname = input_desc.getInputParam(i).getParamQName();
			SOAPBodyElement curr_param = new org.apache.axis.message.SOAPBodyElement(new QName(cur_param_qname.getLocalPart()));
			Iterator root_obj = ConversionUtil.String2SOAPElement(paramData[i].getData());

			/* Add this parameter */
			boolean isSimpleArray = false, isComplexArray = false; // True only if current parameter is an array. Default value is set here
			if( root_obj.hasNext() ){


				Object next_elem = root_obj.next();

				// 1st case) Parameter is represented by a simple string
				if(next_elem instanceof org.apache.axis.message.Text){
					Text inner_txt = (Text) next_elem;
					curr_param = new org.apache.axis.message.SOAPBodyElement(new QName(cur_param_qname.getLocalPart())); // can't touch this
					curr_param.addTextNode(inner_txt.getNodeValue());
				}

				// 2nd case) Parameter is represented as a list of attributes (complex type, simple type array, complex type array)
				else if( next_elem instanceof javax.xml.soap.SOAPElement ){

					SOAPElement elem = (SOAPElement) next_elem;

					Iterator objs_to_add = elem.getChildElements();

					// Add all elements one at a time
					while( objs_to_add.hasNext() ){
						Object next_to_add = objs_to_add.next();


						if( next_to_add instanceof org.apache.axis.message.Text){
							Text inner_txt = (Text) next_to_add;
							curr_param.addTextNode(inner_txt.getNodeValue());
						}
						else if( next_to_add instanceof javax.xml.soap.SOAPElement ){

							SOAPElement next_to_add_soap = (SOAPElement) next_to_add;
							String next_to_add_name = next_to_add_soap.getNodeName();

							// Verify whether the response is an array
							isSimpleArray = next_to_add_name.contains("response"); // clause for simple types
							isComplexArray = elem.getNodeName().contains("Response"); // clause for complex types


							if( !isSimpleArray && !isComplexArray ){

								// Then 'elem' belongs to a complex type, and it is already in a suitable format to add to the request
								curr_param.addChildElement(elem);

								logger.info("Adding struct: "+elem);
								break;

							}
							else if( isSimpleArray ){

								addSimpleArrayToRequest(next_to_add_soap, curr_param, cur_param_qname.getLocalPart(), operation);
							}
							else if( isComplexArray ){

								addComplexArrayToRequest(elem.getChildElements(), curr_param);
								break;
							}
						}
						else throw new Exception("Unknown return class '"+next_to_add.getClass().getCanonicalName()+"'");

					} 
				}

			}
			if( isSimpleArray ) continue paramsLoop; // Array is completely set, proceed to next parameter
			operation.addChildElement(curr_param);
		}


		// Print message before sending it
		logger.debug("Printing SOAP Envelope for "+workflowDescriptor.getOperationQName().toString()
				+":  \n________________________________________________________>\n"+message.toString()+
		"\n<________________________________________________________\n"); 
	
		
		
	
		/// Invoke service 
		javax.xml.rpc.Service service = null;
		Call call;
		EndpointReferenceType serviceOperationEPR = operationClient.getEndpointReference();  //new EndpointReference(workflowDescriptor.getServiceURL());
		// Secure invocation when a credential is provided
		if( hasCredential ){   

			logger.info("DO have credential");


			ServiceSecurityMetadata securityMetadata = ConversionUtil.createServiceSecurityMetadata(
					workflowDescriptor.getWorkflowInvocationSecurityDescriptor(), workflowDescriptor.getOperationQName().getLocalPart());
			SecurityConfigurationUtil config_service  = new SecurityConfigurationUtil(serviceOperationEPR, proxy);



			// Set Authorization
			Authorization authorization = org.globus.wsrf.impl.security.authorization.NoAuthorization.getInstance();  // We don't care about service's identity     
			config_service.setAuthorization(authorization); 
			config_service.configureStubSecurity(workflowDescriptor.getOperationQName().getLocalPart(), securityMetadata);

			call = (Call) config_service.getCall();

		}
		else {  // No credential provided, proceed to unsecure invocation 

			logger.info("Have NO credential");

			service = new Service();
			call = (Call) service.createCall();
			call.setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
		}


		call.setTargetEndpointAddress( new java.net.URL(workflowDescriptor.getServiceURL()));
		String tns = serviceNamespace; 
		operationQname = new QName(tns, workflowDescriptor.getOperationQName().toString());
		call.setOperationName(operationQname);

		
		logger.info("Invoking service "+workflowDescriptor.getOperationQName().toString());
		logger.info("Service URL: "+ call.getTargetEndpointAddress());

		try {
			call.setTimeout(Integer.MAX_VALUE);   // Configure the call with a timeout that will never expire (at least not before 60 years)
			ret = call.invoke(message);
		}
		catch(Throwable t){
			logger.error("Error invoking operation "+ workflowDescriptor.getOperationQName(), t);
			throw new Exception(t.getMessage());
		}


		// Print received SOAP message
		logger.debug("Returned SOAP: \n________________________________________________________>\n"+ret.toString()
				+"\n<________________________________________________________\n");// */

		// Get return from invoked method
		SOAPBody body1 = ret.getBody(); 
		response = body1.getFirstChild();



		logger.debug("Service return for "+workflowDescriptor.getOperationQName().getLocalPart()
				+": \n________________________________________________________>\n"+response
				+"\n<________________________________________________________\n");


		return response;
	}



	/**
	 * Fulfill a request's portion with an array of complex type
	 * 
	 * @param complex_array_elems Array's complex type elements
	 * @param curr_param The parameter associated with the complex array
	 * 
	 * */
	private static void addComplexArrayToRequest(Iterator complex_array_elems, SOAPBodyElement curr_param) {


		while( complex_array_elems.hasNext() ){

			Object next_array_elem = complex_array_elems.next();

			// Add current array element
			if( next_array_elem instanceof org.apache.axis.message.Text){

				Text inner_txt = (Text) next_array_elem;
				try {
					curr_param.addTextNode(inner_txt.getNodeValue());
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (SOAPException e) {
					e.printStackTrace();
				}
			}
			else if( next_array_elem instanceof javax.xml.soap.SOAPElement ){


				SOAPElement next_elem_to_add_soap = (SOAPElement) next_array_elem;
				try {
					curr_param.addChildElement(next_elem_to_add_soap);
				} catch (SOAPException e) {
					e.printStackTrace();
				}
			}  
		}

		return;
	}




	/**
	 * Fulfill a request's portion with an array of simple type
	 * 
	 * @param first_element Enclosing tag of the array (probably a SOAP Response)
	 * @param curr_param Parameter associated with the array 
	 * @param curr_param_name Name of the parameter associated with the array
	 * @param operation Tag in which the array will be put
	 * 
	 * */
	private static void addSimpleArrayToRequest(SOAPElement first_element, SOAPBodyElement curr_param, final String curr_param_name, SOAPBodyElement operation ){

		if( first_element.hasChildNodes() ){
			Iterator array_elements = first_element.getChildElements();

			while( array_elements.hasNext() ){

				Object next_array_elem = array_elements.next();
				curr_param = new org.apache.axis.message.SOAPBodyElement(new QName(curr_param_name)); // can't touch this

				// Add current array element
				if( next_array_elem instanceof org.apache.axis.message.Text){


					Text inner_txt = (Text) next_array_elem;
					try {
						curr_param.addTextNode(inner_txt.getNodeValue());
						operation.addChildElement(curr_param); // Adding array element to the <Request> element
					} catch (SOAPException e) {
						e.printStackTrace();
					} 

					logger.info("Adding array element: "+ inner_txt.getNodeValue());

				}
				else if( next_array_elem instanceof javax.xml.soap.SOAPElement ){


					SOAPElement next_elem_to_add_soap = (SOAPElement) next_array_elem;
					try {
						curr_param.addChildElement(next_elem_to_add_soap);
					} catch (SOAPException e) {
						e.printStackTrace();
					}

					logger.info("Adding complex array element: "+ next_array_elem);
				}  

			}																				
		} 

		return;
	}



	/** Apply an XPath query to an XML document
	 * 
	 * @param xml_doc String with an XML content
	 * @param xpath_query query to perform
	 * 
	 * @return result of the query in a string with XML content
	 * 
	 * */
	public static String applyXPathQuery(String xml_doc, String xpath_query, QName[] namespaces, QName expectedOutputType) throws Exception {

		
		if( (xml_doc == null) || (xpath_query == null) || (namespaces == null) ){
			String errMsg = "Null reference received as argument. (xml_doc="+ xml_doc +", xpath_query="+ xpath_query +", namespaces="+ namespaces +")";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
		
		
		
		Node query_result = null;
		String result = null;


		
		logger.info("=== Applying query "+xpath_query+" on '"+xml_doc+"'\n");
		logger.debug("Namespaces are: ");
		for(int i=0; i < namespaces.length; i++){

			logger.debug(namespaces[i].getLocalPart()+" = "+ namespaces[i].getNamespaceURI());
		}
		logger.debug("End namespaces"); 



		// Get return from invoked method using XPath
		XPathFactory factory = XPathFactory.newInstance();
		XPath query = factory.newXPath();

		try {
			PersonalNamespaceContext context = new PersonalNamespaceContext();
			for(int i=0; i < namespaces.length; i++){  		
				context.addMapping(namespaces[i].getLocalPart(), namespaces[i].getNamespaceURI());
			}
			query.setNamespaceContext(context);


			InputSource source = new InputSource(new StringReader(xml_doc));


			/* Execute the query and post process the result so we return exactly what was requested */
			NodeList xpath_result = (NodeList)query.evaluate(xpath_query, source, XPathConstants.NODESET);

			logger.info("XPath query result is: "+ xpath_result); 

			// Result is a single node
			final int numElements = xpath_result.getLength();
			logger.debug("XPath result has "+ numElements +" elements");
			if( numElements <= 1 ){

				try{
					query_result = xpath_result.item(0);
				} catch(NullPointerException npe){
					String errMsg = "XPath query result is null. Check your XPath query and associated namespaces. Query: "+ xpath_query;
					logger.error(errMsg);
					throw new Exception(errMsg);
				}

				
				/* Check the validity of the query result */
				if( query_result == null ){
					String errMsg = "XPath query result is null. Check your XPath query and associated namespaces. Query: "+ xpath_query
					+ " ; XML doc: "+ xml_doc +" ; namespaces: \n";
					for(int i=0; i < namespaces.length; i++){

						errMsg += namespaces[i].getLocalPart()+" = "+ namespaces[i].getNamespaceURI() + "\n";
					}
					errMsg += "End namespaces"; 
					logger.error(errMsg);
					throw new Exception(errMsg);
				}
				

				/* In case of a simple type return, it will be a structure like <FooResponse><response>bar</response><FooResponse>
				 * but we must return only the text node, because the entire tree will be confounded with a complex type's object */
				if( query_result.hasChildNodes() ){


					final int query_result_cardinality = query_result.getChildNodes().getLength();

					

					Node first_child = query_result.getFirstChild();
					boolean isComplexType = (query_result_cardinality > 1) && (!first_child.getNodeName().matches(".*"+expectedOutputType.getLocalPart()+".*"));
					
					
					
					if( first_child.getNodeName().equals("response") ){

						// If query_result has only one child, it represents a simple type. If it has more than that, it represents a complex type
						if(  (query_result_cardinality == 1) && first_child.hasChildNodes() ){   

							query_result = first_child.getFirstChild();
						}
					}
					
					
					// Make sure the result is named by the output type
					else if (isComplexType){
						
						
						
					
						Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
						Element node = d.createElementNS(expectedOutputType.getNamespaceURI(), "nsns:"+ expectedOutputType.getLocalPart());
						
						// Copy attributes and children nodes
						NamedNodeMap attrs = query_result.getAttributes();
						for(int i=0; i < attrs.getLength(); i++){
							
							Node curr_attr = attrs.item(i);
							Node attr_clone = d.importNode(curr_attr, true);
							node.setAttributeNode((Attr) attr_clone);
						}
						NodeList children = query_result.getChildNodes();
						for(int i=0; i < children.getLength(); i++){
							
							Node curr_child = children.item(i);
							Node child_clone = d.importNode(curr_child, true);
							node.appendChild(child_clone);
						}
						
						query_result = node;  // Update resulting node
					}	// */				
					
				}

				result = ConversionUtil.Node2String(query_result);
			}

			else {
				logger.debug("\n\nThe very result of the xpath query is ");

				// Arrays are supposed to be into an enclosing tag. So, add an artificial enclosing tag to the array
				result = "<FakeResponse>";
				for(int i=0; i < numElements; i++){

					Node curr_node = xpath_result.item(i);
					result += ConversionUtil.Node2String(curr_node);

					logger.debug(ConversionUtil.Node2String(curr_node));
				}
				result += "</FakeResponse>";
				System.out.flush(); 
			}


		} catch (Exception e) {

			// Print query and its namespaces
			String info = "[applyXPathQuery] Query: '"+ xpath_query +"'   ";
			for(int i=0 ; i < namespaces.length; i++){
				info += " "+namespaces[i].getLocalPart()+'='+namespaces[i].getNamespaceURI();
			}
			e.printStackTrace();
		}

		logger.info("=== Result for query '"+xpath_query+"':\n"+result);

		return result;
	}



	/** This class represents a set of [prefix, namespace] associations */
	static class PersonalNamespaceContext implements NamespaceContext {

		private Map<String,String> map = new HashMap<String,String>();

		public void addMapping(String prefix, String uri) {
			map.put(prefix, uri);
		}
		public String getNamespaceURI(String prefix) {
			return map.get(prefix);
		}

		// This method isn't necessary for XPath processing.
		public String getPrefix(String uri) {
			throw new UnsupportedOperationException();
		}

		// This method isn't necessary for XPath processing either.
		public Iterator getPrefixes(String uri) {
			throw new UnsupportedOperationException();
		}

	}


	/** test 
	 * @throws Exception */
	public static void main(String[] args) throws Exception{


		logger.info("Begin test");


		// testing xpath on complex types
		String xml_response = "<GenerateDataResponse xmlns=\"http://service1.introduce.cagrid.org/Service1\">" +
		" <ns1:StringAndItsLenght xmlns:ns1=\"http://service1.workflow.cagrid.org/Service1\"> " +
		"<ns1:str>george teadoro gordao que falou</ns1:str>" +
		"<ns1:length>31</ns1:length>" +
		"</ns1:StringAndItsLenght>" +
		"</GenerateDataResponse>";  




		QName namespaces[] = new QName[]{ new QName("http://www.w3.org/2001/XMLSchema", "xsd"), new QName("http://service1.introduce.cagrid.org/Service1", "ns0"),
				new QName("http://service1.workflow.cagrid.org/Service1", "ns1") };
		String xpath_query = "/ns0:GenerateDataResponse/ns1:StringAndItsLenght";




		String ret = ServiceInvocationUtil.applyXPathQuery(xml_response, xpath_query, namespaces, new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLenght"));
		logger.info("Query 1: '"+ xpath_query +"'");
		logger.info("Returned 1: \n"+ret);


		Iterator elem_iter = ConversionUtil.String2SOAPElement(ret);
		if( elem_iter.hasNext() ){
			SOAPElement elem = (SOAPElement) elem_iter.next();
			elem.getPrefix();
			logger.info("Element returned is: "+ elem.getPrefix()+':'+elem.getLocalName());
		}


		xpath_query = "/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:length";
		ret = ServiceInvocationUtil.applyXPathQuery(xml_response, xpath_query, namespaces, new QName(NamespaceConstants.NSPREFIX_SCHEMA_XSD, "string"));
		logger.info("Query 2: '"+ xpath_query +"'");
		logger.info("Returned 2: \n"+ret);



		String xml_response2 = "<CheckStringAndItsLengthResponse xmlns=\"http://service5.introduce.cagrid.org/Service5\">"
			+"<response>true</response></CheckStringAndItsLengthResponse>";



		namespaces = new QName[]{ new QName("http://service5.introduce.cagrid.org/Service5", "ns5") };
		xpath_query = "/ns5:CheckStringAndItsLengthResponse";
		ret = ServiceInvocationUtil.applyXPathQuery(xml_response2, xpath_query, namespaces, new QName(NamespaceConstants.NSPREFIX_SCHEMA_XSD, "boolean"));
		logger.info("Query 2: '"+ xpath_query +"'");
		logger.info("Returned 2: \n"+ret);




		String xml_response3 = 
			"<GetArrayResponse xmlns=\"http://createarrayservice.introduce.cagrid.org/CreateArrayService\">" +
			"<response>number 0</response>" +
			"<response>number 1</response>" +
			"<response>number 2</response>" +
			"<response>number 3</response>" +
			"<response>number 4</response>" +
			"<response>number 5</response>" +
			"</GetArrayResponse>";

		namespaces = new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "abc") };
		xpath_query = "/abc:GetArrayResponse";
		ret = ServiceInvocationUtil.applyXPathQuery(xml_response3, xpath_query, namespaces, new QName(NamespaceConstants.NSPREFIX_SCHEMA_XSD, "string"));
		logger.info("Query 3: '"+ xpath_query +"'");
		logger.info("Returned 3: \n"+ret);



		logger.info("End test"); 

		/*****************************************************************************************/


		logger.info("Leaving main...");

	}





}
