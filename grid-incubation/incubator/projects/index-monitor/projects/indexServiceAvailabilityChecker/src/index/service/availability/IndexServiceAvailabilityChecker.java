package index.service.availability;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;

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
		if (args.length != 1) {
			usage();
		}
		try {
			String indexServiceURL = args[0];
			DiscoveryClient client = new DiscoveryClient(indexServiceURL);
			while (true) {
				System.out.println("Attempting to contact index service");
				try {
					client.getAllServices(true);
					System.out.println("Successful contact with index service.");
					System.exit(0);
				} catch (Exception e) {
					System.out.println("Attempt to contact index service failed.");
				}
				Thread.sleep(2000);
			}
		} catch (Throwable e) {
			System.err.println("Exiting because an unexpected exception was thrown");
			e.printStackTrace();
			abend();
		}
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
		System.err.println("This application expects to have exactly one command line argument which is the URL of the Index Service to be checked.");
		abend();
	}

}
