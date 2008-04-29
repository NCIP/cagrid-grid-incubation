package org.cagrid.workflow.manager.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.manager.service.conversion.WorkflowProcessLayoutConverter;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.service.bpelParser.BpelParser;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;
/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase {

	
	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}
	public static void writeTextFile(String contents, String fullPathFilename) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fullPathFilename));
		writer.write(contents);
		writer.flush();
		writer.close();	
	}

	
  public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstance(org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowManagerInstanceDescriptor) throws RemoteException {
//	  System.out.println("Manager Service muito doido");
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		//BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + "workflowManagerServiceInstanceHome";
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext.lookup(homeName);
			resourceKey = home.createResource();
			System.out.println("resourceKey = "+resourceKey);

			//  Grab the newly created resource
			WorkflowManagerInstanceResource thisResource = (WorkflowManagerInstanceResource)home.find(resourceKey);
			
			//set the workflow descriptor on the helper instance
			thisResource.setWorkflowManagerInstanceDescriptor(workflowManagerInstanceDescriptor);
			
			String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0,transportURL.lastIndexOf('/') +1 );
			transportURL += "WorkflowManagerServiceInstance";

			System.out.println("transportURL = "+transportURL);
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL,resourceKey);
		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowManagerServiceInstance  home:" + e.getMessage(), e);
		}
		
		
		String auxFileName = null;
		try {
			auxFileName = java.io.File.createTempFile("WorkflowDescriptor_"+resourceKey.toString().replace('/', '-'), ".bpel").getAbsolutePath();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		System.out.println("FileName = "+ auxFileName);
		// now we have to parse the bpel file we received
		// first of all I am going to write it into the current directory
		try {
			// write the 
			writeTextFile(workflowManagerInstanceDescriptor.getBpelFilename(), auxFileName);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteException("ERROR: Fail to write bpel file to disk: " + auxFileName);
		}
		
		BpelParser parserBpelWorkflowFile = new BpelParser();
		
		WorkflowProcessLayout workflowLayout = parserBpelWorkflowFile.startParsing(auxFileName);
		/*try{
			workflowLayout.printClass(); // DEBUG
		}
		catch(NullPointerException npe){
			System.err.println("We got a null WorkflowProcessLayout. A problem may be happened while parsing the BPEL file");
			return null;
		} // */
		
		//DEBUG
		System.out.println("Checkpoint 1");
		
		
		/** Create workflow description so the HelperService can be invoked and the workflow instantiated */
		//org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor
		WorkflowInstanceHelperDescriptor wf_desc = new WorkflowInstanceHelperDescriptor();
		wf_desc.setWorkflowID(workflowLayout.getName());
		wf_desc.setWorkflowManagerEPR(epr);
		
		
		String helper_url = "http://localhost:8080/wsrf/services/cagrid/WorkflowHelper"; // TODO Retrieve this address from some service discovery mechanism

		WorkflowHelperClient helper_client = null;
		try {
			EndpointReference helper_epr = new EndpointReference(helper_url);
			helper_client = new WorkflowHelperClient(helper_epr);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		
		
		//DEBUG
		System.out.println("Checkpoint 2");

		
		
		WorkflowInstanceHelperDescriptor wfi_desc = new WorkflowInstanceHelperDescriptor();
		wfi_desc.setWorkflowID(workflowLayout.getName());
		wfi_desc.setWorkflowManagerEPR(epr);
		
		WorkflowInstanceHelperClient wfi_client = null;
		try {
			 wfi_client = helper_client.createWorkflowInstanceHelper(wfi_desc);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		
		
		//DEBUG
		System.out.println("Checkpoint 3");

		
		WorkflowInvocationHelperDescriptor[] invocation_descs = WorkflowProcessLayoutConverter.getWorkflowInvocationHelperDescriptors(workflowLayout);
		List<WorkflowInvocationHelperClient> operations_clients = new ArrayList<WorkflowInvocationHelperClient>(); 
		
		for(int i=0; i < invocation_descs.length; i++){
			
			
			WorkflowInvocationHelperClient curr_invocation_client = null;
			try {
				curr_invocation_client = wfi_client.createWorkflowInvocationHelper(invocation_descs[i]);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}
			
			// TODO Configure I/O for current operation
			/*curr_invocation_client.configureInput(operationInputMessageDescriptor);
			curr_invocation_client.configureOutput(operationOutputTransportDescriptor); // */
			
			// TODO Set parameter, if any is supposed to be set statically
			
			operations_clients.add(curr_invocation_client);
		}
		
		//DEBUG
		System.out.println("Checkpoint 4");
		
		
		// return the typed EPR
	    WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference(); 

		ref.setEndpointReference(epr);
		
		//DEBUG
		System.out.println("END ManagerService");
		
		return ref;


	  
	  
	  
  }

}

