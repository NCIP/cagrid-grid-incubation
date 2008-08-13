package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.util.WorkflowDescriptorParser;

public class RunUnsecureWorkflowStepFromXML extends RunUnsecureWorkflowsStep {



	private static final String CONTAINERBASEPLACEHOLDER = "CONTAINERBASE";


	private static Log logger = LogFactory.getLog(RunUnsecureWorkflowStepFromXML.class);


	private File sampleDescriptors;


	public RunUnsecureWorkflowStepFromXML(EndpointReferenceType manager_epr,
			String containerBaseURL, File sampleXMLDirectory) {
		super(manager_epr, containerBaseURL);  // Getting rid of the final slash so we don't have double slashes in some service's path
		this.containerBaseURL = containerBaseURL.replaceAll("/$", "");
		this.sampleDescriptors = sampleXMLDirectory;
	}


	@Override
	public void runStep() throws Throwable {


		/** Create XML description for each one of the workflows: Fan in-Fan out, simple-type array forwarding, complex-type array forwarding,
		 * simple-type array streaming, complex-type array streaming  
		 * */
		logger.info("-x-x-x- BEGIN NON-SECURE WORKFLOWS TESTS USING XML DESCRIPTORS -x-x-x-");
		if( this.validatorEnabled ) logger.info("Running the output matcher");
		else logger.info("Not running the output matcher");


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


	
	@Override
	protected void runComplexArrayStreaming(
			WorkflowManagerServiceClient wf_manager) throws RemoteException {


		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "UnsecureWorkflowComplexArrayStreaming.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		File wfDescriptor = new File(wfDescriptorFilename);
		String wfXmlDescriptor = readFileToString(wfDescriptor);


		// Replace the URLs in the descriptor with the actual container URL	
		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);
//		Reader descriptorReader = new StringReader(wfXmlDescriptor);


		// Create and execute the workflow
		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			
//			WorkflowManagerInstanceDescriptor wfDesc = (WorkflowManagerInstanceDescriptor)Utils.deserializeObject(descriptorReader, WorkflowManagerInstanceDescriptor.class);
			WorkflowManagerInstanceDescriptor wfDesc = new org.cagrid.workflow.manager.util.WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} 


		managerInstanceClient.start();  // Start execution
		String[] outputs = managerInstanceClient.getOutputValues();  // Retrieve the outputs for this workflow


	}


	@Override
	protected void runSimpleArrayStreaming(
			WorkflowManagerServiceClient wf_manager) throws RemoteException,
			MalformedURIException {

		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "UnsecureWorkflowSimpleArrayStreaming.xml";
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
		try {

			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		managerInstanceClient.start();  // Start execution
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
	protected void runSimpleArrayTest(WorkflowManagerServiceClient wf_manager)
	throws RemoteException {


		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "UnsecureWorkflowSimpleArray.xml";
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
		try {

			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		managerInstanceClient.start();  // Start execution
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


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunUnsecureWorkflowsStep#runComplexArrayTest(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient)
	 */
	@Override
	protected void runComplexArrayTest(WorkflowManagerServiceClient wf_manager)
	throws RemoteException {



		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "UnsecureWorkflowComplexArray.xml";
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
		try {

			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		managerInstanceClient.start();  // Start execution
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


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunUnsecureWorkflowsStep#runFaninFanOutTest(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient, int)
	 */
	@Override
	protected void runFaninFanOutTest(WorkflowManagerServiceClient wf_manager) throws RemoteException {


		// Read descriptor into a string
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "UnsecureFanInFanOutWorkflow.xml";
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
		try {

			WorkflowManagerInstanceDescriptor wfDesc = new WorkflowDescriptorParser().parseWorkflowDescriptor(wfXmlDescriptor);
			WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		managerInstanceClient.start();  // Start execution
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
	
}
