package org.cagrid.workflow.helper.service;

import org.cagrid.workflow.helper.service.globus.resource.WorkflowHelperResource;
import  org.cagrid.workflow.helper.service.WorkflowHelperConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public abstract class WorkflowHelperImplBase {
	
	public WorkflowHelperImplBase() throws RemoteException {
	
	}
	
	public WorkflowHelperConfiguration getConfiguration() throws Exception {
		return WorkflowHelperConfiguration.getConfiguration();
	}
	
	
	public org.cagrid.workflow.helper.service.globus.resource.WorkflowHelperResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (org.cagrid.workflow.helper.service.globus.resource.WorkflowHelperResourceHome)resource;
	}

	
	
	
	public org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResourceHome getWorkflowInstanceHelperResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("workflowInstanceHelperHome");
		return (org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResourceHome)resource;
	}
	
	public org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResourceHome getWorkflowInvocationHelperResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("workflowInvocationHelperHome");
		return (org.cagrid.workflow.helper.invocation.service.globus.resource.WorkflowInvocationHelperResourceHome)resource;
	}
	
	
	protected ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}


}

