package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.client.ClientConstants;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;

public class CredentialHandlingUtil {


	private CredentialHandlingUtil(){}


	private static Log logger = LogFactory.getLog(CredentialHandlingUtil.class);



	/** Delegate a credential to a specific grid identity
	 * 
	 * @param delegatee Grid identity of the service the credential will be delegated to
	 * @param cdsURL The Service URL of the Credential Delegation Service (CDS)
	 * @param toDelegate GlobusCredential to delegate
	 * @param issuedCredentialLifetime Specifies the how long credentials issued to allowed parties will be valid for. 
	 * @param delegationPathLength Path length of the credential being delegated. The minimum is 1.
	 * @param issuedCredentialPathLength Path length of the credentials issued to allowed parties. A path length of 0 means that the requesting party cannot further delegate the credential.
	 * @return 
	 * */
	public static EndpointReferenceType delegateCredential(GlobusCredential toDelegate, String delegatee, String cdsURL, ProxyLifetime delegationLifetime, 
			ProxyLifetime issuedCredentialLifetime, int delegationPathLength, int issuedCredentialPathLength) throws Throwable {


		logger.info("BEGIN delegateCredential");
		logger.info("delegatee: "+ delegatee);
		logger.info("delegator: "+ toDelegate.getIdentity());
		logger.info("CDS URL: "+ cdsURL);
		logger.info("Delegation lenght: "+ delegationPathLength);
		logger.info("Issued Credential lenght: "+ issuedCredentialPathLength);


		//Specifies the key length of the delegated credential
		int keySize = ClientConstants.DEFAULT_KEY_SIZE;

		//The policy stating which parties will be allowed to obtain a delegated credential. The CDS will only 
		//issue credentials to parties listed in this policy.

		List<String> parties = new ArrayList<String>();
		parties.add(delegatee);  
		AllowedParties allowed_parties = new AllowedParties(parties.toArray(new String[parties.size()]));
		IdentityDelegationPolicy policy = new IdentityDelegationPolicy(allowed_parties );  

		//Create an instance of the delegation client, specifies the CDS Service URL and the credential 
		//to be delegated.
		logger.info("Creating DelegationUserClient"); 
		DelegationUserClient client = new DelegationUserClient(cdsURL, toDelegate);


		//Delegates the credential and returns a reference which can later be used by allowed parties to 
		//obtain a credential.
		logger.info("[delegateCredential] Sending delegation request to CDS");
		DelegatedCredentialReference ref = client.delegateCredential(delegationLifetime, delegationPathLength, policy,
				issuedCredentialLifetime, issuedCredentialPathLength, keySize);



		logger.info("END delegateCredential");

		return ref.getEndpointReference();
	}


	/** Get a delegated credential through a proxy EPR
	 * @param proxyEPR The location to retrieve credential from
	 * @return Retrieved delegated credential
	 *  */
	public static GlobusCredential getDelegatedCredential(EndpointReference proxyEPR) throws Exception {



		// get host key and cert here ->


		GlobusCredential credential = ProxyUtil.getDefaultProxy();

		logger.info("Default proxy DN: "+ credential.getIdentity()); 	

		//A DelegateCredentialReference is provided by the delegator to delegatee, it 
		//represents the delegated credential that the delegatee should obtain.
		DelegatedCredentialReference reference = new DelegatedCredentialReference(proxyEPR); 

		//Create and Instance of the delegate credential client, specifying the 
		//DelegatedCredentialReference and the credential of the delegatee.  The 
		//DelegatedCredentialReference specifies which credential to obtain.  The 
		//delegatee's credential is required to authenticate with the CDS such 
		//that the CDS may determine if the the delegatee has been granted access 
		//to the credential in which they wish to obtain.
		DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(reference, credential);


		//The get credential method obtains a signed delegated credential from the CDS.
		GlobusCredential delegatedCredential = client.getDelegatedCredential();

		return delegatedCredential;
	}

}
