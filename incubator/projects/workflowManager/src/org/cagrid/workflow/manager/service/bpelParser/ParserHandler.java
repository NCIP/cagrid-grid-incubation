package org.cagrid.workflow.manager.service.bpelParser;

import javax.xml.namespace.QName;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class ParserHandler extends DefaultHandler{

	int currentState;
	int lastState;
	WorkflowProcessLayout workflowLayout;
	InvokeProperties auxInvokeCurrent = null; 
//	/
//	TODO: The variable bellow have to be replaced with the workflow manager structures
//	/
	String processName;

//	/ END: TODO
	public ParserHandler() {
		currentState = ParserStates.ERROR;
		workflowLayout = new WorkflowProcessLayout();
	}
	WorkflowProcessLayout getLayout(){
		return workflowLayout;	
	}
	/**

	 * Receive notification of the beginning of the document.

	 * @throws SAXException

	 */

	public void startDocument () throws SAXException

	{
		System.out.println("Start Document");
		currentState = ParserStates.START;
		// Write your application specific logic

	}

	/**

	 * Receive notification of the end of the document.

	 * @throws SAXException

	 */

	public void endDocument() throws SAXException {

		// Write your application specific logic

	}

	/**

	 * Receive notification of the start of an element.

	 * @param uri

	 * @param localName

	 * @param qName

	 * @param attributes

	 * @throws SAXException

	 */

	public void startElement (String uri, String localName,	String qName, Attributes attributes) throws SAXException{

		switch(currentState){

		case ParserStates.START:
			if(qName == ParserStates.PROCESS_TAG){
//				System.out.println("START");
				if(attributes != null){
//					System.out.println("attributes.Length : "+attributes.getLength());
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG){
							workflowLayout.setName(attributes.getValue(x));
//							System.out.println("setting process name");
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_TARGET_NAMESPACE_TAG){
								workflowLayout.setTargetNamespace(attributes.getValue(x));
//								System.out.println("setting process name");
							}else{
								boolean isNamespace = attributes.getQName(x).startsWith(ParserStates.ATTRIBUTE_XMLNS);

								if(isNamespace == true){
//									System.out.println("PROCESS_QNAME = "+attributes.getQName(x));
//									System.out.println("substring = "+attributes.getQName(x).substring( ParserStates.ATTRIBUTE_XMLNS.length(), attributes.getQName(x).length()));
									try{
										workflowLayout.setEndpoint(attributes.getQName(x).substring( ParserStates.ATTRIBUTE_XMLNS.length(), attributes.getQName(x).length()),attributes.getValue(x));
									}catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}

								}else{
									System.out.println("Warnning: Tag process, unknow attribute = "+attributes.getQName(x)+" value = "+attributes.getValue(x));
								}
							}
						}	
					}
				}
				lastState = currentState;
				currentState = ParserStates.BEGIN_PROCESS;
			}	
			break;

		case ParserStates.BEGIN_PROCESS:
			if(qName == ParserStates.VARIABLES_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_VARIABLE;	
			}else{
				// Beginning parser PartnerLinks
				if(qName == ParserStates.PARTNER_LINKS_TAG){
					lastState = currentState;
					currentState = ParserStates.INSIDE_PARTINER_LINK;
				}else{
					if(qName == ParserStates.SEQUENCE_TAG){
						lastState = currentState;
						currentState = ParserStates.BEGIN_SEQUENCE;

					}else{
						ParserStates.printError(currentState, ParserStates.BEGIN_PROCESS);	
						System.exit(0);
					}
				}	 
			}

			break;
		case ParserStates.BEGIN_VARIABLE:
			Variable auxVariable = new Variable();

			if(qName == ParserStates.VARIABLE_TAG){
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
							auxVariable.setName(attributes.getValue(x));
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_MESSAGE_TYPE_TAG){

								// Resolve the namespace and obtain a fully qualified type before setting the message type
								String attribute_value = attributes.getValue(x);
								QName message_type = ParserHandler.getQName(workflowLayout, attribute_value);
								auxVariable.setMessageType(message_type);
								System.out.println("Message type is: "+message_type);

							}	
						}
					}
				}	
			}
			try{
				workflowLayout.setVariable(auxVariable);
			}catch(Exception e){
				e.printStackTrace();	
			}

			lastState = currentState;
			currentState = ParserStates.INSIDE_VARIABLE;

			break;

		case ParserStates.INSIDE_VARIABLE:
			Variable auxVariableInside = new Variable();

			if(qName == ParserStates.VARIABLE_TAG){
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
							auxVariableInside.setName(attributes.getValue(x));

							//							System.out.print("variable: "+  attributes.getValue(x));
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_MESSAGE_TYPE_TAG){
								// Resolve the namespace and obtain a fully qualified type before setting the message type
								String attribute_value = attributes.getValue(x);
								QName message_type = ParserHandler.getQName(workflowLayout, attribute_value);
								auxVariableInside.setMessageType(message_type);
								System.out.println("Message type is: "+message_type);
							}	
						}
					}
				}	
			}else{
				/// error

			}
			try{
				workflowLayout.setVariable(auxVariableInside);
			}catch(Exception e){
				e.printStackTrace();	
			}

			lastState = currentState;
			currentState = ParserStates.INSIDE_VARIABLE;

			break;
		case ParserStates.INSIDE_PARTINER_LINK:
			WorkflowProcessLayout.PartnerLink auxPartnerLink = workflowLayout.new PartnerLink();

			if(qName == ParserStates.PARTNER_LINK_TAG){
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
							auxPartnerLink.setName(attributes.getValue(x));
//							System.out.print("PartnerLink: "+  attributes.getValue(x));
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PARTNER_LINK_TYPE_TAG){
								
								QName plink_qname = ParserHandler.getQName(workflowLayout, attributes.getValue(x));
								auxPartnerLink.setPartnerLinkType(plink_qname);
//								System.out.println(" value: "+ attributes.getValue(x));
							}	
						}
					}
				}	
			}else{
				///// ERROR	

			}
			try{
				workflowLayout.setPartnerLink(auxPartnerLink);
			}catch(Exception e){
				e.printStackTrace();
//				throw new Exception(e);
			}	

			lastState = currentState;
			// current state still the same

			break;

		case ParserStates.BEGIN_SEQUENCE:
			InvokeProperties auxInvoke = new InvokeProperties();
			auxInvoke.setIsReceive();

			if(lastState == ParserStates.BEGIN_PROCESS && qName == ParserStates.RECEIVE_TAG){
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
							auxInvoke.setName(attributes.getValue(x));
						}
						if(attributes.getQName(x) == ParserStates.PARTNER_LINK_TAG){
							auxInvoke.setPartnerLink(attributes.getValue(x));

						}	
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PORT_TYPE_TAG){
							// Resolve the namespace and obtain a fully qualified type before setting the port type
							String attribute_value = attributes.getValue(x);
							QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
							auxInvoke.setPortType(operation_qname);
							System.out.println("Port type is: "+operation_qname);
						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_OPERATION){
							// Resolve the namespace and obtain a fully qualified type before setting the message type
							String attribute_value = attributes.getValue(x);
							QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
							auxInvoke.setOperation(operation_qname);
							System.out.println("Message type is: "+operation_qname);

						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_VARIABLE_TAG){
							auxInvoke.setInputVariable(attributes.getValue(x));
						}
					}
				}
				lastState = currentState;
				currentState = ParserStates.PRE_ASSIGN;
			}else{
				System.out.println("Error");
				// ERROR	
			}

			// I will not insert it now, because I have to know if it
			// has any Assign tag
			auxInvokeCurrent = auxInvoke;

			break;

		case ParserStates.PRE_ASSIGN:
			if(qName == ParserStates.ASSIGN_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_ASSIGN;
			}else{
				System.out.println("qName = "+qName);
				ParserStates.printError(lastState, ParserStates.PRE_ASSIGN);	
				System.exit(0);

			}
			break;
		case ParserStates.END_COPY:
			if(qName == ParserStates.COPY_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_COPY;
				System.out.println("END COPY: BEGIN COPY_TAG");
			}else{
				// ERROR	

			}

		case ParserStates.BEGIN_ASSIGN:
			if(qName == ParserStates.COPY_TAG){
				System.out.println("lau");
				lastState = currentState;
				currentState = ParserStates.BEGIN_COPY;
			}else{
				ParserStates.printError(lastState, ParserStates.BEGIN_ASSIGN);	
				System.exit(0);
			}

			break;
		case ParserStates.BEGIN_COPY:
			if(qName == ParserStates.FROM_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_FROM;  
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_VARIABLE_TAG ){
//							System.out.print("variable : "+  attributes.getValue(x));
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PART_TAG){
//								System.out.println(" part: "+ attributes.getValue(x));
							}	
						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_EXPRESSION_TAG ){
//							System.out.print("expression : "+  attributes.getValue(x));

						}


					}
				}	

			}else{
				ParserStates.printError(lastState, ParserStates.BEGIN_COPY);	
			}
			break;

		case ParserStates.BEFORE_TO:
			if(qName == ParserStates.TO_TAG){
				lastState =  currentState;
				currentState = ParserStates.END_TO;

				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_VARIABLE_TAG ){
							System.out.print("variable : "+  attributes.getValue(x));
						}else{
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PART_TAG){
								System.out.println(" part: "+ attributes.getValue(x));
							}	
						}
					}
				}	


			}

			break;
		case ParserStates.END_ASSIGN:
			workflowLayout.addInvokeProprieties(auxInvokeCurrent);

			if(qName == ParserStates.INVOKE_TAG){
				auxInvoke = new InvokeProperties();

				lastState = currentState;
				currentState = ParserStates.BEGIN_INVOKE;
				System.out.println("BEGIN_INVOKE");
				if(attributes != null){
					for(int x = 0; x < attributes.getLength(); x++){
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
							auxInvoke.setName(attributes.getValue(x));
						}
						if(attributes.getQName(x) == ParserStates.PARTNER_LINK_TAG){
							auxInvoke.setPartnerLink(attributes.getValue(x));

						}	
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PORT_TYPE_TAG){
							// Resolve the namespace and obtain a fully qualified type before setting the port type
							String attribute_value = attributes.getValue(x);
							QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
							auxInvoke.setPortType(operation_qname);
							System.out.println("Port type is: "+operation_qname);
						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_OPERATION){

							// Resolve the namespace and obtain a fully qualified type before setting the operation name
							String attribute_value = attributes.getValue(x);
							QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
							auxInvoke.setOperation(operation_qname);
							System.out.println("Operation name: "+operation_qname);
						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_INPUT_VARIABLE){
							auxInvoke.setInputVariable(attributes.getValue(x));
						}
						if(attributes.getQName(x) == ParserStates.ATTRIBUTE_OUTPUT_VARIABLE){
							auxInvoke.setOutputVariable(attributes.getValue(x));
						}


					}
				}	
				auxInvokeCurrent = auxInvoke;

			}else{
				/// lalalal	

			}
			// I will not insert it now, because I have to know if it
			// has any Assign tag
			break;

		case ParserStates.END_INVOKE:

			if(qName == ParserStates.ASSIGN_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_ASSIGN;
			}else{
				if(qName == ParserStates.REPLY_TAG){
					workflowLayout.addInvokeProprieties(auxInvokeCurrent);

					InvokeProperties auxInvokeEnd = new InvokeProperties();
					auxInvokeEnd.setIsReply();
//					System.out.println("BEGIN_REPLY");
					if(attributes != null){
						for(int x = 0; x < attributes.getLength(); x++){
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_NAME_TAG ){
								auxInvokeEnd.setName(attributes.getValue(x));
							}
							if(attributes.getQName(x) == ParserStates.PARTNER_LINK_TAG){
								auxInvokeEnd.setPartnerLink(attributes.getValue(x));

							}	
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_PORT_TYPE_TAG){

								// Resolve the namespace and obtain a fully qualified type before setting the port type
								String attribute_value = attributes.getValue(x);
								QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
								auxInvokeEnd.setPortType(operation_qname);
								System.out.println("Port type is: "+operation_qname);
							}
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_OPERATION){

								// Resolve the namespace and obtain a fully qualified type before setting the operation name
								String attribute_value = attributes.getValue(x);
								QName operation_qname = ParserHandler.getQName(workflowLayout, attribute_value);
								auxInvokeEnd.setOperation(operation_qname);
								System.out.println("Operation name: "+operation_qname);
							}
							if(attributes.getQName(x) == ParserStates.ATTRIBUTE_VARIABLE_TAG){
								auxInvokeEnd.setOutputVariable(attributes.getValue(x));
							}				
						}
					}
					lastState = currentState;
					currentState = ParserStates.BEGIN_REPLY;
					workflowLayout.addInvokeProprieties(auxInvokeEnd);
				}else{
					/// ERROR	


				}
			}
			break;
		default:
			System.out.println("Unknown state: "+currentState);
//		currentState = ParserStates.ERROR;
		break;
		}
	}


	/** Resolve a type reference to a QName according to the mapping from a WorkflowProcessLayout instance */
	private static QName getQName(WorkflowProcessLayout workflowLayout2,
			String value) {
		String namespace; 
		// If qualified by a namespace prefix, retrieve the actual namespace. Otherwise, assume retrieve 'targetNamespace'.
		if( value.contains(":") ){
			String namespace_key = value.substring(0, value.indexOf(':'));
			if( namespace_key.equals("tns") ){
				namespace = workflowLayout2.getTargetNamespace();
			}
			else{
				namespace = workflowLayout2.getServiceNamespace(namespace_key);
			}
		}
		else {
			namespace = workflowLayout2.getTargetNamespace();
		}

		QName message_type = new QName(namespace, value.substring(value.indexOf(':')+1));

		return message_type;
	}


	/**

	 * Receive notification of the end of an element.

	 * @param uri

	 * @param localName

	 * @param qName

	 * @throws SAXException

	 */

	public void endElement (String uri, String localName, String qName)

	throws SAXException

	{
		switch(currentState){

		case ParserStates.INSIDE_VARIABLE:
			if(qName == ParserStates.VARIABLES_TAG){
				lastState = currentState;		
				currentState = ParserStates.BEGIN_PROCESS;
			}	
			break;
		case ParserStates.INSIDE_PARTINER_LINK:
			if(qName == ParserStates.PARTNER_LINKS_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_PROCESS;
			}else{
				// ERROR	
			}
			break;
		case ParserStates.BEGIN_FROM:
			if (qName == ParserStates.FROM_TAG){
				lastState = currentState;
				currentState = ParserStates.BEFORE_TO;
			}else{
				//ERROR	
			}
			break;

		case ParserStates.BEGIN_TO:
			if (qName ==  ParserStates.TO_TAG){
				lastState = currentState;
				currentState = ParserStates.END_TO;
				System.out.println("END_TO");			
			}else{
				/// ERROR	
			}
			break;
		case ParserStates.END_TO:
			if(qName ==  ParserStates.COPY_TAG){
				lastState =  currentState;
				currentState = ParserStates.END_COPY;

			}else{

				// ERROR	
			}
			break;
		case ParserStates.END_COPY:
			if(qName == ParserStates.ASSIGN_TAG){
				lastState =  currentState;
				currentState = ParserStates.END_ASSIGN;

			}else{
				// ERROR	
			}
			break;
		case ParserStates.BEFORE_TO:
			if(qName == ParserStates.TO_TAG){
				lastState = currentState;
				currentState = ParserStates.BEGIN_PROCESS;
			}else{

			}
			break;

		case ParserStates.END_ASSIGN:

			System.out.println("END_ASSIGN");
			break;
		case ParserStates.BEGIN_INVOKE:
			if(qName ==  ParserStates.INVOKE_TAG){
				lastState = currentState;
				currentState = ParserStates.END_INVOKE;
			}else{
				// ERROR	
			}
			break;

		case ParserStates.BEGIN_REPLY:
			if(qName ==  ParserStates.REPLY_TAG){
				lastState =  currentState;
				currentState = ParserStates.END_REPLY;	
			}else{
				// ERROR
			}
			break;
		case ParserStates.END_REPLY:
			if(qName == ParserStates.SEQUENCE_TAG){
				lastState =  currentState;
				currentState = ParserStates.END_SEQUENCE;
			}else{
				// ERROR	
			}
			break;
		case ParserStates.END_SEQUENCE:
			if(qName == ParserStates.PROCESS_TAG){
				lastState = currentState;
				currentState = ParserStates.END_PROCESS;
			}else{
				// ERROR	
			}
			break;
		default:
			System.out.println("endElement: Unknown state: "+currentState);
		}

	}

	/**

	 * Receive notification of character data inside an element.

	 * @param ch

	 * @param start

	 * @param length

	 * @throws SAXException

	 */

	public void characters (char ch[], int start, int length) throws SAXException {
//		System.out.println("start: "+start+" - length: "+length);
//		System.out.print("characters : ");
//		for(int x = 0; x< length; x++){
//		/				System.out.print(ch[start+x]);	
//		}
//		System.out.println();
	}

}

