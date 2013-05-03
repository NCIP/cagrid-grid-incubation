/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.common.Utils;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.util.WorkflowDescriptorParser;
import org.globus.gsi.GlobusCredential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RunSecureWorkflowsFromXMLStep extends RunSecureWorkflowsStep {

	private static Log logger = LogFactory.getLog(RunSecureWorkflowsFromXMLStep.class);

	private static final String CONTAINERBASEPLACEHOLDER = "CONTAINERBASE";
	private static final String CREDENTIALPROXYPLACEHOLDER = "CREDENTIALPROXY";
	private static final String CREDENTIALREFERENCEPROPERTY = "CREDENTIALREFERENCEPROPERTY";

	private File sampleDescriptors;


	public RunSecureWorkflowsFromXMLStep(EndpointReference managerEPR,
			EndpointReferenceType cdsEPR, String container_base_url,
			GlobusCredential userCredential, String cdsURL, File sampleDescriptorsDir) {

		super(managerEPR, cdsEPR, container_base_url, userCredential, cdsURL);
		this.sampleDescriptors = sampleDescriptorsDir;
	}



	public void runStep() throws Throwable {
		System.out.println("---- BEGIN SECURE WORKFLOW USING XML TEST ----");


		try{

			String wfManagerURL = this.containerBaseURL + "/cagrid/WorkflowManagerService";
			final EndpointReferenceType manager_epr = new EndpointReferenceType(new Address(wfManagerURL));
			WorkflowManagerServiceClient wf_manager = new WorkflowManagerServiceClient(manager_epr);


			// User role: Delegate user credential to the Manager
			logger.info("Obtaining user credential");
			AttributedURI cdsAddress = this.cdsEPR.getAddress();
			String cdsURL = cdsAddress.toString();
			EndpointReferenceType delegatedCredentialProxy = CredentialHandlingUtil.delegateCredential(this.userCredential, wf_manager.getIdentity(),
					cdsURL , new ProxyLifetime(5,0,0), new ProxyLifetime(6,0,0), 3, 2);
			logger.info("Delegation done");


			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			System.out.println("[CreateTestSecureWorkflowsStep] Simple arrays as input");
			runSimpleArrayTest(wf_manager, delegatedCredentialProxy);
			System.out.println("[CreateTestSecureWorkflowsStep] OK"); // */

			/*System.out.println("[CreateTestSecureWorkflowsStep] Complex arrays as input");
			runComplexArrayTest(wf_manager, delegatedCredentialProxy);
			System.out.println("[CreateTestSecureWorkflowsStep] OK");

			System.out.println("[CreateTestSecureWorkflowsStep] END Testing arrays"); // */




			/** BEGIN streaming test **/
			/*logger.info("[CreateTestSecureWorkflowsStep] BEGIN Testing streaming");

			// Streaming simple types
			logger.info("[CreateTestSecureWorkflowsStep] Streaming of simple-type arrays");
			runSimpleArrayStreaming(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] OK");  // */



			/* Streaming complex types */
			/*System.out.print("[CreateTestSecureWorkflowsStep] Streaming of complex-type arrays");
			runComplexArrayStreaming(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] OK");

			logger.info("[CreateTestSecureWorkflowsStep] END Testing streaming"); // */



			/** FAN IN AND FAN OUT TEST **/
			/*logger.info("[CreateTestSecureWorkflowsStep] BEGIN Testing fan in and fan out");
			runFaninFanOutTest(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] END Testing fan in and fan out"); // */


			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitForCompletion();

		}
		catch(Throwable t){
			logger.error(t.getMessage(), t);
			Assert.fail();
		}

		logger.info("---- END SECURE WORKFLOW USING XML TEST ----");

		return;
	}



	private void runFaninFanOutTest(WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy) throws RemoteException {

		// Build the path for the XML input file
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "SecureWorkflowFanInFanOut.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		// Submit the XML file for execution
		runWorkflowFromXML(wfDescriptorFilename , wf_manager, delegatedCredentialProxy);

	}



	private void runComplexArrayTest(WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy) throws RemoteException {



		// Build the path for the XML input file
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "SecureWorkflowComplexArray.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		// Submit the XML file for execution
		runWorkflowFromXML(wfDescriptorFilename , wf_manager, delegatedCredentialProxy);


	}



	private void runSimpleArrayTest(WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy) throws RemoteException {


		// Build the path for the XML input file
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "SecureWorkflowSimpleArray.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		// Submit the XML file for execution
		runWorkflowFromXML(wfDescriptorFilename , wf_manager, delegatedCredentialProxy);


	}


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunSecureWorkflowsStep#runComplexArrayStreaming(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient, org.apache.axis.message.addressing.EndpointReferenceType)
	 */
	protected void runComplexArrayStreaming(
			WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy)
	throws RemoteException {


		// Build the path for the XML input file
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "SecureWorkflowComplexArrayStreaming.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		// Submit the XML file for execution
		runWorkflowFromXML(wfDescriptorFilename , wf_manager, delegatedCredentialProxy);
	}



	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.tests.system.steps.RunSecureWorkflowsStep#runSimpleArrayStreaming(org.cagrid.workflow.manager.client.WorkflowManagerServiceClient, org.apache.axis.message.addressing.EndpointReferenceType)
	 */
	protected void runSimpleArrayStreaming(
			WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy)
	throws RemoteException {


		// Build the path for the XML input file
		String wfDescriptorFilename = null;
		try {
			wfDescriptorFilename = this.sampleDescriptors.getCanonicalFile() + File.separator + "SecureWorkflowSimpleArrayStreaming.xml";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}


		// Submit the XML file for execution
		runWorkflowFromXML(wfDescriptorFilename , wf_manager, delegatedCredentialProxy);


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


	private void runWorkflowFromXML(String wfDescriptorFilename, WorkflowManagerServiceClient wf_manager,
			EndpointReferenceType delegatedCredentialProxy) throws RemoteException{

		System.out.println("BEGIN runWorkflowFromXML");   //DEBUG


		File wfDescriptor = new File(wfDescriptorFilename);
		String wfXmlDescriptor = readFileToString(wfDescriptor);


		// Replace the URLs in the descriptor by the actual container URL
		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CONTAINERBASEPLACEHOLDER, this.containerBaseURL);


		// Replace the delegated credential proxy EPRs by the delegated credential's actual proxy
		String encodedEpr = "";
		try {

			// Get a string representation of the proxy EPR
			StringWriter writer = new StringWriter();
			Utils.serializeObject(delegatedCredentialProxy, EndpointReferenceType.getTypeDesc().getXmlType(), writer);
			writer.flush();
			String eprStr = writer.toString();


			// Convert the string representation into an XML document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(eprStr)));
			document.normalizeDocument();
			Element rootNode = document.getDocumentElement();


			// Convert each element of the EPR to XML format
			if(rootNode.hasChildNodes()){

				NodeList children = rootNode.getChildNodes();
				for(int i=0 ; i < children.getLength() ; i++){

					Node curr_child = children.item(i);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					XMLEncoder eprEncoder = new XMLEncoder(outStream);
					eprEncoder.writeObject(curr_child);    // Convert the EPR object into an XML representation
					eprEncoder.flush();
					eprEncoder.close();
					String currStr = new String(outStream.toByteArray());
					encodedEpr += currStr;

					//DEBUG
					System.out.println("CURR ELEMENT: "+ currStr);
				}
			}
			else throw new Exception("EPR is expected to have child nodes, but none was found");
//			wfXmlDescriptor.replaceAll(CREDENTIALREFERENCEPROPERTY, encodedEpr);

		} catch (Exception e1) {
			e1.printStackTrace();
		}


		/*
		AttributedURI address = delegatedCredentialProxy.getAddress();
		String delegatedCredentialProxyXML = address.getScheme() + "://" + address.getHost() + ':' + address.getPort() + address.getPath();   //outStream.toString();


		// Add the reference property found within the EPR to the XML
		ReferencePropertiesType refProps = delegatedCredentialProxy.getProperties();
		String refPropStr = "";
		if( refProps.size() > 0 ){
			refPropStr = refProps.get(0).toString();  // Only one element is expected
		}
		wfXmlDescriptor.replaceAll(CREDENTIALREFERENCEPROPERTY, refPropStr); // */


		// DEBUG
		System.out.println("-----------------------------------------------");
		System.out.println("EPR.toString: "+ delegatedCredentialProxy.toString());
		System.out.println("Encoded EPR: "+ encodedEpr);
		System.out.println("-----------------------------------------------");


		wfXmlDescriptor = wfXmlDescriptor.replaceAll(CREDENTIALPROXYPLACEHOLDER, encodedEpr);  // Writes the actual EPR into the XML file


		System.out.println("Final version of the XML descriptor: "+ wfXmlDescriptor);   //DEBUG


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


		System.out.println("END runWorkflowFromXML");   //DEBUG

		return;
	}

}
