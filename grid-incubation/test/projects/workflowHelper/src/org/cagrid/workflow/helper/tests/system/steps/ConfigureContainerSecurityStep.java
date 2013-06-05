/**
*============================================================================
*  Copyright (c) 2008, The Ohio State University Research Foundation, Emory 
*  University, the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridca.common.CertUtil;
import gov.nih.nci.cagrid.gridca.common.KeyUtil;
import gov.nih.nci.cagrid.gridca.common.SecurityConstants;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import junit.framework.Assert;

import org.globus.gsi.GlobusCredential;



public class ConfigureContainerSecurityStep extends Step {

	private static String separator = File.separator;
	private static final String securityFilename = "webapps"+separator+"wsrf"+separator+"WEB-INF"+separator+"etc"+separator
	+"globus_wsrf_core"+separator+"global_security_descriptor.xml"; // Security descriptor file path in Tomcat container
	private static final String hostCertFilename = "webapps"+separator+"wsrf"+separator+"WEB-INF"+separator+"etc"+separator
	+"globus_wsrf_core"+separator+"host_cert.pem";
	private static final String hostKeyFilename = "webapps"+separator+"wsrf"+separator+"WEB-INF"+separator+"etc"+separator
	+"globus_wsrf_core"+separator+"host_key.pem";
	private static final String caCertFilename = "certificates" + separator + "ca";



	private File containerBasedir;
	private GlobusCredential hostCredential;  // Credential to be used for starting up the container

	// Credential to be used as CA identifier
	private X509Certificate caCertificate;   
	private PrivateKey caKey;

	private File hostKeyFileLocation;



	public ConfigureContainerSecurityStep(File containerBasedir, File hostCertFile, File hostKeyFile) {
		super();
		this.containerBasedir = containerBasedir;
		this.hostKeyFileLocation = hostKeyFile;


		// Retrieve host certificate
		try {

			// Host credentials and CA's should be the same
			this.caCertificate = CertUtil.loadCertificate(hostCertFile);
			this.caKey = KeyUtil.loadPrivateKey(hostKeyFile, SecurityConstants.HOSTKEY_PASSWORD);


			this.hostCredential = new GlobusCredential(this.caKey, new X509Certificate[]{ this.caCertificate });
			ProxyUtil.saveProxyAsDefault(this.hostCredential);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * Configure the container security. 
	 * 
	 * 
	 * */
	public void runStep() throws Throwable {


		try{

			// Write host pair <certificate, key> to file system
			X509Certificate hostCert = this.caCertificate;   //this.hostCertificate.getIdentityCertificate();
			File hostCertFile = new File(this.containerBasedir + ConfigureContainerSecurityStep.separator +
					ConfigureContainerSecurityStep.hostCertFilename);
			//System.out.println("Writing host certificate to "+ hostCertFile.getCanonicalPath());//DEBUG
			String hostCertString = CertUtil.writeCertificate(hostCert);
			FileWriter hostCertPrinter = new FileWriter(hostCertFile);
			hostCertPrinter.append(hostCertString);
			hostCertPrinter.close();


			//PrivateKey hostKey = this.caKey;  //this.hostCredential.getPrivateKey();
			File hostKeyFile = new File(this.containerBasedir + ConfigureContainerSecurityStep.separator +
					ConfigureContainerSecurityStep.hostKeyFilename);
			//System.out.println("Writing host key to "+ hostKeyFile.getCanonicalPath()); //DEBUG
			copyFile(this.hostKeyFileLocation, hostKeyFile);



			// Copy the CA certificate to the Globus trusted CA certificates' directory 
			X509Certificate caCert = this.caCertificate; //this.hostCredential.getIdentityCertificate();
			File caCertFile = new File(this.containerBasedir  + separator + ConfigureContainerSecurityStep.caCertFilename + 
					separator + "cacert.0");
			//System.out.println("Writing CA certificate to "+ caCertFile.getCanonicalPath());//DEBUG
			String caCertString = CertUtil.writeCertificate(caCert);
			FileWriter caCertPrinter = new FileWriter(caCertFile);
			caCertPrinter.append(caCertString);
			caCertPrinter.close();


			String caCertGlobus = System.getProperty("user.home") + separator + ".globus" + separator 
			+ "certificates" + separator + "cacert.0";
			File caCertGlobusFile = new File(caCertGlobus);
			FileWriter caCertGlobusPrinter = new FileWriter(caCertGlobusFile);
			caCertGlobusPrinter.append(caCertString);
			caCertGlobusPrinter.close();



			// Write security descriptor
			File secDescFile = new File(this.containerBasedir + ConfigureContainerSecurityStep.separator +ConfigureContainerSecurityStep.securityFilename);
			FileWriter fileOutput = new FileWriter(secDescFile);		


			fileOutput.append(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
					"<securityConfig xmlns=\"http://www.globus.org\">\n"+
					"<credential>\n"+
					"<key-file value=\""+ hostKeyFile.getCanonicalPath() +"\" />\n"+
					"<cert-file value=\""+ hostCertFile.getCanonicalPath() +"\" />\n"+
					"</credential>\n"+
			"</securityConfig>\n" ); 
			// */
			fileOutput.close();
		}
		catch(Throwable t){
			t.printStackTrace();
			Assert.fail(t.getMessage());
		}


		return;
	}


	public static void copyFile(File in, File out) 
	throws IOException 
	{
		FileChannel inChannel = new
		FileInputStream(in).getChannel();
		FileChannel outChannel = new
		FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(),
					outChannel);
		} 
		catch (IOException e) {
			throw e;
		}
		finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}

}
