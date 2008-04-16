package systemTests.test.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;

public class ConfigureWorkflowInOutStep extends Step {

	private OperationInputMessageDescriptor inputConfig = null;
	private OperationOutputTransportDescriptor outputConfig = null;
	private WorkflowInvocationHelperClient service_client = null;
	
	
	
	public ConfigureWorkflowInOutStep(
			OperationInputMessageDescriptor inputConfig,
			OperationOutputTransportDescriptor outputConfig,
			WorkflowInvocationHelperClient service_client) {
		super();
		this.inputConfig = inputConfig;
		this.outputConfig = outputConfig;
		this.service_client = service_client;
	}



	@Override
	public void runStep() throws Throwable {

		System.out.println("BEGIN ConfigureWorkflowInOutStep");
		service_client.configureInput(inputConfig);
		service_client.configureOutput(outputConfig);
		System.out.println("END ConfigureWorkflowInOutStep");
	}

	
	
	private WorkflowInvocationHelperClient getService_client() {
		return service_client;
	}


	private void setService_client(WorkflowInvocationHelperClient service_client) {
		this.service_client = service_client;
	}


	
	
	private OperationInputMessageDescriptor getInputConfig() {
		return inputConfig;
	}


	private void setInputConfig(OperationInputMessageDescriptor inputConfig) {
		this.inputConfig = inputConfig;
	}


	private OperationOutputTransportDescriptor getOutputConfig() {
		return outputConfig;
	}


	private void setOutputConfig(OperationOutputTransportDescriptor outputConfig) {
		this.outputConfig = outputConfig;
	}

}
