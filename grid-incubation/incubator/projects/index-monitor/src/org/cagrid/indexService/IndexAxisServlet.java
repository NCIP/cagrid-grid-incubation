/**
 * 
 */
package org.cagrid.indexService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.axis.handlers.soap.SOAPService;
import org.globus.wsrf.container.AxisServlet;

/**
 * Override the usual servlet class to redirect GETs.
 * @author mgrand
 */
public class IndexAxisServlet extends AxisServlet {
	/**
	 * Hash code for serialization
	 */
	private static final long serialVersionUID = 2084415505816625308L;

	@Override
	protected void reportServiceInfo(HttpServletResponse response,
			PrintWriter writer, SOAPService service, String serviceName) {
        writer.write("We can print stuff here, or redirect :");
        // The leading slash means we are redirecting using an absolute path
//        String redirectPage = response.encodeRedirectURL("/" + serviceName + ".jsp");
//        try {
//            response.sendRedirect(redirectPage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }           
        //super.reportServiceInfo(response, writer, service, serviceName);
	}

	
}
