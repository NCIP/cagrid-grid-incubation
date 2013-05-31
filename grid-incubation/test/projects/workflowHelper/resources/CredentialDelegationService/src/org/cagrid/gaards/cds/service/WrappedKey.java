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

public class WrappedKey {

	private byte[] wrappedKeyData;
	private byte[] iv;


	public WrappedKey(byte[] wrappedKeyData, byte[] iv) {
		this.wrappedKeyData = wrappedKeyData;
		this.iv = iv;
	}


	public byte[] getWrappedKeyData() {
		return wrappedKeyData;
	}


	public byte[] getIV() {
		return iv;
	}
}
