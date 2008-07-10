package org.cagrid.workflow.manager.instance.service;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowManagerInstanceImpl extends WorkflowManagerInstanceImplBase {

	private static Log logger = LogFactory.getLog(WorkflowManagerInstanceImpl.class);

	public WorkflowManagerInstanceImpl() throws RemoteException {
		super();
	}

	/**
	 * Receive the output of a WorkflowInvocationHelper and store it internally. The source of the received
	 * value is identified by the paramater index, that is present in exactly one pair <integer, InvocationHelper>
	 * within this resource.
	 * 
	 * @param inputParameter Parameter one wants to send to the ManagerInstance
	 * */
  public void setParameter(org.cagrid.workflow.helper.descriptor.InputParameter inputParameter) throws RemoteException {

		logger.info("Receiving parameter");

		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			resource.setParameter(inputParameter);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
		
		logger.info("END");
		return;
	}

	
	/**
	 * Retrieve the current status of the workflow managed by this ManagerInstance. 
	 * */
  public org.cagrid.workflow.helper.descriptor.TimestampedStatus getTimestampedStatus() throws RemoteException {

		TimestampedStatus status = null;
		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			status = resource.getTimestampedStatus();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}

		return status;
	}

	/**
	 * Retrieve workflow outputs.
	 * */
  public java.lang.String[] getOutputValues() throws RemoteException {

		
		logger.info("Retrieving workflow outputs");
		
		String[] outputs = null;
		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			outputs = resource.getWorkflowOutputs();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}

		logger.info("END");
		return outputs;
	}

	
  public java.lang.String getEPRString() throws RemoteException {

		String EPR = null;

		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			EPR = resource.getEPRString();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}

		return EPR;
	}

	
	public void registerInstanceHelper(WorkflowInstanceHelperClient thisResource, String name) throws RemoteException {

		logger.info("Storing WorkflowInstanceHelper for later use");
		
		try {

			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			resource.registerInstanceHelper(thisResource.getEndpointReference(), name);
		} catch (Exception e) {			
			throw new RemoteException(e.getMessage(), e);
		}
		
		logger.info("END");
		return;
	} 

	
	/** Start workflow execution */
  public void start() throws RemoteException {

		
		logger.info("Starting workflow execution");
		
		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			resource.start();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
		logger.info("END");
		return;
	}

} 

