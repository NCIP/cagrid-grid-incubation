/**
 * 
 */
package org.cagrid.indexService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.axis.handlers.soap.SOAPService;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.container.AxisServlet;

/**
 * Override the usual servlet class to redirect GETs.
 * 
 * @author mgrand
 */
public class IndexAxisServlet extends AxisServlet {
	/**
	 * Hash code for serialization
	 */
	private static final long serialVersionUID = 2084415505816625308L;

	private static Properties redirectionProperties = null;

	@Override
	protected void reportServiceInfo(HttpServletResponse response,
			PrintWriter writer, SOAPService service, String serviceName) {
		try {
			Properties properties = getRedirectionProperties();
			String redirectionURL = properties.getProperty("redirectionURL");
			if (redirectionURL == null) {
				throw new IOException();
			}
			response.sendRedirect(redirectionURL);
		} catch (IOException e) {
			// There should be some logging here
			writer.write("Redirection to another service is enabled, but the redirection is not properly configured.");
		}
	}

	/**
	 * Get the properties object for the configuration of the redirection.
	 * 
	 * @return the properties object
	 * @throws IOException
	 *             if there is a problem.
	 */
	private Properties getRedirectionProperties() throws IOException {
		synchronized (this.getClass()) {
			if (redirectionProperties == null) {
				String baseDirectoryPath = ContainerConfig.getBaseDirectory();
				File etcDirectory = new File(baseDirectoryPath, "etc");
				File redirectionPropertiesFile = new File(etcDirectory, "redirection.properties");
				InputStream inStream = new BufferedInputStream(new FileInputStream(redirectionPropertiesFile));
				try {
					redirectionProperties = new Properties();
					redirectionProperties.load(inStream);
				} finally {
					inStream.close();
				}
			}
		}
		return redirectionProperties;
	}
}
