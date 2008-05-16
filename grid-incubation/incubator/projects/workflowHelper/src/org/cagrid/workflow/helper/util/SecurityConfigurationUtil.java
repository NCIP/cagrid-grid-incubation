package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Service;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;

public class SecurityConfigurationUtil {


	private EndpointReferenceType epr;
	private GlobusCredential proxy;

	private ServiceSecurityMetadata securityMetadata = null;

	private Map<String, Operation> operations;
	private Authorization authorization;
	private String delegationMode;
	private Call call;
	
	
	private static boolean debug = false;


	public SecurityConfigurationUtil(EndpointReferenceType epr, GlobusCredential proxy){

		this.epr = epr;
		this.proxy = proxy;
		this.operations = new HashMap<String, Operation>();
		try {
			this.call = new Service().createCall();
		} catch (ServiceException e) {
			e.printStackTrace();
		}


	}


	private void initializeServiceSecurityMetadata(ServiceSecurityMetadata secMetadata){
		
		this.securityMetadata = secMetadata; 
		
		// initialize operations
		Operation[] oper = this.securityMetadata.getOperations().getOperation(); 
		for(int i=0; i < oper.length; i++){

			this.operations.put(oper[i].getName(), oper[i]);
		}

		
	}
	
	
	

	protected void configureStubSecurity(String method, ServiceSecurityMetadata secMetadata) throws RemoteException {

		
		this.initializeServiceSecurityMetadata(secMetadata);
		
		synchronized(call){

			boolean https = false;
			if (epr.getAddress().getScheme().equals("https")) {
				https = true;
			}

			if (method.equals("getServiceSecurityMetadata")) {
				if (https) {
					//resetStub(stub);
					call.setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
							org.globus.wsrf.security.Constants.SIGNATURE);
					call.setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
					call.setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
							org.globus.wsrf.impl.security.authorization.NoAuthorization.getInstance());
				}
				return;
			}

			if (securityMetadata == null) {

				throw new RemoteException("Couldn't configure stub security because the service's security metadata wasn't set");
			}


			CommunicationMechanism serviceDefault = securityMetadata.getDefaultCommunicationMechanism();

			CommunicationMechanism mechanism = null;
			if (operations.containsKey(method)) {
				
				Operation o = (Operation) operations.get(method);
				mechanism = o.getCommunicationMechanism();
			} else {
				mechanism = serviceDefault;
			}
			boolean anonymousAllowed = true;
			boolean authorizationAllowed = true;
			boolean delegationAllowed = true;
			boolean credentialsAllowed = true;

			if(debug) System.out.println("[configureStubSecurity] GSITransport? "+ (mechanism.getGSITransport() != null));
			if ((https) && (mechanism.getGSITransport() != null)) {
				
				if(debug) System.out.println("GSITransport"); //DEBUG
				
				ProtectionLevelType level = mechanism.getGSITransport().getProtectionLevel();
				if (level != null) {
					
					if(debug) System.out.println("Protection level is: "+level);
					
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						if(debug) System.out.println("Setting prop GSI_TRANSPORT with value ENCRYPTION");//DEBUG
						
					} else {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
								org.globus.wsrf.security.Constants.SIGNATURE);
						if(debug) System.out.println("Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
					}

				} else {
					call.setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
							org.globus.wsrf.security.Constants.SIGNATURE);
					
					if(debug) System.out.println("Level is null. Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
				}
				delegationAllowed = false;

			} else if (https) {
				call.setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.SIGNATURE);
				delegationAllowed = false;
				
				if(debug) System.out.println("https set. Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
				
			} else if (mechanism.getGSISecureConversation() != null) {
				ProtectionLevelType level = mechanism.getGSISecureConversation().getProtectionLevel();
				if (level != null) {
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						if(debug) System.out.println("Setting prop GSI_SEC_CONV with value ENCRYPTION");//DEBUG

					} else {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
								org.globus.wsrf.security.Constants.SIGNATURE);
						
						if(debug) System.out.println("Setting prop GSI_SEC_CONV with value SIGNATURE");//DEBUG
					}

				} else {
					call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
							org.globus.wsrf.security.Constants.ENCRYPTION);
					
					if(debug) System.out.println("Setting prop GSI_SEC_CONV with value ENCRYPTION");//DEBUG
				}

			} else if (mechanism.getGSISecureMessage() != null) {
				ProtectionLevelType level = mechanism.getGSISecureMessage().getProtectionLevel();
				if (level != null) {
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						if(debug) System.out.println("Setting prop GSI_SEC_MSG with value ENCRYPTION");//DEBUG
						
					} else {
						call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
								org.globus.wsrf.security.Constants.SIGNATURE);
						
						if(debug) System.out.println("Setting prop GSI_SEC_MSG with value SIGNATURE");//DEBUG
					}

				} else {
					call.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
							org.globus.wsrf.security.Constants.ENCRYPTION);
					
					if(debug) System.out.println("Setting prop GSI_SEC_MSG with value ENCRYPTION");//DEBUG
				}
				delegationAllowed = false;
				anonymousAllowed = false;
			} else {
				
				if(debug) System.out.println("NOTHING really set.."); // DEBUG
				
				anonymousAllowed = false;
				authorizationAllowed = false;
				delegationAllowed = false;
				credentialsAllowed = false;
			}

			if ((anonymousAllowed) && (mechanism.isAnonymousPermitted())) {
				call.setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
				
				if(debug) System.out.println("Setting prop GSI_ANONYMOUS with value TRUE");//DEBUG
				
			} else if ((credentialsAllowed) && (proxy != null)) {
				try {
					org.ietf.jgss.GSSCredential gss = new org.globus.gsi.gssapi.GlobusGSSCredentialImpl(proxy,
							org.ietf.jgss.GSSCredential.INITIATE_AND_ACCEPT);
					call.setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, gss);
					
					if(debug) System.out.println("Setting prop GSI_CREDENTIALS using identity "+ proxy.getIdentity());//DEBUG
					
				} catch (org.ietf.jgss.GSSException ex) {
					throw new RemoteException(ex.getMessage());
				}
			}

			if (authorizationAllowed) {
				if (authorization == null) {
					call.setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
					
					if(debug) System.out.println("Setting prop AUTHORIZATION with value NO_AUTHORIZATION");//DEBUG
					
				} else {
					call.setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, getAuthorization());
					
					if(debug) System.out.println("Setting prop AUTHORIZATION with value "+getAuthorization());//DEBUG
				}
			}
			if (delegationAllowed) {
				if (getDelegationMode() != null) {
					call.setProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE, getDelegationMode());
					
					if(debug) System.out.println("Setting prop GSI_MODE with value "+getDelegationMode());//DEBUG
				}
			}
		}
		
	}


	public Authorization getAuthorization() {
		return authorization;
	}



	public String getDelegationMode() {
		return delegationMode;
	}



	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}



	public void setDelegationMode(String delegationMode) {
		this.delegationMode = delegationMode;
	}


	public Call getCall() {
		
		return this.call;
	}
}
