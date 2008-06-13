package org.cagrid.workflow.manager.service;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.service.bpelParser.BpelParser;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;
import org.cagrid.workflow.manager.service.conversion.WorkflowProcessLayoutConverter;

/**
 * I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase {

	private static final Log logger = LogFactory.getLog(WorkflowManagerServiceImpl.class);

	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}

	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstance(org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowManagerInstanceDescriptor,org.apache.axis.message.addressing.EndpointReferenceType managerEPR) throws RemoteException {

		logger.debug("Manager Service muito doido"); 

		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		// BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
		.getCurrentContext();
		String servicePath = ctx.getTargetService();

		// workflowManagerInstanceResourceHome
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
		+ servicePath + "/" + "workflowManagerInstanceHome";
		System.out.println("homeName = " + homeName);
		WorkflowManagerInstanceResource thisResource = null;
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext
			.lookup(homeName);
			resourceKey = home.createResource();
			System.out.println("resourceKey = " + resourceKey);

			// Grab the newly created resource
			thisResource = (WorkflowManagerInstanceResource) home.find(resourceKey);

			// set the workflow descriptor on the helper instance
			thisResource
			.setWorkflowManagerInstanceDescriptor(workflowManagerInstanceDescriptor);

			String transportURL = (String) ctx
			.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
			transportURL += "WorkflowManagerInstance";

			System.out.println("transportURL = " + transportURL);
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);

			// Set the resource identifier, that is derived from its EPR
			thisResource.setEPRString(new EndpointReference(epr).toString());

		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowManagerInstance  home:"
					+ e.getMessage(), e);
		}

		/** Parse BPEL description */
		BpelParser parserBpelWorkflowFile = new BpelParser();

		WorkflowProcessLayout workflowLayout = parserBpelWorkflowFile.parseString(workflowManagerInstanceDescriptor.getBpelDescription(),
				resourceKey.toString());
		// Parser Bpel done
		logger.info("End bpel parser");

		/* Get the URL of the workflow's services */
		HashMap<String, URL> servicesURLs = WorkflowProcessLayoutConverter.getServicesURL(
				workflowManagerInstanceDescriptor.getServicesURLs()); 

		/* Create as many helpers as containers we are supposed to contact. 
		 * For now, we are assuming all InvocationHelpers in the same container
		 * to be managed by the same InstanceHelper */
		Map<String, List<String>> instancesServices = WorkflowProcessLayoutConverter.
		getInstanceHelpersForInvocationHelpers(workflowManagerInstanceDescriptor.getServicesURLs());

		WorkflowProcessLayoutConverter converter = new WorkflowProcessLayoutConverter(workflowLayout);
		List<WorkflowStageDescriptor> invocation_descs = converter.extractWorkflowInvocationHelperDescriptors(
				servicesURLs);

		/** Create all InstanceHelpers and its underlying InvocationHelpers */
		logger.info("Creating workflow");

		// TODO: the id just as the workflow name is not a strong name
		logger.info("ID = " + workflowLayout.getName());

		Set<Entry<String, List<String>>> instanceHelpers = instancesServices.entrySet();
		Iterator<Entry<String, List<String>>> instanceHelpersIter = instanceHelpers.iterator();

		while( instanceHelpersIter.hasNext() ){

			Entry<String, List<String>> curr_entry = instanceHelpersIter.next();

			// Retrieve WorkflowHelper's address and instantiate it
			String curr_helperURL = curr_entry.getKey(); 
			List<String> curr_invocationHelperURLs = curr_entry.getValue();

			thisResource.createWorkflowInstanceHelper(curr_helperURL, curr_invocationHelperURLs, invocation_descs, managerEPR, workflowLayout);
		}

		// return the typed EPR
		WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference();
		ref.setEndpointReference(epr);

		// DEBUG
		System.out.println("END ManagerService");

		return ref;
	}

}

