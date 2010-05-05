package org.cagrid.redcap.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.dorian.client.DorianClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.dorian.idp.StateCode;
import org.globus.gsi.GlobusCredential;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

public class RedcapGridUserClient {

	//https://grouper.training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper
	private static final String DORIAN_URL = "https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian";
	private static final String USERMAPPINGS = "etc/rcUserGridMapping.properties";
	
	public static void main(String[] args) throws Exception {
		
		RedcapGridUserClient client = new RedcapGridUserClient();
		
		if(args.length>=3){
			String admin = args[2];
			String adminPwd = args[3];
			
			GlobusCredential credential = client.authenticate(DORIAN_URL,DORIAN_URL,admin,adminPwd);
			ProxyUtil.saveProxyAsDefault(credential);
			try {
				
				String gridGrouperUrl = args[0];
				String groupName = args[1];
				
				boolean addAnotherUser = true;
				while(addAnotherUser){
					String gridUserId = client.getInput("Grid User ID: ");
					String redcapUserId = client.getInput("Redcap User Name: ");
					
					GridGrouper grouper = new GridGrouper(gridGrouperUrl);
					GroupI group = grouper.findGroup(groupName);
					
					if (group != null) {
						boolean isMember = grouper.isMemberOf(gridUserId, groupName);
						if (isMember) {
							System.out.println("user already in the group: "+group.getName());
						} else {
							System.out.println("user is not in group: "+group.getName());
							client.registerUser(gridUserId);
							
							System.out.println("user is not found, so adding user to group");
							GridGrouperClient grp = new GridGrouperClient(gridGrouperUrl);
							grp.setAnonymousPrefered(false);
							Subject subj = SubjectUtils.getSubject(gridUserId,false);
							grp.addMember(new GroupIdentifier(null,groupName), subj.getId());
							
							int index = gridUserId.lastIndexOf("=");
							String userKey = gridUserId.substring(index+1, gridUserId.length());
							client.loadProperties(userKey,USERMAPPINGS,redcapUserId);
						}
					} else {
						System.out.println("Group not found");
					}
					try{
						addAnotherUser = Boolean.valueOf(client.getInput("Another User (true/false): "));
					}catch(Exception e){
						System.out.println("enter true/false");
						addAnotherUser = Boolean.valueOf(client.getInput("Another User (true/false): "));
					}
				} 
			}catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}else{
			System.out.println("Insufficient arguments. Required 1.Grid Group Url 2. Group Name 3. Admin Id 4. Password");
		}
	}
	
	private String getInput(String input){
		System.out.println("Enter "+input);
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}
	
	private String loadProperties(String key, String file, String value) {
		Properties props = new Properties();
		FileOutputStream fos = null;
		String query = null;
		try {
			props.load(new FileInputStream(file));
			if(!props.containsKey(key)){
				props.setProperty(key,value);
				props.store((fos = new FileOutputStream(file)),"");
				fos.close();
			}else{
				System.out.println("User already mapped to: "+props.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return query;
	}
	
	public boolean registerUser (String user) throws Exception {
		DorianClient dorianClient = new DorianClient(DORIAN_URL);
		int index = user.lastIndexOf("=");
		String gridUser = user.substring(index+1, user.length());
		try{
			if(dorianClient.doesLocalUserExist(gridUser)){
				System.out.println("User already registered in grid: " + gridUser );
				return false;
			}else{
				System.out.println("User not already registered: "+gridUser);
				dorianClient.registerLocalUser(signUp());
				System.out.println("Successfully registered in Grid"+ gridUser);
			}
		}catch(Exception e){
			System.out.println("Error Registering user");
			System.exit(0);
		}
		return true;
	}
	
	private Application signUp(){
		System.out.println(":::::::REGISTERING USER TO GRID:::::::");
		Application userApplication = new Application();
		userApplication.setUserId( getInput("User Name: "));
		userApplication.setEmail( getInput("Email: ") );
		userApplication.setPassword(getInput("Password: ") );
		userApplication.setFirstName(getInput("First Name: ") );
		userApplication.setLastName( getInput("Last Name: ") );
		userApplication.setAddress( getInput("Address: ") );
		userApplication.setAddress2( getInput("Address2: ") );
		userApplication.setCity(getInput("City: ") );
		userApplication.setState( StateCode.fromString(getInput("State(eg. OH): ")) );
		userApplication.setCountry( CountryCode.fromString( getInput("Country(eg. US): ") ) );
		userApplication.setZipcode( getInput("Zip Code: "));
		userApplication.setPhoneNumber( getInput("Phone Number: "));
		userApplication.setOrganization(getInput("Organization: "));
		return userApplication;
	}
	
	
	public GlobusCredential authenticate(String dorianURL, String authenticationServiceURL, String userId,
	        String password) throws Exception {
	    	GlobusCredential credential = null;
    	try{
	        BasicAuthentication auth = new BasicAuthentication();
	        auth.setUserId(userId);
	        auth.setPassword(password);

	        AuthenticationClient authClient = new AuthenticationClient(authenticationServiceURL);
	        SAMLAssertion saml = authClient.authenticate(auth);

	        CertificateLifetime lifetime = new CertificateLifetime();
	        lifetime.setHours(12);
	        GridUserClient dorian = new GridUserClient(dorianURL);
	        credential = dorian.requestUserCertificate(saml, lifetime);
	        System.out.println(":::::::LOGIN SUCCESSFULL:::::::");

    	}catch(Exception apf){
    		System.out.println("Error during authentication");
    		System.exit(1);
    	}
	    return credential;
	}
}


