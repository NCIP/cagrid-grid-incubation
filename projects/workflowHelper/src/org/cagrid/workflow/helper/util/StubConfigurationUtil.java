package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;
import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadataOperations;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.instance.stubs.SecureServiceStub;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;

public class StubConfigurationUtil {

	private Stub stub = null;;
	private EndpointReferenceType epr;
	private GlobusCredential proxy;
	
	private ServiceSecurityMetadata securityMetadata;
	
	private Map<String, Operation> operations;
	private Authorization authorization;
	private String delegationMode;

	
	
	public StubConfigurationUtil(EndpointReferenceType epr, GlobusCredential proxy ){

		this.epr = epr;
		this.proxy = proxy;
		this.stub = new SecureServiceStub();
	}

	
	
	public void configureStubSecurity(String method) throws RemoteException {

		Stub stub = this.stub;
		
		boolean https = false;
		if (epr.getAddress().getScheme().equals("https")) {
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
			operations = new HashMap<String, Operation>();
			securityMetadata = getServiceSecurityMetadata();  // TODO Actually, the service's security metadata must be given by the WorkflowManager
			ServiceSecurityMetadataOperations ssmo = securityMetadata.getOperations();
			if (ssmo != null) {
				Operation[] ops = ssmo.getOperation();
				if (ops != null) {
					for (int i = 0; i < ops.length; i++) {
						String lowerMethodName = ops[i].getName().substring(0, 1).toLowerCase()
							+ ops[i].getName().substring(1);
						operations.put(lowerMethodName, ops[i]);
					}
				}
			}

		}
		resetStub(stub);

		CommunicationMechanism serviceDefault = securityMetadata.getDefaultCommunicationMechanism();

		CommunicationMechanism mechanism = null;
		if (operations.containsKey(method)) {
			Operation o = operations.get(method);
			mechanism = o.getCommunicationMechanism();
		} else {
			mechanism = serviceDefault;
		}
		boolean anonymousAllowed = true;
		boolean authorizationAllowed = true;
		boolean delegationAllowed = true;
		boolean credentialsAllowed = true;

		if ((https) && (mechanism.getGSITransport() != null)) {
			ProtectionLevelType level = mechanism.getGSITransport().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.ENCRYPTION);
				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
					org.globus.wsrf.security.Constants.SIGNATURE);
			}
			delegationAllowed = false;

		} else if (https) {
			stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
				org.globus.wsrf.security.Constants.SIGNATURE);
			delegationAllowed = false;
		} else if (mechanism.getGSISecureConversation() != null) {
			ProtectionLevelType level = mechanism.getGSISecureConversation().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
						org.globus.wsrf.security.Constants.ENCRYPTION);

				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
					org.globus.wsrf.security.Constants.ENCRYPTION);
			}

		} else if (mechanism.getGSISecureMessage() != null) {
			ProtectionLevelType level = mechanism.getGSISecureMessage().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
						org.globus.wsrf.security.Constants.ENCRYPTION);
				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
					org.globus.wsrf.security.Constants.ENCRYPTION);
			}
			delegationAllowed = false;
			anonymousAllowed = false;
		} else {
			anonymousAllowed = false;
			authorizationAllowed = false;
			delegationAllowed = false;
			credentialsAllowed = false;
		}

		if ((anonymousAllowed) && (mechanism.isAnonymousPermitted())) {
			stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
		} else if ((credentialsAllowed) && (proxy != null)) {
			try {
				org.ietf.jgss.GSSCredential gss = new org.globus.gsi.gssapi.GlobusGSSCredentialImpl(proxy,
					org.ietf.jgss.GSSCredential.INITIATE_AND_ACCEPT);
				stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, gss);
			} catch (org.ietf.jgss.GSSException ex) {
				throw new RemoteException(ex.getMessage());
			}
		}

		if (authorizationAllowed) {
			if (authorization == null) {
				stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, getAuthorization());
			}
		}
		if (delegationAllowed) {
			if (getDelegationMode() != null) {
				stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE, getDelegationMode());
			}
		}
	}


	// TODO This method shouldn't be necessary. This operation is up to the WorkflowManager
	public ServiceSecurityMetadata getServiceSecurityMetadata() {
		
		
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
