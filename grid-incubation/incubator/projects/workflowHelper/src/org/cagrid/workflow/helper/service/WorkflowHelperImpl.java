 package org.cagrid.workflow.helper.service;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.axis.message.addressing.EndpointReference;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GlobusCredential;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowHelperImpl extends WorkflowHelperImplBase {

	public WorkflowHelperImpl() throws RemoteException {
		super();
		
		
		/* Added by Andre de Souza in May-27-2008. FIXME Should be removed as soon as we find a better way to retrieve the service's credential */
		// Host credentials and CA's should be the same
		try {
			String hostCertBasePath = WorkflowHelperConfiguration.getConfiguration().getHelperCredential();
			X509Certificate hostCert = CertUtil.loadCertificate(hostCertBasePath + "cert.pem");
			File hostKeyFile = new File(hostCertBasePath + "key.pem");
			PrivateKey hostKey = KeyUtil.loadPrivateKey(hostKeyFile, "testing");
			GlobusCredential hostCredential = new GlobusCredential(hostKey, new X509Certificate[]{ hostCert });
			
			ProxyUtil.saveProxyAsDefault(hostCredential); // TODO This shouldn't be necessary, but it is for now.
			
			// DEBUG
			/*GlobusCredential defaultProxy = ProxyUtil.getDefaultProxy(); 
			System.out.println("[WorkflowHelperImpl] Default identity is "+ defaultProxy.getIdentity()); // */
			
		} catch (Throwable e) {
			e.printStackTrace();
		} // End of Andre's code */
		
		
		
	}

  public org.cagrid.workflow.helper.instance.stubs.types.WorkflowInstanceHelperReference createWorkflowInstanceHelper(org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowInstanceHelperDescriptor) throws RemoteException {
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + "workflowInstanceHelperHome";

		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResourceHome) initialContext.lookup(homeName);
			resourceKey = home.createResource();

			//  Grab the newly created resource
			org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResource thisResource = (org.cagrid.workflow.helper.instance.service.globus.resource.WorkflowInstanceHelperResource)home.find(resourceKey);

			//  This is where the creator of this resource type can set whatever needs
			//  to be set on the resource so that it can function appropriatly  for instance
			//  if you want the resouce to only have the query string then there is where you would
			//  give it the query string.

			// sample of setting creator only security.  This will only allow the caller that created
			// this resource to be able to use it.
			//thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());
			

			String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0,transportURL.lastIndexOf('/') +1 );
			transportURL += "WorkflowInstanceHelper";
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL,resourceKey);
			
			// Set the resource identifier, that is derived from its EPR
			thisResource.setEprString(new EndpointReference(epr));
			
		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowInstanceHelper home:" + e.getMessage(), e);
		}

		//return the typed EPR
		org.cagrid.workflow.helper.instance.stubs.types.WorkflowInstanceHelperReference ref = new org.cagrid.workflow.helper.instance.stubs.types.WorkflowInstanceHelperReference();
		ref.setEndpointReference(epr);

		return ref;
	}

	/** Retrieve WorkflowHelperService's Grid identity */
  public java.lang.String getIdentity() throws RemoteException {

		// TODO Retrieve the Helper's ID dynamically
		String helperIdentity = null;
		try {
			helperIdentity = org.cagrid.workflow.helper.service.WorkflowHelperConfiguration.getConfiguration().getHelperIdentity();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return helperIdentity;
	}

}

