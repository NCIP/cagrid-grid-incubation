package gov.nih.nci.cagrid.identifiers.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class IdentifiersNAServiceConfiguration implements ServiceConfiguration {

	public static IdentifiersNAServiceConfiguration  configuration = null;
    public String etcDirectoryPath;
    	
	public static IdentifiersNAServiceConfiguration getConfiguration() throws Exception {
		if (IdentifiersNAServiceConfiguration.configuration != null) {
			return IdentifiersNAServiceConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			IdentifiersNAServiceConfiguration.configuration = (IdentifiersNAServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return IdentifiersNAServiceConfiguration.configuration;
	}
	

	
	private String identifiersNaPrefix;
	
	private String identifiersNaHttpServerPort;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getIdentifiersNaPrefix() {
		return identifiersNaPrefix;
	}
	
	
	public void setIdentifiersNaPrefix(String identifiersNaPrefix) {
		this.identifiersNaPrefix = identifiersNaPrefix;
	}

	
	public String getIdentifiersNaHttpServerPort() {
		return identifiersNaHttpServerPort;
	}
	
	
	public void setIdentifiersNaHttpServerPort(String identifiersNaHttpServerPort) {
		this.identifiersNaHttpServerPort = identifiersNaHttpServerPort;
	}

	
}
