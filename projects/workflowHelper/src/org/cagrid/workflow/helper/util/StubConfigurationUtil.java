package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.workflow.helper.instance.stubs.SecureServiceStub;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;

public class StubConfigurationUtil {

	private Stub stub = null;;
	private EndpointReferenceType epr;
	private GlobusCredential proxy;

	private ServiceSecurityMetadata securityMetadata = null;

	private Map<String, Operation> operations;
	private Authorization authorization;
	private String delegationMode;



	public StubConfigurationUtil(EndpointReferenceType epr, GlobusCredential proxy){

		this.epr = epr;
		this.proxy = proxy;
		this.stub = new SecureServiceStub();
		this.operations = new HashMap<String, Operation>();


	}


	private void initializeServiceSecurityMetadata(ServiceSecurityMetadata secMetadata){
		
		this.securityMetadata = secMetadata; 
		
		// initialize operations
		Operation[] oper = this.securityMetadata.getOperations().getOperation(); 
		for(int i=0; i < oper.length; i++){

			this.operations.put(oper[i].getName(), oper[i]);
		}

		
	}
	
	
	

	protected void configureStubSecurity(/*Stub stub,*/ String method, ServiceSecurityMetadata secMetadata) throws RemoteException {

		System.out.println("BEGIN configureStubSecurity"); //DEBUG
		
		
		this.initializeServiceSecurityMetadata(secMetadata);
		
		synchronized(stub){

			boolean https = false;
			if (epr.getAddress().getScheme().equals("https")) {
				System.out.println("[configureStubSecurity] HTTPS set");  //DEBUG
				https = true;
			}

			if (method.equals("getServiceSecurityMetadata")) {
				if (https) {
					resetStub(stub);
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
							org.globus.wsrf.security.Constants.SIGNATURE);
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
					stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
							org.globus.wsrf.impl.security.authorization.NoAuthorization.getInstance());
				}
				return;
			}

			if (securityMetadata == null) {

				throw new RemoteException("Couldn't configure stub security because the service's security metadata wasn't set");
			}
			resetStub(stub);

			CommunicationMechanism serviceDefault = securityMetadata.getDefaultCommunicationMechanism();

			CommunicationMechanism mechanism = null;
			if (operations.containsKey(method)) {
				
				System.out.println("Operation contains "+method);//DEBUG
				
				Operation o = (Operation) operations.get(method);
				mechanism = o.getCommunicationMechanism();
			} else {
				mechanism = serviceDefault;
			}
			boolean anonymousAllowed = true;
			boolean authorizationAllowed = true;
			boolean delegationAllowed = true;
			boolean credentialsAllowed = true;

			System.out.println("[configureStubSecurity] GSITransport? "+ (mechanism.getGSITransport() != null));
			if ((https) && (mechanism.getGSITransport() != null)) {
				
				System.out.println("GSITransport"); //DEBUG
				
				ProtectionLevelType level = mechanism.getGSITransport().getProtectionLevel();
				if (level != null) {
					
					System.out.println("Protection level is: "+level);
					
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						System.out.println("Setting prop GSI_TRANSPORT with value ENCRYPTION");//DEBUG
						
					} else {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
								org.globus.wsrf.security.Constants.SIGNATURE);
						System.out.println("Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
					}

				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
							org.globus.wsrf.security.Constants.SIGNATURE);
					
					System.out.println("Level is null. Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
				}
				delegationAllowed = false;

			} else if (https) {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.SIGNATURE);
				delegationAllowed = false;
				
				System.out.println("https set. Setting prop GSI_TRANSPORT with value SIGNATURE");//DEBUG
				
			} else if (mechanism.getGSISecureConversation() != null) {
				ProtectionLevelType level = mechanism.getGSISecureConversation().getProtectionLevel();
				if (level != null) {
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						System.out.println("Setting prop GSI_SEC_CONV with value ENCRYPTION");//DEBUG

					} else {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
								org.globus.wsrf.security.Constants.SIGNATURE);
						
						System.out.println("Setting prop GSI_SEC_CONV with value SIGNATURE");//DEBUG
					}

				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
							org.globus.wsrf.security.Constants.ENCRYPTION);
					
					System.out.println("Setting prop GSI_SEC_CONV with value ENCRYPTION");//DEBUG
				}

			} else if (mechanism.getGSISecureMessage() != null) {
				ProtectionLevelType level = mechanism.getGSISecureMessage().getProtectionLevel();
				if (level != null) {
					if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
								org.globus.wsrf.security.Constants.ENCRYPTION);
						
						System.out.println("Setting prop GSI_SEC_MSG with value ENCRYPTION");//DEBUG
						
					} else {
						stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
								org.globus.wsrf.security.Constants.SIGNATURE);
						
						System.out.println("Setting prop GSI_SEC_MSG with value SIGNATURE");//DEBUG
					}

				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
							org.globus.wsrf.security.Constants.ENCRYPTION);
					
					System.out.println("Setting prop GSI_SEC_MSG with value ENCRYPTION");//DEBUG
				}
				delegationAllowed = false;
				anonymousAllowed = false;
			} else {
				
				System.out.println("NOTHING really set.."); // DEBUG
				
				anonymousAllowed = false;
				authorizationAllowed = false;
				delegationAllowed = false;
				credentialsAllowed = false;
			}

			if ((anonymousAllowed) && (mechanism.isAnonymousPermitted())) {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
				
				System.out.println("Setting prop GSI_ANONYMOUS with value TRUE");//DEBUG
				
			} else if ((credentialsAllowed) && (proxy != null)) {
				try {
					org.ietf.jgss.GSSCredential gss = new org.globus.gsi.gssapi.GlobusGSSCredentialImpl(proxy,
							org.ietf.jgss.GSSCredential.INITIATE_AND_ACCEPT);
					stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, gss);
					
					System.out.println("Setting prop GSI_CREDENTIALS with value of object GSS");//DEBUG
					
				} catch (org.ietf.jgss.GSSException ex) {
					throw new RemoteException(ex.getMessage());
				}
			}

			if (authorizationAllowed) {
				if (authorization == null) {
					stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
					
					System.out.println("Setting prop AUTHORIZATION with value NO_AUTHORIZATION");//DEBUG
					
				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, getAuthorization());
					
					System.out.println("Setting prop AUTHORIZATION with value "+getAuthorization());//DEBUG
				}
			}
			if (delegationAllowed) {
				if (getDelegationMode() != null) {
					stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE, getDelegationMode());
					
					System.out.println("Setting prop GSI_MODE with value "+getDelegationMode());//DEBUG
				}
			}
		}
		
		System.out.println("END configureStubSecurity"); //DEBUG
	}


	// This method shouldn't be necessary. This operation is up to the WorkflowManager
	/*public ServiceSecurityMetadata getServiceSecurityMetadata() {


		ServiceSecurityMetadata metadata = null;
		try {
			ServiceSecurityClient ssc = new ServiceSecurityClient(this.epr, this.proxy);
			metadata = ssc.getServiceSecurityMetadata();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return metadata;
	} // */



	private void resetStub(Stub stub) {
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS);
		stub.removeProperty(org.globus.wsrf.security.Constants.AUTHORIZATION);
		stub.removeProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG);
		stub.removeProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE);

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



	public Stub getStub() {

		return stub;
	}
}
