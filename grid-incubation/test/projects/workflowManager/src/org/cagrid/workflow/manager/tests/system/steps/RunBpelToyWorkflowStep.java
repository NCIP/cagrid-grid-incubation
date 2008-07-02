package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.util.FileUtil;
import org.globus.wsrf.NotifyCallback;

public class RunBpelToyWorkflowStep extends Step implements NotifyCallback {


	private final String bpelFileName;
	private final EndpointReferenceType managerEPR;
	private final ServiceContainer serviceContainer;


	private TimestampedStatus workflowStatus = new TimestampedStatus(Status.UNCONFIGURED, 0);
	private Lock isFinishedKey = new ReentrantLock();
	private Condition isFinishedCondition = isFinishedKey.newCondition();
	private boolean isFinished = false;
	private String operationsDescFilename;




	public RunBpelToyWorkflowStep(String bpelFileName, String operationsDescFilename, EndpointReferenceType managerEPR, ServiceContainer serviceContainer) {
		this.bpelFileName = bpelFileName;
		this.managerEPR = managerEPR;
		this.serviceContainer = serviceContainer;
		this.operationsDescFilename = operationsDescFilename;
	}

	@Override
	public void runStep() throws Throwable {


		try{
			WorkflowManagerServiceClient client = new WorkflowManagerServiceClient(this.managerEPR);
			// place client calls here if you want to use this main as a
			// test....

			String workflowBpelFileContent = null;
			String operationsDesc = null;
			try{
				URI servicesBaseURI = this.serviceContainer.getContainerBaseURI();
//				String servicesBaseURL = servicesBaseURI.toString();

				//System.out.println("container base URI: "+ servicesBaseURL);  //DEBUG

				workflowBpelFileContent = FileUtil.readTextFile(this.bpelFileName);
				operationsDesc = FileUtil.readTextFile(this.operationsDescFilename);

			}catch(IOException ioe){
				ioe.printStackTrace();
				System.exit(1);
			}
			System.out.println("File read!");

		
			System.out.println("Before create workflow");
			WorkflowManagerInstanceReference managerInstanceReference = client.createWorkflowManagerInstanceFromBpel(workflowBpelFileContent, operationsDesc ,this.managerEPR);

			System.out.println("Get reference");
			WorkflowManagerInstanceClient managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceReference.getEndpointReference());


			// Subscribe for status changes' notifications
			System.out.println("Subscribing for status changes notifications");
			managerInstanceClient.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);


			// Initiate workflow execution
			System.out.println("Initiating workflow execution");
			managerInstanceClient.start();

			// Block current thread until all FINISH notifications are received 
			this.waitUntilCompletion();

			//String[] outputs = managerInstanceClient.getOutputValues(); 
		}
		catch(Throwable t){
			t.printStackTrace();
			Assert.fail(t.getMessage());
		}


		System.out.println("End client");

	}




	public void deliver(List arg0, EndpointReferenceType epr, Object arg2) {
		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());


		//DEBUG
		PrintStream log = System.out;
//		log.println("[RunToyWorkflowStep.deliver] Received message of type "+ message_qname.getLocalPart() );//+" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


//			log.println("[RunToyWorkflowStep.deliver] Received new status value: "+ status.getStatus().toString() 
//			+ ':' + status.getTimestamp()); //DEBUG

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


	protected void waitUntilCompletion() {

		System.out.println("Waiting for workflow notification of FINISH status");


		this.isFinishedKey.lock();
		try {


			if( !this.isFinished ){

				try {

					boolean wasSignaled = this.isFinishedCondition.await(45, TimeUnit.SECONDS); 					
					if(wasSignaled) System.out.println("OK. Received notification of FINISH status. Exiting"); 
					else {
						String errMsg = "Timeout exceeded without any notification of FINISH status. Exiting";
						System.err.println(errMsg);
						System.out
						.println("[RunToyWorkflowStep.waitUntilCompletion] Status is "+ 
								this.workflowStatus.getStatus()+ ':' + this.workflowStatus.getTimestamp());
						Assert.fail(errMsg);
					}

				} catch(Throwable t){
					System.err.println("Error while waiting");
					t.printStackTrace();
				}
			}

		}
		finally {
			this.isFinishedKey.unlock();
		}

	}




}
