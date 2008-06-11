package org.cagrid.workflow.manager.service.conversion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.service.bpelParser.CopyOutputDirective;
import org.cagrid.workflow.manager.service.bpelParser.InvokeProperties;
import org.cagrid.workflow.manager.service.bpelParser.Variable;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;


public class WorkflowProcessLayoutConverter {


	// Layout to convert 
	private WorkflowProcessLayout layout;


	public WorkflowProcessLayoutConverter(WorkflowProcessLayout layout) {
		super();
		this.layout = layout;
	}



	/** Retrieve the description of all workflow stages from a WorkflowProcessLayout instance 
	 * @param servicesURLs */
	public List<WorkflowStageDescriptor> extractWorkflowInvocationHelperDescriptors(HashMap<String, URL> servicesURLs) {


		List<WorkflowStageDescriptor> descriptions = new ArrayList<WorkflowStageDescriptor>();


		/* Instantiate the description of each workflow operation */
		InvokeProperties curr_properties = this.layout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);
		int i = 0;

		while(hasMoreOperations){


			if(!(curr_properties.getIsReceive()) & !(curr_properties.getIsReply())){

				/** (1) Build basic description */
				String output_variable = curr_properties.getOutputVariable(); 
				boolean outputIsVoid = (output_variable == null);
				QName operation_name = curr_properties.getOperation();


				URL operation_url = servicesURLs.get(operation_name.getNamespaceURI());
				if( operation_url == null ){
					System.err.println("[getWorkflowInvocationHelperDescriptors] Operation "+ operation_name.getNamespaceURI() +" not found");
				}


				// TODO: in the client we always use the primitive type, never the response
				// type as the service outputType.
				// DEBUG
				/*System.out.println("[WorkflowProcessLayoutConverter] Current operation is "+operation_name);
				System.out.println("[WorkflowProcessLayoutConverter] Current output variable is "+ output_variable);
				System.out.println("[WorkflowProcessLayoutConverter] Current URL is "+operation_url); // */				


				WorkflowInvocationHelperDescriptor curr_basicDesc = new WorkflowInvocationHelperDescriptor();
				curr_basicDesc.setOperationQName(operation_name);

				QName outputType = null;

				if( !outputIsVoid ){
					Variable outputVariable = this.layout.getVariable(output_variable); 
					outputType = outputVariable.getMessageType(); 
					curr_basicDesc.setOutputType(outputType);
				}

				curr_basicDesc.setWorkflowID(this.layout.getName());
				curr_basicDesc.setServiceURL(operation_url.toExternalForm());

				i++;


				/** TODO (2) Create input description */
				OperationInputMessageDescriptor curr_inputDesc = new OperationInputMessageDescriptor();





				/** TODO (3) Create output description */
				System.out.println("[WorkflowProcessLayoutConverter.getWorkflowInvocationHelperDescriptors] Printing "+ 
						curr_properties.getCopyCommandSize() +" copy commands");  //DEBUG
				OperationOutputTransportDescriptor curr_outputDescriptor = new OperationOutputTransportDescriptor();
				List<OperationOutputParameterTransportDescriptor> outputs = new ArrayList<OperationOutputParameterTransportDescriptor>();
				for( int curr_dest = 0 ; curr_dest < curr_properties.getCopyCommandSize() ; curr_dest++){


					OperationOutputParameterTransportDescriptor curr_output = new OperationOutputParameterTransportDescriptor();
					CopyOutputDirective curr_copyCmd = curr_properties.getCopyCommand(curr_dest);
					curr_output.setLocationQuery(curr_copyCmd.getQuery());
					//curr_output.setParamIndex(paramIndex);
					//curr_output.setType(type);
					//curr_output.setDeliveryPolicy(DeliveryPolicy.ROUNDROBIN);
					outputs.add(curr_output);


					curr_copyCmd.printClass(); //DEBUG					
				}

				OperationOutputParameterTransportDescriptor[] paramDescriptor = outputs.toArray(new OperationOutputParameterTransportDescriptor[outputs.size()]);
				curr_outputDescriptor.setParamDescriptor(paramDescriptor);
				// END (3)



				// Add current stage's description to our list 
				WorkflowStageDescriptor curr_description = new WorkflowStageDescriptor();
				curr_description.setBasicDescription(curr_basicDesc);
				curr_description.setInputsDescription(curr_inputDesc);
				curr_description.setOutputTransportDescriptor(curr_outputDescriptor);	
				curr_description.setBpelInputVariable(curr_properties.getInputVariable());
				curr_description.setBpelOutputVariable(curr_properties.getOutputVariable());

				
				descriptions.add(curr_description);
			}


			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);
		}

		return descriptions; //.toArray(new WorkflowStageDescriptor[descriptions.size()]);
	}



	/**
	 * Retrieve all associations <service QName, service URL> present in a string.
	 * 
	 * @param servicesURLs A properties file's contents in a string format
	 * 
	 * @return The URLs of each service according to the received file
	 * */
	public static HashMap<String, URL> getServicesURL(String servicesURLs) {


		// Load the pairs <service, URL> from the received string
		Properties serviceUrlMapping = new Properties();
		InputStream propertiesStream = new ByteArrayInputStream(servicesURLs.getBytes());
		try {
			serviceUrlMapping.load(propertiesStream);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Build a map containing each association found in the received file
		HashMap<String, URL> associations = new HashMap<String, URL>();
		Enumeration<?> serviceNames = serviceUrlMapping.propertyNames();		

		while( serviceNames.hasMoreElements() ){

			// Get service QName
			String currService = serviceNames.nextElement().toString();

			// Get service URL
			String currURL = serviceUrlMapping.getProperty(currService);
			URL serviceURL = null;
			try {
				serviceURL = new URL(currURL);				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}


			//System.out.println("[WorkflowManagerServiceImpl.getServicesURL] Curr pair is: <"+ currService +", "+ serviceURL +">");  //DEBUG

			associations.put(currService, serviceURL);
		}

		return associations;
	}



	/**
	 * TODO Retrieve the workflow inputs specified in the Layout
	 * */
	public InputParameter[] getInputParameters(
			OperationInputMessageDescriptor operationInputMessageDescriptor) {

		return new InputParameter[0];
	}



	public HashMap<QName, String> getOutputVariables(){

		
		HashMap<QName, String> serviceOutput = new HashMap<QName, String>();
		
		
		InvokeProperties curr_properties = this.layout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);


		while(hasMoreOperations){

			
			serviceOutput.put(curr_properties.getOperation(), curr_properties.getOutputVariable());
			
			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);			
		}
		
		return serviceOutput;
	}


	public HashMap<String, QName> getInputVariables(){

		HashMap<String, QName> serviceInput = new HashMap<String, QName>();
		
		
		InvokeProperties curr_properties = this.layout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);


		while(hasMoreOperations){

			
			serviceInput.put(curr_properties.getInputVariable(), curr_properties.getOperation());
			
			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);			
		}
		
		return serviceInput;
	}

	
	/**
	 * Retrieve all assignment operations from the Layout instance
	 * */
	public HashMap<String, String> getAssignOperations(){
		
		HashMap<String, String> operations = new HashMap<String, String>();
		
		InvokeProperties curr_properties = this.layout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);


		while(hasMoreOperations){

			
			if( curr_properties.getCopyCommandSize() > 0 ){
				
				CopyOutputDirective curr_operation = curr_properties.getCopyCommand(0);
				String sourceVariable = curr_operation.getFromVariable();
				String destinationVariable = curr_operation.getToVariable();

				operations.put(sourceVariable, destinationVariable);
			}
			
			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);			
		}
		
		
		
		return operations;
	}



	/**
	 * Create an association of InstanceHelpers' URLs and its underlying InvocationHelpers' URLs.
	 * For now, we assume all InvocationHelpers within a container will be managed by the same
	 * InstanceHelper.
	 * 
	 * @param propertiesFileInString Properties string with the URL of each service in a workflow.
	 * 
	 * @return A map that associates each InstanceHelper's URL with the URLs of the stages it will be
	 * 	responsible for.
	 * 
	 * */
	public static Map<String,List<String>> getInstanceHelpersForInvocationHelpers(String propertiesFileInString) {


		HashMap<String, List<String> > retval = new HashMap<String, List<String>>();
		
		
		// Load the pairs <service, URL> from the received string
		Properties serviceUrlMapping = new Properties();
		InputStream propertiesStream = new ByteArrayInputStream(propertiesFileInString.getBytes());
		try {
			serviceUrlMapping.load(propertiesStream);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Build a map containing each association found in the received file
		Enumeration<?> serviceNames = serviceUrlMapping.propertyNames();		

		while( serviceNames.hasMoreElements() ){

			// Get service QName
			String currService = serviceNames.nextElement().toString();

			// Get service URL
			String currURL = serviceUrlMapping.getProperty(currService);
			URL serviceURL = null;
			try {
				serviceURL = new URL(currURL);				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			
			
			// Infer the URL of the InstanceHelper which is responsible for the current InvocationHelper
			String instanceHelperURL = serviceURL.getProtocol() + "://" + serviceURL.getHost() + ':' 
				+ serviceURL.getPort() + "/wsrf/services/cagrid/WorkflowHelper";
			
			
			// Add the current service's URL to current InstanceHelper's entry
			if( retval.containsKey(instanceHelperURL) ){
				
				List<String> old_list = retval.get(instanceHelperURL);
				old_list.add(currService);
				
				retval.put(instanceHelperURL, old_list);
			}
			else {
				List<String> new_list = new ArrayList<String>();
				new_list.add(currService);
				
				retval.put(instanceHelperURL, new_list);
			}
			
		}
		
		return retval;
	}
	
}














