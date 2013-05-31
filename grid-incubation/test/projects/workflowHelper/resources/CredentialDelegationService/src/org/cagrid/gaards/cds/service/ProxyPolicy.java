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

import java.util.Set;

public class ProxyPolicy {

	private Set<String> supportedKeySizes;
	private int maxDelegationPathLength;

	public ProxyPolicy() {

	}

	public Set<String> getSupportedKeySizes() {
		return supportedKeySizes;
	}

	public void setSupportedKeySizes(Set<String> keySizes) {
		this.supportedKeySizes = keySizes;
	}

	public boolean isKeySizeSupported(int keySize) {
		if (this.supportedKeySizes.contains(String.valueOf(keySize))) {
			return true;
		} else {
			return false;
		}
	}

	public int getMaxDelegationPathLength() {
		return maxDelegationPathLength;
	}

	public void setMaxDelegationPathLength(int maxDelegationPathLength) {
		this.maxDelegationPathLength = maxDelegationPathLength;
	}
}
