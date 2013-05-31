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
package org.cagrid.monitor.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.cagrid.monitor.db.Service;

public class Ping implements Command {
	private static Logger LOG = Logger.getLogger(Ping.class);
	
	Service service = null;

	public CommandResult run() {
		CommandResult commandResult = new CommandResult();
		commandResult.setReturnCode(1);

		URL url = null;
		try {
			url = new URL(service.getEpr().toString());
		} catch (MalformedURLException e1) {
			LOG.error("This URL: " + service.getEpr().toString() + " should have already been validated");
			return commandResult;
		}
		
		HttpURLConnection urlConnection = null;

		try {
			acceptAllCerts();

			InetAddress.getByName(service.getEpr().getHost());

			urlConnection = (HttpURLConnection) url.openConnection();
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				commandResult.setReturnCode(0);
				commandResult.setOutput("Successfully connected to: "
						+ service.getEpr().getHost());
			}
		} catch (IOException e) {
			commandResult.setOutput("Unable to reach: "
					+ service.getEpr().getHost());
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

		}
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commandResult;
	}

	private void acceptAllCerts() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc
					.getSocketFactory());
		} catch (Exception e) {
		}
	}

	public void setParameters(Collection<Parameter> parameters) {
		Object[] params = parameters.toArray();
		Parameter parameter = (Parameter) params[0];
		this.service = (Service) parameter.getValue();
	}
}
