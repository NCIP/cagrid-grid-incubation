package org.cagrid.workflow.manager.service.conversion;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.workflow.manager.service.bpelParser.Variable;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.service.bpelParser.InvokeProperties;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;


public class WorkflowProcessLayoutConverter {

	
	/** Retrieve the description of all workflow stages from a WorkflowProcessLayout instance 
	 * @param servicesURLs */
	public static WorkflowInvocationHelperDescriptor[] getWorkflowInvocationHelperDescriptors(
			final WorkflowProcessLayout workflowLayout, HashMap<String, URL> servicesURLs) {
		
		List<WorkflowInvocationHelperDescriptor> descs = new ArrayList<WorkflowInvocationHelperDescriptor>();

		
		/* Instantiate the description of each workflow operation */
		InvokeProperties curr_properties = workflowLayout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);
		int i = 0;
		
		while(hasMoreOperations){
		
			if(!(curr_properties.getIsReceive()) & !(curr_properties.getIsReply())){
				String output_variable = curr_properties.getOutputVariable(); 
				boolean outputIsVoid = (output_variable == null);
				QName operation_name = curr_properties.getOperation();
			
				
				URL operation_url = servicesURLs.get(operation_name.getNamespaceURI());
				if( operation_url == null ){
					System.err.println("[getWorkflowInvocationHelperDescriptors] Operation "+ operation_name.getNamespaceURI() +" not found");
				}
				
				
				// TODO: in the client we always use the primitive type, never the response
				// type as the service outputType.
				// TODO
				/*System.out.println("[WorkflowProcessLayoutConverter] Current operation is "+operation_name);
				System.out.println("[WorkflowProcessLayoutConverter] Current output variable is "+ output_variable);
				System.out.println("[WorkflowProcessLayoutConverter] Current URL is "+operation_url); // */
			
				QName outputType = null;
			
				if( !outputIsVoid ){
					Variable outputVariable = workflowLayout.getVariable(output_variable); 
					outputType = outputVariable.getMessageType();
				}
			
				
				// TODO Get the service's URL from another XML file (not the main BPEL)
				/*String serviceURL1 =  "http://150.164.3.188:8080/wsrf/services/cagrid/First";
				String serviceURL2 =  "http://150.164.3.188:8080/wsrf/services/cagrid/Second"; // */
				 
			
				WorkflowInvocationHelperDescriptor curr_desc = new WorkflowInvocationHelperDescriptor();
				curr_desc.setOperationQName(operation_name);

				if( !outputIsVoid ){ 
					curr_desc.setOutputType(outputType);
				}
				
				curr_desc.setWorkflowID(workflowLayout.getName());
				curr_desc.setServiceURL(operation_url.toExternalForm());
				
				i++;
				descs.add(curr_desc);
			}
			
			
			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);
		}
		
		return descs.toArray(new WorkflowInvocationHelperDescriptor[descs.size()]);
	}
}
