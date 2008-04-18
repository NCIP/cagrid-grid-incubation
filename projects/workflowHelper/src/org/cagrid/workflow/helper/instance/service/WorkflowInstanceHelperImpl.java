package org.cagrid.workflow.helper.instance.service;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReference;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResource;
import org.cagrid.workflow.helper.util.ServiceInvocationUtil;
import org.globus.gsi.GlobusCredential;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInstanceHelperImpl extends WorkflowInstanceHelperImplBase {

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

			WorkflowInvocationSecurityDescriptor security_desc = workflowInvocationHelperDescriptor.getWorkflowInvocationSecurityDescriptor();

			GlobusCredential credential = ServiceInvocationUtil.configureSecurity( thisResource, security_desc );

			// sample of setting creator only security.  This will only allow the caller that created
			// this resource to be able to use it.
			//thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());

			String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0,transportURL.lastIndexOf('/') +1 );
			transportURL += "WorkflowInvocationHelper";
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL,resourceKey);
		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowInvocationHelper home:" + e.getMessage(), e);
		}

		//return the typed EPR
		org.cagrid.workflow.helper.invocation.stubs.types.WorkflowInvocationHelperReference ref = new org.cagrid.workflow.helper.invocation.stubs.types.WorkflowInvocationHelperReference();
		ref.setEndpointReference(epr);

		return ref;
	}

	
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
			GlobusCredential credential = resource.getCredential(new EndpointReference(proxyEPR));
			resource.removeCredential(credential);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
  public void replaceCredential(org.apache.axis.message.addressing.EndpointReferenceType serviceOperationEPR,org.apache.axis.message.addressing.EndpointReferenceType proxyEPR) throws RemoteException {
		try {

			WorkflowInstanceHelperResource resource = getResourceHome().getAddressedResource();
			GlobusCredential credential = resource.getCredential(new EndpointReference(proxyEPR));
			resource.replaceCredential(new EndpointReference(serviceOperationEPR), credential);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

