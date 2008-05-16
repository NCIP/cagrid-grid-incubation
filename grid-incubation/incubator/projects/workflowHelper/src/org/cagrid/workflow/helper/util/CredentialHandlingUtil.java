package org.cagrid.workflow.helper.util;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.cds.client.ClientConstants;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.globus.gsi.GlobusCredential;

public class CredentialHandlingUtil {


	private CredentialHandlingUtil(){}


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
			ProxyLifetime issuedCredentialLifetime, int delegationPathLength, int issuedCredentialPathLength){

		//DEBUG
		/*System.out.println("BEGIN delegateCredential");
		System.out.println("delegatee: "+ delegatee);
		System.out.println("delegator: "+ toDelegate.getIdentity());
		System.out.println("Delegation lenght: "+ delegationPathLength);
		System.out.println("Issued Credential lenght: "+ issuedCredentialPathLength); // */


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

		DelegationUserClient client = null;
		try {
			client = new DelegationUserClient(cdsURL, toDelegate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Delegates the credential and returns a reference which can later be used by allowed parties to 
		//obtain a credential.
		DelegatedCredentialReference ref = null;
		try {
			ref = client.delegateCredential(delegationLifetime, delegationPathLength, policy,
					issuedCredentialLifetime, issuedCredentialPathLength, keySize);
		} catch (CDSInternalFault e) {
			e.printStackTrace();
		} catch (DelegationFault e) {
			e.printStackTrace();
		} catch (PermissionDeniedFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		//DEBUG
		//System.out.println("END delegateCredential");

		return ref.getEndpointReference();
	}


	/** Get a delegated credential through a proxy EPR
	 * @param proxyEPR The location to retrieve credential from
	 * @return Retrieved delegated credential
	 *  */
	public static GlobusCredential getDelegatedCredential(EndpointReference proxyEPR) throws Exception {



		GlobusCredential credential = ProxyUtil.getDefaultProxy();


		//System.out.println("Identity: "+ credential.getIdentity()); 	//DEBUG

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



	public static GlobusCredential LogUserOnGrid(String dorianURL, String userID, String userPassword, 
			int delegationPathLength, ProxyLifetime lifetime){

		GlobusCredential proxy = null;


		try{

			//Create credential		

			Credential cred = new Credential();
			BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
			bac.setUserId(userID);
			bac.setPassword(userPassword);
			cred.setBasicAuthenticationCredential(bac);

			//Authenticate to the IdP (DorianIdP) using credential

			AuthenticationClient authClient = new AuthenticationClient(dorianURL, cred);
			SAMLAssertion saml = authClient.authenticate();

			gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime lifetime2 = new gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime();
			lifetime2.setHours(lifetime.getHours());
			lifetime2.setMinutes(lifetime.getMinutes());
			lifetime2.setSeconds(lifetime.getSeconds());
			
			

			//Request Grid Credential

			IFSUserClient dorian = new IFSUserClient(dorianURL);
			proxy = dorian.createProxy(saml, lifetime2,delegationPathLength);

		}catch (Exception e) {
			e.printStackTrace();
		}


		return proxy;
	}

}
