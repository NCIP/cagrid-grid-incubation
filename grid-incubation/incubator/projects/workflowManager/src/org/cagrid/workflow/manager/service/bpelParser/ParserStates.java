package org.cagrid.workflow.manager.service.bpelParser;



public class ParserStates{
	public static final int ERROR			= -1;
	// Used inside the parser to control the parse flow
	public static final int START 			= 1;
	public static final int END			= 2;

	public static final int BEGIN_PROCESS		= 3;

	// parsing variables used to call the services
	public static final int BEGIN_VARIABLE		= 4;
	public static final int INSIDE_VARIABLE		= 5;
	public static final int END_VARIABLE		= 6;

	// parsing partnerLinks is used to callback the requester
	// of the service. In our case we do not have this section,
	// because the calls are not centralized. But we accept
	// this section and through it out
	public static final int BEGIN_PARTINER_LINKS	= 7;
	public static final int INSIDE_PARTINER_LINK	= 8;
	public static final int END_PARTINER_LINKS	= 9;

// BEGIN ORCHESTRATION SECTION	
	// sequence is the BPEL file section used to create the
	// workflow. It addresses things like wht orchestration
	// of the services and how the inputs and outputs are
	// managed between services
	public static final int BEGIN_SEQUENCE		= 10;

	// The receive tag is used to manage the XML message 
	// received from the user to the first stage of the workflow
	public static final int BEGIN_RECEIVE		= 11;
	public static final int END_RECEIVE		= 12;

	// the assign section is shared by the receive and
	// invoke tags. It is used to handle the variable
	// creating the inputs to the next stages of the workflow
	public static final int PRE_ASSIGN		= 1000;
	public static final int BEGIN_ASSIGN		= 13;
	public static final int BEGIN_COPY		= 14;
	public static final int BEGIN_FROM		= 15;
	public static final int BEFORE_TO		= 16;
	public static final int BEFORE_QUERY	= 1017;
	public static final int AFTER_QUERY		= 1018;
//	public static final int BEFORE_TO		= 1001;
	public static final int BEGIN_TO		= 17;
	public static final int END_TO			= 18;
	public static final int END_COPY		= 19;
	public static final int END_ASSIGN		= 20;

	// thag describing the service inputs
	public static final int BEGIN_INVOKE		= 21;
	public static final int END_INVOKE		= 22;	

	// this is the tag used to handle the output of our flow
	public static final int BEGIN_REPLY		= 23;
	public static final int END_REPLY		= 24;

	// just end the orchestration section
	public static final int END_SEQUENCE		= 25;

	// finalize the BPEL descriptor file
	public static final int END_PROCESS		= 26;
//END ORCHESTRATION SECTION

	public static final String PROCESS_TAG			= "process";
	public static final String VARIABLES_TAG		= "variables";
	public static final String VARIABLE_TAG			= "variable";
	public static final String PARTNER_LINKS_TAG	= "partnerLinks";
	public static final String PARTNER_LINK_TAG		= "partnerLink";
	public static final String SEQUENCE_TAG			= "sequence";
	public static final String RECEIVE_TAG			= "receive";
	public static final String ASSIGN_TAG			= "assign";
	public static final String COPY_TAG				= "copy";
	public static final String FROM_TAG				= "from";
	public static final String QUERY_TAG			= "query";
	public static final String TO_TAG				= "to";
	public static final String INVOKE_TAG			= "invoke";
	public static final String REPLY_TAG			= "reply";


	// Used to extract the used attributes from its tags
	public static final String ATTRIBUTE_NAME_TAG				= "name";
	public static final String ATTRIBUTE_MESSAGE_TYPE_TAG		= "messageType";
	public static final String ATTRIBUTE_PARTNER_LINK_TYPE_TAG 	= "partnerLinkType";
	public static final String ATTRIBUTE_PARTNER_LINK_TAG		= "partnerLink";

	public static final String ATTRIBUTE_VARIABLE_TAG			= "variable";
	public static final String ATTRIBUTE_EXPRESSION_TAG			= "expression";
	public static final String ATTRIBUTE_PART_TAG				= "part";
	public static final String ATTRIBUTE_VARIABLE_TYPE			= "type";
	public static final String ATTRIBUTE_VARIABLE_NAMESPACE		= "namespace";
	
	
	// attributes from the invoke tag
	public static final String ATTRIBUTE_TARGET_NAMESPACE_TAG	= "targetNamespace";
	public static final String ATTRIBUTE_PORT_TYPE_TAG			= "portType";
	public static final String ATTRIBUTE_OPERATION				= "operation";
	public static final String ATTRIBUTE_INPUT_VARIABLE			= "inputVariable";
	public static final String ATTRIBUTE_OUTPUT_VARIABLE		= "outputVariable";
	public static final String ATTRIBUTE_XMLNS					= "xmlns";
	
	// attributes related to the query tag
	public static final String ATTRIBUTE_QUERY_LANGUAGE			= "queryLanguage";
	public static final String ATTRIBUTE_QUERY_QUERY			= "query";
	
	// Used only to print the States in case of
	// debugging or ERROR to make it readable
	public static final String START_STR 			= "START";
	public static final String END_STR 			= "END";
	public static final String BEGIN_PROCESS_STR 		= "BEGIN_PROCESS";
	public static final String BEGIN_VARIABLE_STR		= "BEGIN_VARIABLE";
	public static final String INSIDE_VARIABLE_STR		= "INSIDE_VARIABLE";
	public static final String END_VARIABLE_STR		= "END_VARIABLE";
	public static final String BEGIN_PARTINER_LINKS_STR	= "BEGIN_PARTINER_LINKS";
	public static final String INSIDE_PARTINER_LINK_STR	= "INSIDE_PARTINER_LINK";
	public static final String END_PARTINER_LINKS_STR	= "END_PARTINER_LINKS";
	public static final String BEGIN_SEQUENCE_STR		= "BEGIN_SEQUENCE";
	public static final String BEGIN_RECEIVE_STR		= "BEGIN_RECEIVE";
	public static final String END_RECEIVE_STR		= "END_RECEIVE";
	public static final String BEGIN_ASSIGN_STR		= "BEGIN_ASSIGN";
	public static final String BEGIN_COPY_STR		= "BEGIN_COPY";
	public static final String BEGIN_FROM_STR		= "BEGIN_FROM";
	public static final String BEFORE_TO_STR		= "BEFORE_TO";
	public static final String BEGIN_TO_STR			= "BEGIN_TO";
	public static final String END_TO_STR			= "END_TO";
	public static final String END_COPY_STR			= "END_COPY";
	public static final String END_ASSIGN_STR		= "END_ASSIGN";
	public static final String BEGIN_INVOKE_STR		= "BEGIN INVOKE";
	public static final String END_INVOKE_STR		= "END_INVOKE";	
	public static final String BEGIN_REPLY_STR		= "BEGIN_REPLY";
	public static final String END_REPLY_STR		= "END_REPLY";
	public static final String END_SEQUENCE_STR		= "END_SEQUENCE";
	public static final String END_PROCESS_STR		= "END_PROCESS";
	public static final String PRE_ASSIGN_STR		= "PRE_ASSIGN";

	public static void  printError(int lastState, int actualState){
		System.out.println("Parser Error: Unexpected state. Received a state = "+getTagByState(actualState)+" after state = "+ getTagByState(lastState));	
	}
	public static String getTagByState(int state){

		String returnValue = "";
		switch (state){
			case START:
				returnValue = START_STR;
				break;

			case END:
				returnValue = END_STR;
				break;

			case BEGIN_PROCESS:
				returnValue = BEGIN_PROCESS_STR;
				break;

			case BEGIN_VARIABLE:
				returnValue=  BEGIN_VARIABLE_STR;
				break;

			case INSIDE_VARIABLE:
				returnValue = INSIDE_VARIABLE_STR;
				break;

			case END_VARIABLE:
				returnValue = END_VARIABLE_STR;
				break;

			case BEGIN_PARTINER_LINKS:
				returnValue = BEGIN_PARTINER_LINKS_STR;
				break;

			case INSIDE_PARTINER_LINK:
				returnValue = INSIDE_PARTINER_LINK_STR;
				break;

			case END_PARTINER_LINKS:
				returnValue = END_PARTINER_LINKS_STR;
				break;

			case BEGIN_SEQUENCE:
				returnValue = BEGIN_SEQUENCE_STR;
				break;

			case BEGIN_RECEIVE:
				returnValue = BEGIN_RECEIVE_STR;
				break;

			case END_RECEIVE:
				returnValue = END_RECEIVE_STR;
				break;

			case BEGIN_ASSIGN:
				returnValue = BEGIN_ASSIGN_STR;
				break;

			case BEGIN_COPY:
				returnValue = BEGIN_COPY_STR;
				break;

			case BEGIN_FROM:
				returnValue = BEGIN_FROM_STR;
				break;

			case BEFORE_TO:
				returnValue = BEFORE_TO_STR;
				break;

			case BEGIN_TO:
				returnValue = BEGIN_TO_STR;
				break;

			case END_TO:
				returnValue = END_TO_STR;
				break;

			case END_COPY:
				returnValue = END_COPY_STR;
				break;

			case END_ASSIGN:
				returnValue = END_ASSIGN_STR;
				break;

			case BEGIN_INVOKE:
				returnValue = BEGIN_INVOKE_STR;
				break;

			case END_INVOKE:
				returnValue = END_INVOKE_STR;
				break;

			case BEGIN_REPLY:
				returnValue = BEGIN_REPLY_STR;
				break;

			case END_REPLY:
				returnValue = END_REPLY_STR;
				break;

			case END_SEQUENCE:
				returnValue = END_SEQUENCE_STR;
				break;
			case END_PROCESS:
				returnValue = END_PROCESS_STR;
				break;
			case PRE_ASSIGN:
				returnValue = PRE_ASSIGN_STR;
				break;
			default:
				System.out.println("getTagByState: Unknow state = "+ state);
				returnValue = "getTagByState: Unknow state = "+ state;
				break;

		}
		return returnValue;
	}
}
