package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.DeliveryPolicy;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionsDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.globus.wsrf.NotifyCallback;

public class RunToyWorkflowStep extends Step implements NotifyCallback {


	private Log logger = LogFactory.getLog(RunToyWorkflowStep.class);


	private EndpointReferenceType managerEPR;
	private TimestampedStatus workflowStatus = new TimestampedStatus(Status.UNCONFIGURED, 0);
	private Lock isFinishedKey = new ReentrantLock();
	private Condition isFinishedCondition = isFinishedKey.newCondition();
	private boolean isFinished = false;


	public RunToyWorkflowStep(EndpointReferenceType managerEPR) {
		super();
		this.managerEPR = managerEPR;
	}




	@Override
	public void runStep() throws Throwable {



		WorkflowManagerServiceClient managerClient = new WorkflowManagerServiceClient(this.managerEPR);



		WorkflowManagerInstanceDescriptor workflowDesc = new WorkflowManagerInstanceDescriptor();


		// Create description for the portion of the workflow that will be within the same container
		WorkflowPortionDescriptor[] workflowParts = new WorkflowPortionDescriptor[1];
		workflowParts[0] = new WorkflowPortionDescriptor();


		WorkflowInstanceHelperDescriptor instanceHelperDesc = new WorkflowInstanceHelperDescriptor();
		String workflowID = "test_workflow_123";
		instanceHelperDesc.setWorkflowID(workflowID);
		EndpointReferenceType managerEPR = this.managerEPR;
		instanceHelperDesc.setWorkflowManagerEPR(managerEPR);
		// instanceHelperDesc.setProxyEPR(proxyEPR); 
		workflowParts[0].setInstanceHelperDesc(instanceHelperDesc);


		// Build HelperService URL

		AttributedURI managerAddress = managerEPR.getAddress();
		String workflowHelperServiceLocation = managerAddress.getScheme() + "://" + managerAddress.getHost() + ':' +
		managerAddress.getPort() + "/wsrf/services/cagrid/WorkflowHelper";
		workflowParts[0].setWorkflowHelperServiceLocation(workflowHelperServiceLocation );



		// Build description of each stage
		WorkflowStageDescriptor[] invocationHelperDescs = new WorkflowStageDescriptor[2];



		// 2nd stage 
		invocationHelperDescs[1] = new WorkflowStageDescriptor();
		invocationHelperDescs[1].setGlobalUniqueIdentifier(2);
		WorkflowInvocationHelperDescriptor basicDescription = new WorkflowInvocationHelperDescriptor();
		basicDescription.setOperationQName(new QName("http://second.cagrid.org/Second", "ReceiveRequest"));  // Remember this is actually the request name, not the method name 
		basicDescription.setServiceURL(managerAddress.getScheme() + "://" + managerAddress.getHost() + ':' +
				managerAddress.getPort() + "/wsrf/services/cagrid/Second");
		basicDescription.setWorkflowID(workflowID);
		basicDescription.setWorkflowManagerEPR(managerEPR);
		//basicDescription.setWorkflowInvocationSecurityDescriptor(workflowInvocationSecurityDescriptor); 
		invocationHelperDescs[1].setBasicDescription(basicDescription);



		InputParameterDescriptor[] inputDesc = new InputParameterDescriptor[1]; 
		inputDesc[0] = new InputParameterDescriptor();
		inputDesc[0].setParamQName(new QName("input"));
		inputDesc[0].setParamType(new QName("string"));
		OperationInputMessageDescriptor inputsDescription = new OperationInputMessageDescriptor(inputDesc );
		invocationHelperDescs[1].setInputsDescription(inputsDescription);



		OperationOutputTransportDescriptor outputTransportDescriptor = new OperationOutputTransportDescriptor(new OperationOutputParameterTransportDescriptor[0]);
		invocationHelperDescs[1].setOutputTransportDescriptor(outputTransportDescriptor);


		workflowParts[0].setInvocationHelperDescs(invocationHelperDescs);
		workflowDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(workflowParts));



		// 1st stage 
		invocationHelperDescs[0] = new WorkflowStageDescriptor();
		invocationHelperDescs[0].setGlobalUniqueIdentifier(1);
		basicDescription = new WorkflowInvocationHelperDescriptor();
		basicDescription.setOperationQName(new QName("http://first.cagrid.org/First", "PrintRequest"));
		basicDescription.setOutputType(new QName("string"));
		basicDescription.setServiceURL(managerAddress.getScheme() + "://" + managerAddress.getHost() + ':' +
				managerAddress.getPort() + "/wsrf/services/cagrid/First");
		basicDescription.setWorkflowID(workflowID);
		basicDescription.setWorkflowManagerEPR(managerEPR);
		//basicDescription.setWorkflowInvocationSecurityDescriptor(workflowInvocationSecurityDescriptor); 
		invocationHelperDescs[0].setBasicDescription(basicDescription);



		inputDesc = new InputParameterDescriptor[0]; // No input parameters for this stage
		inputsDescription = new OperationInputMessageDescriptor(inputDesc );
		invocationHelperDescs[0].setInputsDescription(inputsDescription);



		outputTransportDescriptor = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] paramDescriptor = new OperationOutputParameterTransportDescriptor[1];
		paramDescriptor[0] = new OperationOutputParameterTransportDescriptor();
		paramDescriptor[0].setDeliveryPolicy(DeliveryPolicy.ROUNDROBIN);
		paramDescriptor[0].setDestinationGlobalUniqueIdentifier(2);
		paramDescriptor[0].setQueryNamespaces(new QName[]{ new QName("http://first.cagrid.org/First", "ns1") });
		paramDescriptor[0].setLocationQuery("/ns1:PrintResponse");
		paramDescriptor[0].setParamIndex(0);
		paramDescriptor[0].setType(new QName("string"));

		outputTransportDescriptor.setParamDescriptor(paramDescriptor );
		invocationHelperDescs[0].setOutputTransportDescriptor(outputTransportDescriptor);
		logger.info("Creating manager instance");



		WorkflowManagerInstanceReference managerInstance = managerClient.createWorkflowManagerInstanceFromObjectDescriptor(workflowDesc);

		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstance.getEndpointReference());
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// Monitor workflow status' changes
		logger.info("Subscribing to receive status changes' notifications");
		managerInstanceClient.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);


		logger.info("Starting workflow execution");
		managerInstanceClient.start();

		this.waitForCompletion();

		logger.info("Destroying manager instance's resource");
		managerInstanceClient.destroy();

		// */

	}


	private void waitForCompletion() {


		logger.info("Waiting for workflow notification of FINISH status");


		this.isFinishedKey.lock();
		try {


			if( !this.isFinished ){

				try {

					boolean wasSignaled = this.isFinishedCondition.await(45, TimeUnit.SECONDS); 					
					if(wasSignaled) logger.info("OK. Received notification of FINISH status. Exiting"); 
					else {
						String errMsg = "Timeout exceeded without any notification of FINISH status. Exiting";
						logger.error(errMsg);
						logger.info("[RunToyWorkflowStep.waitUntilCompletion] Status is "+ 
								this.workflowStatus.getStatus()+ ':' + this.workflowStatus.getTimestamp());
						Assert.fail(errMsg);
					}

				} catch(Throwable t){
					logger.error("Error while waiting");
					t.printStackTrace();
				}
			}

		}
		finally {
			this.isFinishedKey.unlock();
		}
	}



	/** Handle status changes' notifications */
	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {


		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());


		logger.debug("Received message of type "+ message_qname.getLocalPart() );//+" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.info("[RunToyWorkflowStep.deliver] Received new status value: "+ status.getStatus().toString() 
			+ ':' + status.getTimestamp()); 

			this.isFinishedKey.lock();
			try{

				boolean statusActuallyChanged = (status.getTimestamp() > this.workflowStatus.getTimestamp()) && 
				(!status.getStatus().equals(this.workflowStatus.getStatus()));


				if(statusActuallyChanged){
					this.workflowStatus = status;

					if( this.workflowStatus.getStatus().equals(Status.FINISHED) ){

						this.isFinished  = true;
						this.isFinishedCondition.signalAll();
					}
				}


			} 
			finally {
				this.isFinishedKey.unlock();
			}
		}	
	}


}
