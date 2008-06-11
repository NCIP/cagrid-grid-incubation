package org.cagrid.workflow.manager.instance.service;

import java.rmi.RemoteException;

import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowManagerInstanceImpl extends WorkflowManagerInstanceImplBase {

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

		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			resource.setParameter(inputParameter);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * Retrieve workflow outputs.
	 * */
  public java.lang.String[] getOutputValues() throws RemoteException {
		
		String[] outputs = null;
		try {
			WorkflowManagerInstanceResource resource = getResourceHome().getAddressedResource();
			outputs = resource.getWorkflowOutputs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outputs;
	}

}

