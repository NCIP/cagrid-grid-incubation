package org.cagrid.workflow.manager.service.conversion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.workflow.manager.service.bpelParser.Variable;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.service.bpelParser.InvokeProperties;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;


public class WorkflowProcessLayoutConverter {

	
	/** Retrieve the description if all workflow stages from a WorkflowProcessLayout instance */
	public static WorkflowInvocationHelperDescriptor[] getWorkflowInvocationHelperDescriptors(
			final WorkflowProcessLayout workflowLayout) {
		
		List<WorkflowInvocationHelperDescriptor> descs = new ArrayList<WorkflowInvocationHelperDescriptor>();

		
		/* Instantiate the description of each workflow operation */
		InvokeProperties curr_properties = workflowLayout.getFirstService();
		boolean hasMoreOperations = (curr_properties != null);
		
		while(hasMoreOperations){
			
			String output_variable = curr_properties.getOutputVariable(); 
			boolean outputIsVoid = (output_variable == null);
			
			//DEBUG // TODO Apparently, the variable 'divResponse' is missing when we use the file gaus2.BPEL in the ManagerClient  
			System.out.println("[WorkflowProcessLayoutConverter] Current output variable is: "+ output_variable);
			
			QName operation_name = curr_properties.getOperation(); 
			
			QName outputType = null;
			if( !outputIsVoid ){
				Variable outputVariable = workflowLayout.getVariable(output_variable); 
				outputType = outputVariable.getMessageType();
			}
			String serviceURL = null; // TODO Get the service's URL from another XML file (not the main BPEL) 
			
			
			WorkflowInvocationHelperDescriptor curr_desc = new WorkflowInvocationHelperDescriptor();
			curr_desc.setOperationQName(operation_name);
			if( !outputIsVoid ){ 
				
				curr_desc.setOutputType(outputType);
			}
			curr_desc.setServiceURL(serviceURL);
			
			descs.add(curr_desc);
			
			// Move to the next service...
			curr_properties = curr_properties.getNextService();
			hasMoreOperations = (curr_properties != null);
		}
		
		
		return descs.toArray(new WorkflowInvocationHelperDescriptor[descs.size()]);
	}
}
