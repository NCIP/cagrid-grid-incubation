package org.cagrid.workflow.helper.instance.service;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Random;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.OutputReady;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResource;
import org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResource;
import org.cagrid.workflow.helper.service.WorkflowHelperConfiguration;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInstanceHelperImpl extends WorkflowInstanceHelperImplBase {

	
	private static Log logger = LogFactory.getLog(WorkflowInstanceHelperImpl.class);
	
	
	public WorkflowInstanceHelperImpl() throws RemoteException {
		super();
	}

  public org.cagrid.workflow.helper.invocation.stubs.types.WorkflowInvocationHelperReference createWorkflowInvocationHelper(org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor workflowInvocationHelperDescriptor) throws RemoteException {
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + "workflowInvocationHelperHome";

		try {
			// Add GLOBUS_LOCATION to the system properties
			String globus_location = System.getenv("GLOBUS_LOCATION");
			Properties sys_properties = System.getProperties();
			sys_properties.setProperty("GLOBUS_LOCATION", globus_location);
			System.setProperties(sys_properties);
			
			
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResourceHome) initialContext.lookup(homeName);
			resourceKey = home.createResource();

			//  Grab the newly created resource
			org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResource thisResource = (org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResource)home.find(resourceKey);

			//  This is where the creator of this resource type can set whatever needs
			//  to be set on the resource so that it can function appropriatly  for instance
			//  if you want the resouce to only have the query string then there is where you would
			//  give it the query string.
			thisResource.setOperationDesc(workflowInvocationHelperDescriptor);
			thisResource.setOutputType(workflowInvocationHelperDescriptor.getOutputType());
			
			boolean outputIsArray = (workflowInvocationHelperDescriptor.getOutputIsArray() != null)? workflowInvocationHelperDescriptor.getOutputIsArray().booleanValue() : false ; 
			String stageGUID = workflowInvocationHelperDescriptor.getOperationQName()+ String.valueOf(new Random().nextInt());
			
			
			thisResource.setOutputIsArray(outputIsArray);
			thisResource.setTimestampedStatus(new TimestampedStatus(Status.UNCONFIGURED, 0));  // Initialize status
			thisResource.setCredentialAccess(getResourceHome().getAddressedResource());
			thisResource.setWorkflowInvocationHelperDescriptor(workflowInvocationHelperDescriptor);
			thisResource.initializeInstrumentationRecord(stageGUID);
			thisResource.setOutputReady(OutputReady.FALSE);   // Obviously, output is not ready at this point 

			// sample of setting creator only security.  This will only allow the caller that created
			// this resource to be able to use it.
			//thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());

			String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0,transportURL.lastIndexOf('/') +1 );
			transportURL += "WorkflowInvocationHelper";
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL,resourceKey);

			thisResource.setServiceOperationEPR(new EndpointReference(epr)); // Inform the InvocationHelper the key it will use to retrieve its credential
			final boolean isSecure = (workflowInvocationHelperDescriptor.getWorkflowInvocationSecurityDescriptor() != null); 
			this.setIsInvocationHelperSecure(thisResource.getServiceOperationEPR(), isSecure);
			this.registerInvocationHelper(thisResource, workflowInvocationHelperDescriptor.getOperationQName());

		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowInvocationHelper home:" + e.getMessage(), e);
		}

		//return the typed EPR
		org.cagrid.workflow.helper.invocation.stubs.types.WorkflowInvocationHelperReference ref = new org.cagrid.workflow.helper.invocation.stubs.types.WorkflowInvocationHelperReference();
		ref.setEndpointReference(epr);

		return ref;
	}

	/***  Below: credential handling  ***/

  public void addCredential(org.apache.axis.message.addressing.EndpointReferenceType serviceOperationEPR,org.apache.axis.message.addressing.EndpointReferenceType proxyEPR) throws RemoteException {

		try {

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			resource.addCredential(new EndpointReference(serviceOperationEPR), new EndpointReference(proxyEPR));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

  public void removeCredential(org.apache.axis.message.addressing.EndpointReferenceType proxyEPR) throws RemoteException {
		try {

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			resource.removeCredential(new EndpointReference(proxyEPR));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  public void replaceCredential(org.apache.axis.message.addressing.EndpointReferenceType serviceOperationEPR,org.apache.axis.message.addressing.EndpointReferenceType proxyEPR) throws RemoteException {
		try {

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			resource.replaceCredential(new EndpointReference(serviceOperationEPR), new EndpointReference(proxyEPR));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  public void setIsInvocationHelperSecure(org.apache.axis.message.addressing.EndpointReferenceType serviceOperationEPR,boolean isSecure) throws RemoteException {
		try {

			logger.info("Invocation is secure? "+isSecure); 

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			resource.setIsInvocationHelperSecure(new EndpointReference(serviceOperationEPR), isSecure);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerInvocationHelper(WorkflowInvocationHelperResource thisResource, QName name) {

		try {

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			resource.registerInvocationHelper(thisResource.getServiceOperationEPR(), name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

  public java.lang.String getEPRString() throws RemoteException {
		try {
			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			return resource.getEPRString();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(),e);
		}
	}

}

