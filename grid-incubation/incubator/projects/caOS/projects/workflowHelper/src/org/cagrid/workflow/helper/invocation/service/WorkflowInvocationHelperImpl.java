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
	
	  logger.info("BEGIN configureInput");
	  
	  try {
			WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
			resource.setInput_desc(operationInputMessageDescriptor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("END configureInput");
	}

  public void configureOutput(org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor operationOutputTransportDescriptor) throws RemoteException {
	  logger.info("BEGIN configureOutput");
	  try {
		
			WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
			resource.setOutput_desc(operationOutputTransportDescriptor);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("END configureOutput");
	}

  public void setParameter(org.cagrid.workflow.helper.descriptor.InputParameter inputParameter) throws RemoteException {
	  logger.info("BEGIN setParameter");
	  try {

			WorkflowInvocationHelperResourceHome brh = getResourceHome();
			WorkflowInvocationHelperResource resource = brh.getAddressedResource();
			resource.setParameter(inputParameter);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(),e);
		}
		logger.info("END setParameter");
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

  public void startStreaming() throws RemoteException {
	  logger.info("BEGIN");
	  try {
		  WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
		  resource.setReceivingStream();
	  } catch (Exception e) {
		  throw new RemoteException(e.getMessage(),e);
	  }
	  logger.info("END");
  }

  public void endStreaming() throws RemoteException {
	  logger.info("BEGIN");
	  try {
		  WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
		  resource.unsetReceivingStream();
	  } catch (Exception e) {
		  throw new RemoteException(e.getMessage(),e);
	  }
	  logger.info("END");
  }

  public javax.xml.namespace.QName getOperationQName() throws RemoteException {
	  try {
		  WorkflowInvocationHelperResource resource = getResourceHome().getAddressedResource();
		  return resource.getOperationName();
	  } catch (Exception e) {
		  throw new RemoteException(e.getMessage(),e);
	  }
  }

}

