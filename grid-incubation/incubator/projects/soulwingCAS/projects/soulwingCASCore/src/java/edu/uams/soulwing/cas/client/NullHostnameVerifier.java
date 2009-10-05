package edu.uams.soulwing.cas.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
/**

* Host name verifier that does not perform any checks.

*/

public class NullHostnameVerifier implements HostnameVerifier {

	public boolean verify(String hostname, SSLSession session) {
		
		return true;
	}

}
