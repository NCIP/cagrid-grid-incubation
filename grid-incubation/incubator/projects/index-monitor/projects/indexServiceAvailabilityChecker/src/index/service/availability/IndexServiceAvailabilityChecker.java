package index.service.availability;

import org.xml.sax.SAXException;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;

/**
 * Application to determine if a given instance of the index service is running.
 * If the application returns normally, then the given index service is running
 * and functioning normally. If the index service instance is not running, then
 * the application will wait until the index service is running and then the
 * application will return normally.
 * <p>
 * If this application returns abnormally, then is is unknown whether the index
 * service is running.
 * 
 * @author mgrand
 */
public class IndexServiceAvailabilityChecker {

	/**
	 * Main method to determine if a given instance of the index service is
	 * running.
	 * 
	 * @param args
	 *            One command line argument is expected, which is the URL of the
	 *            index service to be checked.
	 */
	public static void main(String[] args) {
		boolean verbose = false;
		if (args.length == 2) {
			if ("-v".equals(args[0])) {
				verbose = true;
				args = new String[]{args[1]};
			}
		}
		if (args.length != 1) {
			usage();
		}
		try {
			String indexServiceURL = args[0];
			DiscoveryClient client = new DiscoveryClient(indexServiceURL);
			while (true) {
				System.out.println("Attempting to contact index service @ " + indexServiceURL);
				try {
					client.getAllServices(false);
					success();
				} catch (RemoteResourcePropertyRetrievalException e) {
					Throwable cause = e.getCause();
					if (cause instanceof SAXException) {
						success();						
					}
					System.out.println("Attempt to contact index service failed.");
					if (verbose) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.out.println("Attempt to contact index service failed.");
					if (verbose) {
						e.printStackTrace();
					}
				}
				Thread.sleep(3000);
			}
		} catch (Throwable e) {
			System.err.println("Exiting because an unexpected exception was thrown");
			e.printStackTrace();
			abend();
		}
	}
	
	private static void success() {
		System.out.println("Successful contact with index service.");
		System.exit(0);
	}

	/**
	 * Abnormally exist this application.
	 */
	private static void abend() {
		System.exit(-1);
	}
	
	/**
	 * Output usage message to standard error output and abnormally exit.
	 */
	private static void usage() {
		System.err.println("This application expects to have one command line argument which is the URL of the Index Service to be checked. It may be proceeded by a \"-v\" verbosity option.");
		abend();
	}

}
