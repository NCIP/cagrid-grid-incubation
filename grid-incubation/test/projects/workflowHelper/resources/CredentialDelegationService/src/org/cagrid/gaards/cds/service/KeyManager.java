/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.gaards.cds.service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;

public interface KeyManager {

	public abstract KeyPair createAndStoreKeyPair(String alias, int keyLength)
			throws CDSInternalFault;

	public abstract PublicKey getPublicKey(String alias)
			throws CDSInternalFault;

	public abstract PrivateKey getPrivateKey(String alias)
			throws CDSInternalFault;

	public abstract X509Certificate[] getCertificates(String alias)
			throws CDSInternalFault;

	public abstract boolean exists(String alias) throws CDSInternalFault;

	public abstract void storeCertificates(String alias, X509Certificate[] cert)
			throws CDSInternalFault, DelegationFault;

	public abstract void delete(String alias) throws CDSInternalFault;

	public abstract void deleteAll() throws CDSInternalFault;
}
