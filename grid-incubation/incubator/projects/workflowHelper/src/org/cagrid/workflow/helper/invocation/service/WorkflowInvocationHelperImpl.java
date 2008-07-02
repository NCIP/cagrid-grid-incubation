package org.cagrid.workflow.helper.invocation.service;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResource;
import org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResourceHome;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInvocationHelperImpl extends WorkflowInvocationHelperImplBase {

	
	private static Log logger = LogFactory.getLog(WorkflowInvocationHelperImpl.class);
	
	
	public WorkflowInvocationHelperImpl() throws RemoteException {
		super();
	}

  public void configureInput(org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor operationInputMessageDescriptor) throws RemoteException {
	
	  logger.info("BEGIN");
	  
	  try {
			WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
			resource.setInput_desc(operationInputMessageDescriptor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("END");
	}

  public void configureOutput(org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor operationOutputTransportDescriptor) throws RemoteException {
	  logger.info("BEGIN");
	  try {
		
			WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
			resource.setOutput_desc(operationOutputTransportDescriptor);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("END");
	}

  public void setParameter(org.cagrid.workflow.helper.descriptor.InputParameter inputParameter) throws RemoteException {
	  logger.info("BEGIN");
	  try {

			WorkflowInvocationHelperResourceHome brh = getResourceHome();
			WorkflowInvocationHelperResource resource = brh.getAddressedResource();
			resource.setParameter(inputParameter);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(),e);
		}
		logger.info("END");
	}

  public void setParameters(org.cagrid.workflow.helper.descriptor.InputParameter[] inputParameters) throws RemoteException {
	  logger.info("BEGIN");
	  try {
			WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
			resource.setParameters(inputParameters);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(),e);
		}
		logger.info("END");		
	}

  public java.lang.String getEPRString() throws RemoteException {
	 
	  
	  String retval = null;
	  try {
		  WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
		  retval = resource.getServiceOperationEPRString();
	  } catch (Exception e) {
		  throw new RemoteException(e.getMessage(),e);
	  }
	 
	  return retval;
  }

  public void start() throws RemoteException {
	  logger.info("BEGIN");
	  try {
		  WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
		  resource.start();
	  } catch (Exception e) {
		  throw new RemoteException(e.getMessage(),e);
	  }
	  logger.info("END");
  }

}

