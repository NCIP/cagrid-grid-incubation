package org.cagrid.workflow.manager.tests.system.steps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.OutputReady;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.util.WorkflowDescriptorParser;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.container.ContainerException;

public class RunUnsecureWorkflowStepFromXML extends RunUnsecureWorkflowsStep {



	private static final String CONTAINERBASEPLACEHOLDER = "CONTAINERBASE";


	private static Log logger = LogFactory.getLog(RunUnsecureWorkflowStepFromXML.class);

	private File sampleDescriptorsDirectory;


	public RunUnsecureWorkflowStepFromXML(EndpointReferenceType manager_epr,
			String containerBaseURL, File sampleXMLDirectory) {
		super(manager_epr, containerBaseURL);  // Getting rid of the final slash so we don't have double slashes in some service's path
		this.containerBaseURL = containerBaseURL.replaceAll("/$", "");
		this.sampleDescriptorsDirectory = sampleXMLDirectory;
	}


	@Override
	public void runStep() throws Throwable {


		/** Create XML description for each one of the workflows: Fan in-Fan out, simple-type array forwarding, complex-type array forwarding,
		 * simple-type array streaming, complex-type array streaming  
		 * */
		logger.info("-x-x-x- BEGIN NON-SECURE WORKFLOWS TESTS USING XML DESCRIPTORS -x-x-x-");
		

		WorkflowManagerServiceClient wf_manager = new WorkflowManagerServiceClient(this.manager_epr); 

		try{

			/** Test streaming ability **/
			logger.info("Streaming tests");
			logger.info("Complex array streaming");
			runComplexArrayStreaming(wf_manager); 
			logger.info("OK"); // */

			logger.info("Simple array streaming");
			runSimpleArrayStreaming(wf_manager);
			logger.info("OK");
			logger.info("END streaming tests"); // */


			/** Test array handling **/
			logger.info("Array handling tests");
			logger.info("Simple arrays");
			runSimpleArrayTest(wf_manager);
			logger.info("OK");


			logger.info("Complex arrays");
			runComplexArrayTest(wf_manager);
			logger.info("OK");
			logger.info("END array handling tests"); // */


			/** Fan-in and Fan-out test **/
			logger.info("Fan-in Fan-out test");
			runFaninFanOutTest(wf_manager);
			logger.info("OK");
			logger.info("END Fan-in Fan-out test"); // */


		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			t.printStackTrace();
		}
	}


	
	private void runTestFromXML(WorkflowManagerServiceClient wf_manager, String xmlBasename) throws RemoteException{
		
		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + xmlBasename;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		File wfDescriptor = new File(wfDescriptorFilename);
		String wfXmlDescriptor = readFileToString(wfDescriptor);


		// Replace the URLs in the descriptor with the actual container URL	
		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);


		// Create and execute the workflow
		WorkflowManagerInstanceClient managerInstanceClient = null;
		Lock asynchronousCallbackLock = null;
		String clientID = null;
		try {

			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());


			// Initialize synchronization variables so we can handle future notifications of execution end
			clientID = managerInstanceClient.getEPRString();
			if(clientID == null){
				throw new RemoteException("Unable to retrieve EPR String");
			}
			this.asynchronousStartCallbackReceived.put(clientID, false);
			asynchronousCallbackLock = new ReentrantLock();
			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());  


			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (ContainerException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} 


		managerInstanceClient.start();  // Start asynchronous execution

		// Wait for asynchornous method callback
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
				logger.info("Blocking on condition variable");
				currWorkflowCondition.await();  // Blocks until execution is finished
				logger.info("Workflow is finished");
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}

		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow

		// Print retrieved outputs
		if(outputs != null){
			logger.info("Printing workflow outputs");
			for(int i=0; i < outputs.length; i++){

				logger.info("Output #"+ i +" is "+ outputs[i]);
			}
			logger.info("End outputs");
		}

		return;
		
	}
	
	

	@Override
	protected void runComplexArrayStreaming(WorkflowManagerServiceClient wf_manager) throws RemoteException {


		runTestFromXML(wf_manager, "UnsecureWorkflowComplexArrayStreaming.xml");
		
//		// Read descriptor into a string
//		String wfDescriptorFilename = null;
//		try {
//			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + "UnsecureWorkflowComplexArrayStreaming.xml";
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		File wfDescriptor = new File(wfDescriptorFilename);
//		String wfXmlDescriptor = readFileToString(wfDescriptor);
//
//
//		// Replace the URLs in the descriptor with the actual container URL	
//		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//
//
//		// Create and execute the workflow
//		WorkflowManagerInstanceClient managerInstanceClient = null;
//		ReentrantLock asynchronousCallbackLock = null;
//		String clientID = null;
//		try {
//
//			WorkflowManagerInstanceDescriptor wfDesc = new org.cagrid.workflow.manager.util.WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
//			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
//			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
//
//			// Initialize synchronization variables so we can handle future notifications of execution end
//			clientID = managerInstanceClient.getEPRString();
//			this.asynchronousStartCallbackReceived.put(clientID, false);
//			asynchronousCallbackLock = new ReentrantLock();
//			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
//			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition()); 
//
//
//			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
//
//		} catch (MalformedURIException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}  catch (ContainerException e) {
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		} catch (Exception e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		}
//
//		managerInstanceClient.start();  // Start asynchronous execution 
//
//		// Wait for asynchornous method callback
//		logger.info("Waiting for workflow to finish");
//		asynchronousCallbackLock.lock();
//		try {
//
//			if(!this.asynchronousStartCallbackReceived.get(clientID)){
//
//				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				logger.info("Blocking on condition variable");
//				currWorkflowCondition.await();  // Blocks until execution is finished				
//			}
//			logger.info("Workflow is finished");
//
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} finally {
//			asynchronousCallbackLock.unlock();
//		}
//
//		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow


	}


	@Override
	protected void runSimpleArrayStreaming(
			WorkflowManagerServiceClient wf_manager) throws RemoteException,
			MalformedURIException {

		runTestFromXML(wf_manager, "UnsecureWorkflowSimpleArrayStreaming.xml");
		
//		// Read descriptor into a string
//		String wfDescriptorFilename = null;
//		try {
//			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + "UnsecureWorkflowSimpleArrayStreaming.xml";
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		File wfDescriptor = new File(wfDescriptorFilename);
//		String wfXmlDescriptor = readFileToString(wfDescriptor);
//
//
//		// Replace the URLs in the descriptor with the actual container URL	
//		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//
//
//
//
//		// Create and execute the workflow
//		WorkflowManagerInstanceClient managerInstanceClient = null;
//		String clientID = null;
//		Lock asynchronousCallbackLock = null;
//		try {
//
//			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
//			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
//			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
//
//
//			// Initialize synchronization variables so we can handle future notifications of execution end
//			clientID = managerInstanceClient.getEPRString();
//			this.asynchronousStartCallbackReceived.put(clientID, false);
//			asynchronousCallbackLock = new ReentrantLock();
//			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
//			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());  
//
//
//			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
//
//		} catch (MalformedURIException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (ContainerException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} 
//
//
//		managerInstanceClient.start();  // Start asynchronous execution
//
//		// Wait for asynchornous method callback
//		asynchronousCallbackLock.lock();
//		try {
//
//			if(!this.asynchronousStartCallbackReceived.get(clientID)){
//
//				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				logger.info("Blocking on condition variable");
//				currWorkflowCondition.await();  // Blocks until execution is finished
//				logger.info("Workflow is finished");
//			}
//
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} finally {
//			asynchronousCallbackLock.unlock();
//		}
//
//		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow
//
//		// Print retrieved outputs
//		if(outputs != null){
//			logger.info("Printing workflow outputs");
//			for(int i=0; i < outputs.length; i++){
//
//				logger.info("Output #"+ i +" is "+ outputs[i]);
//			}
//			logger.info("End outputs");
//		}
//
//		return;
	}


	@Override
	protected void runSimpleArrayTest(WorkflowManagerServiceClient wf_manager)
	throws RemoteException {

		runTestFromXML(wf_manager, "UnsecureWorkflowSimpleArray.xml");

//		// Read descriptor into a string
//		String wfDescriptorFilename = null;
//		try {
//			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + "UnsecureWorkflowSimpleArray.xml";
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		File wfDescriptor = new File(wfDescriptorFilename);
//		String wfXmlDescriptor = readFileToString(wfDescriptor);
//
//
//		// Replace the URLs in the descriptor with the actual container URL	
//		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//
//
//		// Create and execute the workflow
//		WorkflowManagerInstanceClient managerInstanceClient = null;
//		Lock asynchronousCallbackLock = null;
//		String clientID = null;
//		try {
//
//			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
//			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
//			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
//
//
//			// Initialize synchronization variables so we can handle future notifications of execution end
//			clientID = managerInstanceClient.getEPRString();
//			if(clientID == null){
//				throw new RemoteException("Unable to retrieve EPR String");
//			}
//			this.asynchronousStartCallbackReceived.put(clientID, false);
//			asynchronousCallbackLock = new ReentrantLock();
//			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
//			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());  
//
//
//			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
//
//		} catch (MalformedURIException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (ContainerException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} 
//
//
//		managerInstanceClient.start();  // Start asynchronous execution
//
//		// Wait for asynchornous method callback
//		asynchronousCallbackLock.lock();
//		try {
//
//			if(!this.asynchronousStartCallbackReceived.get(clientID)){
//
//				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				logger.info("Blocking on condition variable");
//				currWorkflowCondition.await();  // Blocks until execution is finished
//				logger.info("Workflow is finished");
//			}
//
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} finally {
//			asynchronousCallbackLock.unlock();
//		}
//
//		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow
//
//		// Print retrieved outputs
//		if(outputs != null){
//			logger.info("Printing workflow outputs");
//			for(int i=0; i < outputs.length; i++){
//
//				logger.info("Output #"+ i +" is "+ outputs[i]);
//			}
//			logger.info("End outputs");
//		}
//
//		return;

	}


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunUnsecureWorkflowsStep#runComplexArrayTest(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient)
	 */
	@Override
	protected void runComplexArrayTest(WorkflowManagerServiceClient wf_manager)
	throws RemoteException {

		runTestFromXML(wf_manager, "UnsecureWorkflowComplexArray.xml");

//		// Read descriptor into a string
//		String wfDescriptorFilename = null;
//		try {
//			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + "UnsecureWorkflowComplexArray.xml";
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		File wfDescriptor = new File(wfDescriptorFilename);
//		String wfXmlDescriptor = readFileToString(wfDescriptor);
//
//
//		// Replace the URLs in the descriptor with the actual container URL	
//		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//
//
//		// Create and execute the workflow
//		WorkflowManagerInstanceClient managerInstanceClient = null;
//		String clientID = null;
//		ReentrantLock asynchronousCallbackLock = null;
//		try {
//
//			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
//			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
//			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
//
//			// Initialize synchronization variables so we can handle future notifications of execution end
//			clientID = managerInstanceClient.getEPRString();
//			this.asynchronousStartCallbackReceived.put(clientID, false);
//			asynchronousCallbackLock = new ReentrantLock();
//			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
//			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition()); 
//
//
//			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
//
//		} catch (MalformedURIException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (ContainerException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//
//		managerInstanceClient.start();  // Start asynchronous execution
//
//		// Wait for asynchornous method callback
//		asynchronousCallbackLock.lock();
//		try {
//
//			if(!this.asynchronousStartCallbackReceived.get(clientID)){
//
//				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				logger.info("Blocking on condition variable");
//				currWorkflowCondition.await();  // Blocks until execution is finished
//				logger.info("Workflow is finished");
//			}
//
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} finally {
//			asynchronousCallbackLock.unlock();
//		}
//
//		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow
//
//		// Print retrieved outputs
//		if(outputs != null){
//			logger.info("Printing workflow outputs");
//			for(int i=0; i < outputs.length; i++){
//
//				logger.info("Output #"+ i +" is "+ outputs[i]);
//			}
//			logger.info("End outputs");
//		}
//
//		return;
	}


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunUnsecureWorkflowsStep#runFaninFanOutTest(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient, int)
	 */
	@Override
	protected void runFaninFanOutTest(WorkflowManagerServiceClient wf_manager) throws RemoteException {

		runTestFromXML(wf_manager, "UnsecureFanInFanOutWorkflow.xml");

//		// Read descriptor into a string
//		String wfDescriptorFilename = null;
//		try {
//			wfDescriptorFilename = this.sampleDescriptorsDirectory.getCanonicalFile() + File.separator + "UnsecureFanInFanOutWorkflow.xml";
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		File wfDescriptor = new File(wfDescriptorFilename);
//		String wfXmlDescriptor = readFileToString(wfDescriptor);
//
//
//		// Replace the URLs in the descriptor with the actual container URL	
//		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//
//
//		// Create and execute the workflow
//		WorkflowManagerInstanceClient managerInstanceClient = null;
//		String clientID = null;
//		ReentrantLock asynchronousCallbackLock = null;
//		try {
//
//			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
//			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
//			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
//
//			// Initialize synchronization variables so we can handle future notifications of execution end
//			clientID = managerInstanceClient.getEPRString();
//			this.asynchronousStartCallbackReceived.put(clientID, false);
//			asynchronousCallbackLock = new ReentrantLock();
//			this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
//			this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition()); 
//
//
//			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
//
//		} catch (MalformedURIException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} catch (ContainerException e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//
//		managerInstanceClient.start();  // Start asynchronous execution
//
//		// Wait for asynchornous method callback
//		asynchronousCallbackLock.lock();
//		try {
//
//			if(!this.asynchronousStartCallbackReceived.get(clientID)){
//
//				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				logger.info("Blocking on condition variable");
//				currWorkflowCondition.await();  // Blocks until execution is finished
//				logger.info("Workflow is finished");
//			}
//
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage() ,e);
//			e.printStackTrace();
//		} finally {
//			asynchronousCallbackLock.unlock();
//		}
//
//
//		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow
//
//		// Print retrieved outputs
//		if(outputs != null){
//			logger.info("Printing workflow outputs");
//			for(int i=0; i < outputs.length; i++){
//
//				logger.info("Output #"+ i +" is "+ outputs[i]);
//			}
//			logger.info("End outputs");
//		}
//
//		return;
	}


	/** Store the contents of a file in a String */
	private String readFileToString(File wfDescriptor) {


		int fileLenght = (int) wfDescriptor.length();
		String retval = null;

		try {
			FileReader reader = new FileReader(wfDescriptor);
			char[] cbuf = new char[fileLenght];
			reader.read(cbuf);

			retval = new String(cbuf);

		} catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}

		return retval;
	}

	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		boolean isOutputReady = message_qname.equals(OutputReady.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}   


		logger.info("[CreateTestWorkflowsStep] Received message of type "+ message_qname.getLocalPart() +" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.info("Received new status value: "+ status.getStatus().toString() + ':' + status.getTimestamp());

			this.isFinishedKey.lock();
			try{

				boolean statusActuallyChanged = false;
				if( this.stageStatus.containsKey(stageKey) ){


					TimestampedStatus curr_status = this.stageStatus.get(stageKey);
					statusActuallyChanged = ( curr_status.getTimestamp() < status.getTimestamp() ); 										

					if(statusActuallyChanged){

						this.stageStatus.remove(stageKey);
						this.stageStatus.put(stageKey, status);
					}

				}
				else logger.warn("Unrecognized stage notified status change: "+ stageKey);


				if( statusActuallyChanged && (status.getStatus().equals(Status.FINISHED) || status.getStatus().equals(Status.ERROR)) ){


					this.isFinished  = this.hasFinished(); 

					if(this.isFinished){

						this.isFinishedCondition.signalAll();
						Assert.assertFalse(this.stageStatus.containsValue(Status.ERROR));

						// Destroy ManagerInstance resources
						Iterator<EndpointReferenceType> instances_iter = this.managerInstances.iterator();
						while( instances_iter.hasNext() ){

							EndpointReferenceType curr_managerInstance = instances_iter.next();
							WorkflowManagerInstanceClient curr_client;
							try {
								curr_client = new WorkflowManagerInstanceClient(curr_managerInstance);
								//curr_client.destroy();

							} catch (MalformedURIException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							} catch (RemoteException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							}
						}
					}
				}
			}
			finally {
				this.isFinishedKey.unlock();
			}
		}

		// Handle callbacks received from just finished workflows
		else if(isOutputReady){

			stageKey = null;
			try {
				stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();   

				if(stageKey == null){
					logger.error("[RunSecureWorkflowsStep::deliver] Unable to retrieve stageKey");
				}

			} catch (RemoteException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			} catch (MalformedURIException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			} 


			OutputReady callback = null;
			try {
				callback = (OutputReady) actual_property.getValueAsType(message_qname, OutputReady.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Boolean notificationValue = new Boolean(callback.equals(OutputReady.TRUE));
			logger.info("Received new OutputReady: "+ callback.getValue() + " from "+ stageKey); 
			
			// Store/Update the value stored internally for the current InvocationHelper
			Lock mutex = this.asynchronousStartLock.get(stageKey);
			mutex.lock();
			try {


				this.asynchronousStartCallbackReceived.put(stageKey, notificationValue);
				

				// If the execution is finished, report the user
				boolean allCallbacksReceived = !this.asynchronousStartCallbackReceived.containsValue(Boolean.FALSE);
				if(allCallbacksReceived){

					System.out.println("[RunSecureWorkflowsStep::deliver] All callbacks received. Execution is finished."); 
					Condition workflowFinished = this.asynchronousStartCondition.get(stageKey);
					workflowFinished.signalAll();
				}


			} finally {
				mutex.unlock();
			}
		}
		else{
			logger.error("[RunUnsecureWorkflowStepFromXML::deliver] Callback received from an unknown stage: "+ stageKey);
		}

	}
}
