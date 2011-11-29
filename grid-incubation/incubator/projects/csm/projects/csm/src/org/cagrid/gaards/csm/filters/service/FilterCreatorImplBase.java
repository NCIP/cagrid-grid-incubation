package org.cagrid.gaards.csm.filters.service;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.cagrid.gaards.csm.service.CSMConfiguration;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceHome;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public abstract class FilterCreatorImplBase {
	
	public FilterCreatorImplBase() throws RemoteException {
	
	}
	
	public CSMConfiguration getConfiguration() throws Exception {
		return CSMConfiguration.getConfiguration();
	}
	
	
	public org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome)resource;
	}

	
	
	
	public org.cagrid.gaards.csm.service.globus.resource.CSMResourceHome getCSMResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("cSMHome");
		return (org.cagrid.gaards.csm.service.globus.resource.CSMResourceHome)resource;
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
