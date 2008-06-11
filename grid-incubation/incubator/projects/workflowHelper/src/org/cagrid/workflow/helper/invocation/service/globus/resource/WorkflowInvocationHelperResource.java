package org.cagrid.workflow.helper.invocation.service.globus.resource;

import gov.nih.nci.cagrid.introduce.servicetools.FilePersistenceHelper;
import gov.nih.nci.cagrid.introduce.servicetools.PersistenceHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Text;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.service.globus.resource.CredentialAccess;
import org.cagrid.workflow.helper.invocation.DeliveryEnumerator;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.invocation.common.WorkflowInvocationHelperConstants;
import org.cagrid.workflow.helper.invocation.stubs.WorkflowInvocationHelperResourceProperties;
import org.cagrid.workflow.helper.service.WorkflowHelperConfiguration;
import org.cagrid.workflow.helper.util.ConversionUtil;
import org.cagrid.workflow.helper.util.ServiceInvocationUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.utils.SubscriptionPersistenceUtils;
import org.w3c.dom.Node;


/** 
 * The implementation of this WorkflowInvocationHelperResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInvocationHelperResource extends WorkflowInvocationHelperResourceBase {


	private QName outputType = null;
	private WorkflowInvocationHelperDescriptor operationDesc = null;
	private OperationInputMessageDescriptor input_desc = null;
	private OperationOutputTransportDescriptor output_desc = null;
	private InputParameter[] paramData = new InputParameter[0];
	private CredentialAccess credentialAccess;      // Interface to retrieve GlobusCredential from the InstanceHelper (necessary to invoke secure operations)
	private EndpointReference serviceOperationEPR;  // EPR of this instance. Used as key to retrieve GlobusCredential from the InstanceHelper
	private String serviceOperationEPRString;
	private boolean isSecure = false;


	// Persistency variables
	private boolean beingLoaded = false;
	private PersistenceHelper resourcePropertyPersistenceHelper = null;
	private FilePersistenceHelper resourcePersistenceHelper = null;



	public synchronized boolean executeIfReady() {


		for (int i = 0; i < paramData.length; i++) {
			if (paramData[i] == null) {
				return false;
			}
		}

		logger.debug("[executeIfReady] Execution started for "+ getOperationDesc().getOperationQName().getLocalPart()); 

		try {
			logger.debug("[executeIfReady] Will get curr status"); 
			int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1;
			logger.debug("[executeIfReady] OK. Setting new status");
			this.setTimestampedStatus(new TimestampedStatus(Status.RUNNING, nextTimestamp));
			logger.debug("Set status to RUNNING"); 
		} catch (ResourceException e2) {
			e2.printStackTrace();
		}

		final Thread th = new Thread(new Runnable() {

			public synchronized void run() {


				logger.debug("-- Thread started");

				// we have all the input data needed to execute so lets execute
				// 1. make execution call with axis
				List<Node> service_response = new ArrayList<Node>();
				try {



					final boolean invocationIsSecure = isSecure();  
					logger.debug("[RUNNABLE] Blocking until credential is provided");
					GlobusCredential credential = invocationIsSecure ? getCredential() : null;

					logger.debug("[RUNNABLE] Retrieved credential: "+ credential); 

					InputParameterDescriptor[] input_desc = getInput_desc().getInputParam();
					InputParameter[] input_value = getParamData();

					boolean parameterIsArray = false;
					boolean dataIsArray = false; 
					boolean serviceAlreadyInvoked = false;


					/* Inspect each parameter so we can determine what we're supposed to do with the provided values */
					for(int input = 0; input < input_value.length; input++){

						dataIsArray = parameterIsArray = false;

						final int paramIndex = input_value[input].getParamIndex();
						parameterIsArray = input_desc[paramIndex].getParamType().getLocalPart().contains("[]");		
						final String paramData = input_value[input].getData();

						// Verify whether the soap object represents an array or not
						dataIsArray = DataIsArray(paramData);

						logger.debug("Parameter is array = "+ parameterIsArray + " ; data is array = "+ dataIsArray);

						// If data is array and parameter is not, we need to generate one request for each array element 
						if( dataIsArray && !parameterIsArray ){ 


							// Extract the array elements from the received data and forward each one to its appropriate destination
							List<String> array_elements = getArrayElementsFromData(paramData);
							ListIterator<String> array_elements_iter = array_elements.listIterator();

							while( array_elements_iter.hasNext() ){


								String curr_array_str = array_elements_iter.next();

								// Create new inputs, with the original input value substituted for a new one (only the current array element as data)
								InputParameter[] new_input_params = input_value.clone();
								new_input_params[input].setData(curr_array_str);


								// Invoke service according to its security configuration 
								Node response_node = null;
								if( invocationIsSecure ){

									response_node = ServiceInvocationUtil.generateSecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(), 
											new_input_params, getCredential()); 									
								}
								else {

									response_node = ServiceInvocationUtil.generateUnsecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(), 
											new_input_params);									
								}
								service_response.add(response_node);
								serviceAlreadyInvoked = true;

							}
						}
					}


					if( !serviceAlreadyInvoked ){  // Usual service invocation

						logger.debug("Streaming not applicable");

						/* Invoke service according to its security configuration */
						if( invocationIsSecure ){

							logger.debug("Invoking secure service");

							service_response.add(ServiceInvocationUtil.generateSecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(), 
									input_value, getCredential()));
						}
						else {

							logger.debug("Invoking non-secure service"); 
							service_response.add(ServiceInvocationUtil.generateUnsecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(),
									input_value));
						}

					}

				} catch (Exception e1) {
					e1.printStackTrace();
					try {
						int nextTimestamp = getTimestampedStatus().getTimestamp() + 1; 
						setTimestampedStatus(new TimestampedStatus(Status.ERROR, nextTimestamp));
						logger.error("Set status to ERROR"); 
					} catch (ResourceException e) {
						e.printStackTrace();
					}
				}



				// See list contents
				Node curr_node = null;
				logger.debug("----------------------------");
				for(ListIterator<Node> it = service_response.listIterator(); it.hasNext(); ){
					curr_node = it.next();
					logger.debug("Curr node is: "+curr_node);
				}
				logger.debug("----------------------------");  // */



				/* Process each response and send the outputs to the appropriate service */
				ListIterator<Node> service_response_iterator = service_response.listIterator();
				while( service_response_iterator.hasNext() ){

					final Node curr_response = service_response_iterator.next();

					String node_string = ""+curr_response; // Don't delete the empty string! A compile-time error will occur!!


					// 2. get result send parts where ever the transport descriptor
					// tells me
					org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor desc = getOutput_desc();

					if(desc != null){

						final int num_params = (desc.getParamDescriptor() != null)? desc.getParamDescriptor().length : 0; 

						for (int i = 0; i < num_params; i++) {
							final org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor pdesc = desc.getParamDescriptor(i);
							try {
								InputParameter iparam = new InputParameter();
								iparam.setParamIndex(pdesc.getParamIndex());

								String thisStageOutputType = getOutputType().getLocalPart(); // Get output type of the just executed operation 
								String nextStageExpectedType = pdesc.getType().getLocalPart(); // Get expected type of the receiver of this invocation's output   
								boolean outputIsArray = thisStageOutputType.contains("[]");
								boolean nextStageInputIsArray = nextStageExpectedType.contains("[]");


								// need to get that data out of the response;
								// first, prepare all namespace mappings to the query
								String data = ServiceInvocationUtil.applyXPathQuery(node_string, pdesc.getLocationQuery(), pdesc.getQueryNamespaces());
								iparam.setData(data);

								
								logger.debug("\tfor query '" + pdesc.getLocationQuery() + "' we got\t'"+ data +"'");

								// send the data to the next workflow helper instance
								if( pdesc.getDestinationEPR() != null ){


									EndpointReferenceType next_destination = null;  // next destination to forward data

									/* If we can't do streaming, do usual forwarding. Otherwise, forward each output element to 
									 * a destination following a delivery policy */
									if( !outputIsArray || nextStageInputIsArray ){    

										// Do usual forwarding
										next_destination = pdesc.getDestinationEPR()[0];
										WorkflowInvocationHelperClient client = new WorkflowInvocationHelperClient(next_destination);
										client.setParameter(iparam);

									}
									else {  // Do streaming between stages 


										// Get array elements
										List<String> array_elements = getArrayElementsFromData(data);
										// Prepare for enumerate the destination of each array element
										DeliveryEnumerator destinations_iter = new DeliveryEnumerator(pdesc.getDeliveryPolicy(), pdesc.getDestinationEPR());


										// Iterate over the array elements' list, forwarding each one to a (possibly) different location 
										ListIterator<String> array_iter = array_elements.listIterator();
										while( array_iter.hasNext() ){


											String curr_array_element = array_iter.next();
											iparam.setData(curr_array_element);


											// Get one of the possible destinations according to the delivery policy
											if( destinations_iter.hasNext() ){
												next_destination = destinations_iter.next();
											}
											else {
												System.err.println();
												break;
											}

											// Send the data to the appropriate InvocationHelper 
											WorkflowInvocationHelperClient client = new WorkflowInvocationHelperClient(next_destination);
											client.setParameter(iparam);

										} // End of array elements
									}

								}
								else {
									logger.error("No destination assigned to current parameter (in "+ getOperationDesc().getOperationQName() +").");
									logger.error("Value of parameter is: \n"+iparam.getData());
									System.err.flush();
								}

							} catch (MalformedURIException e) {
								try {
									int nextTimestamp = getTimestampedStatus().getTimestamp() + 1; 
									setTimestampedStatus(new TimestampedStatus(Status.ERROR, nextTimestamp));
									logger.error("Set status to ERROR"); 
								} catch (ResourceException e1) {
									e1.printStackTrace();
								}
								e.printStackTrace();
							} catch (RemoteException e) {
								try {
									int nextTimestamp = getTimestampedStatus().getTimestamp() + 1; 
									setTimestampedStatus(new TimestampedStatus(Status.ERROR, nextTimestamp));
									logger.error("Set status to ERROR"); 
								} catch (ResourceException e1) {
									e1.printStackTrace();
								}
								e.printStackTrace();
							}
						}
					}

				}
				try {
					int nextTimestamp = getTimestampedStatus().getTimestamp() + 1; 
					setTimestampedStatus(new TimestampedStatus(Status.FINISHED, nextTimestamp));
					logger.debug("Set status to FINISHED ("+ getOperationDesc().getOperationQName() +")"); 
				} catch (ResourceException e) {
					e.printStackTrace();
				}

				logger.debug("-- Thread finished");
				return;
			}

		});


		/* Start thread and wait for it to finish */
		th.start();
		try {
			th.join();

			//System.out.println("[executeIfReady] Changing status for FINISHED");


			if( this.getTimestampedStatus().getStatus().equals(Status.RUNNING) ){

				int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1; 
				this.setTimestampedStatus(new TimestampedStatus(Status.FINISHED, nextTimestamp));
			}
		} catch (InterruptedException e) {

			e.printStackTrace();
			try {
				int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1; 
				this.setTimestampedStatus(new TimestampedStatus(Status.ERROR, nextTimestamp));
			} catch (ResourceException e1) {}

		} catch (ResourceException e) {
			e.printStackTrace();
			try {
				int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1; 
				this.setTimestampedStatus(new TimestampedStatus(Status.ERROR, nextTimestamp));
			} catch (ResourceException e1) {}

		}
		//System.out.println("[executeIfReady] END");

		return true;
	}




	/** Get an array represented as a SOAP element and extracts the array elements
	 * 
	 * @param paramData The SOAP element whose child is an array
	 * @return A list with all array elements represented as a String
	 *  
	 *  */
	private List<String> getArrayElementsFromData(String paramData) {


		List<String> output_elements = new ArrayList<String>(); 

		Iterator array = ConversionUtil.String2SOAPElement(paramData);
		SOAPElement enclosing_tag = null;
		if( array.hasNext() ){
			enclosing_tag = (SOAPElement) array.next();
		}

		// Extract each element and generate a request to the service
		Iterator array_elements = enclosing_tag.getChildElements();
		while( array_elements.hasNext() ){


			Object curr_array_element = array_elements.next();
			String curr_array_str = null;

			if(curr_array_element instanceof org.apache.axis.message.Text){
				Text txt = (Text) curr_array_element;
				curr_array_str = txt.getNodeValue();
				output_elements.add(curr_array_str);
			}

			// 2nd case) Parameter is represented as a list of attributes (complex type, simple type array, complex type array)
			else if( curr_array_element instanceof javax.xml.soap.SOAPElement ){

				SOAPElement tree = (SOAPElement) curr_array_element;
				curr_array_str = ""+tree;
				output_elements.add(curr_array_str);
			} 


		}

		return output_elements;
	}



	private boolean DataIsArray(String paramData) {

		boolean dataIsArray = false;
		Iterator data_soap_iterator = ConversionUtil.String2SOAPElement(paramData);

		while(data_soap_iterator.hasNext()){

			Object curr_obj = data_soap_iterator.next();

			if( curr_obj instanceof javax.xml.soap.SOAPElement ){


				SOAPElement enclosing_tag = (SOAPElement) curr_obj; // This object represent a tag like <Response/>

				Iterator array_elements = enclosing_tag.getChildElements(); 
				if( array_elements.hasNext() ){


					Object curr_elem = array_elements.next();

					if( curr_elem instanceof javax.xml.soap.SOAPElement ){

						SOAPElement first_element = (SOAPElement) curr_elem;
						final String first_element_name = first_element.getLocalName();

						if( array_elements.hasNext() ){ // Arrays must have two elements with same name, so let's check this

							Object next_obj = array_elements.next();


							SOAPElement second_element = (SOAPElement) next_obj;
							final String second_element_name = second_element.getLocalName();

							if(first_element_name.equals(second_element_name)){

								dataIsArray = true;
								break;
							}
						}
					}
				}
			}
		} 

		return dataIsArray;
	}



	/** The method below is used for debugging purposes */
	/*private static void printParameters(InputParameter[] paramData){


		String output = "";
		output += "INPUT RECEIVED [printParameters]:\n";
		for(int i=0; i < paramData.length; i++){
			output += '['+paramData[i].getData()+" : "+paramData[i].getParamIndex()+"]\n";
		}
		output += "END PRINT PARAMETERS\n";
		System.out.println(output);
		System.out.flush();
		return;
	} // */


	public synchronized void setParameters(InputParameter[] params) {
		for (int i = 0; i < params.length; i++) {
			setParameter(params[i]);
		}
	}


	public synchronized void setParameter(InputParameter param) {


		Status curr_status = this.getTimestampedStatus().getStatus();
		logger.debug("[setParameter] status is "+curr_status); 

		if(curr_status.equals(Status.WAITING) || curr_status.equals(Status.FINISHED)){

			if (param != null) {
				paramData[param.getParamIndex()] = param;
			}


			// poll to see if we can execute
			executeIfReady();
		}
		else {
			System.err.println("setParameter is allowed only when state is WAITING or FINISHED. Current state: "+curr_status);
		}
	}



	/**
	 * This is the callback to destroy this resource. If anything needs to be
	 * cleaned up when this resource is destroyed it should be done here.
	 */
	public synchronized void remove() throws ResourceException {

	}


	public OperationInputMessageDescriptor getInput_desc() {
		return input_desc;
	}


	public synchronized void setInput_desc(OperationInputMessageDescriptor input_desc) {

		Status curr_status = this.getTimestampedStatus().getStatus();
		logger.debug("[Input] status is "+curr_status); 

		if(curr_status.equals(Status.UNCONFIGURED)){


			this.input_desc = input_desc;

			// Prepare for receiving the arguments (if there's any)
			final int num_params = (this.input_desc.getInputParam() != null)? this.input_desc.getInputParam().length : 0 ; 
			this.paramData = new InputParameter[num_params];

			try {
				int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1; 
				this.setTimestampedStatus(new TimestampedStatus(Status.INPUTCONFIGURED, nextTimestamp));
			} catch (ResourceException e) {
				e.printStackTrace();
			}
		}
		else {
			logger.error("Input setting is allowed only when state is UNCONFIGURED. Current state: "+curr_status);
		}
	}


	public OperationOutputTransportDescriptor getOutput_desc() {
		return output_desc;
	}


	public synchronized void setOutput_desc(OperationOutputTransportDescriptor output_desc) {

		Status curr_status = this.getTimestampedStatus().getStatus();
		logger.debug("[Output] status is "+curr_status); 
		logger.debug("[setOutput_desc] BEGIN"); 


		if(curr_status.equals(Status.INPUTCONFIGURED)){


			this.output_desc = output_desc;
			try {
				int nextTimestamp = this.getTimestampedStatus().getTimestamp() + 1;
				this.setTimestampedStatus(new TimestampedStatus(Status.WAITING, nextTimestamp));
				
				// Skip the 'setParameter' step if we don't have any expected input. 
				// Though, if the service is secure and the credential wasn't provided yet, a deadlock might occur
				if(((this.getParamData() == null) || (this.getParamData().length == 0)) ){
					logger.debug("[setOutput_desc] No parameters needed, proceeding to execution");
					executeIfReady();
				} 

			} catch (ResourceException e) {
				e.printStackTrace();
			}

		}
		else {
			System.err.println("Output setting is allowed only when state is INPUTCONFIGURED. Current state: "+curr_status);
		}

		logger.debug("[setOutput_desc] END"); 

	}

	public InputParameter[] getParamData() {
		return paramData;
	}


	public synchronized void setParamData(InputParameter[] paramData) {
		this.paramData = paramData;
	}


	public GlobusCredential getCredential() {

		GlobusCredential retval = this.getCredentialAccess().getCredential(this.serviceOperationEPR);
		return retval;
	}


	public QName getOutputType() {
		return outputType;
	}


	public void setOutputType(QName outputType) {
		this.outputType = outputType;
	}


	public WorkflowInvocationHelperDescriptor getOperationDesc() {
		return operationDesc;
	} 


	public void setOperationDesc(WorkflowInvocationHelperDescriptor operationDesc) {
		this.operationDesc = operationDesc;
		this.isSecure = (this.operationDesc.getWorkflowInvocationSecurityDescriptor() != null);
	}




	public CredentialAccess getCredentialAccess() {
		return credentialAccess;
	}




	public void setCredentialAccess(CredentialAccess credentialAccess) {
		this.credentialAccess = credentialAccess;
	}




	public String getServiceOperationEPRString(){
		return serviceOperationEPRString;
	}




	public void setServiceOperationEPR(EndpointReference serviceOperationEPR) {
		this.serviceOperationEPR = serviceOperationEPR;
		this.serviceOperationEPRString = this.serviceOperationEPR.toString(); // This value is used as a GUID and should not be modified after this initialization
	} // */




	public boolean isSecure() {
		return isSecure;
	}




	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}




	public EndpointReference getServiceOperationEPR() {
		return serviceOperationEPR;
	}

	
	
	/*
	public void load(ResourceKey resourceKey) throws ResourceException, NoSuchResourceException, InvalidResourceKeyException {
		beingLoaded = true;
		//first we will recover the resource properties and initialize the resource
		WorkflowInvocationHelperResourceProperties props = (WorkflowInvocationHelperResourceProperties)resourcePropertyPersistenceHelper.load(WorkflowInvocationHelperResourceProperties.class, resourceKey.getValue());
		this.initialize(props, WorkflowInvocationHelperConstants.RESOURCE_PROPERTY_SET, resourceKey.getValue());

		//next we will recover the resource itself
		File file = resourcePersistenceHelper.getKeyAsFile(this.getClass(), resourceKey.getValue());
		if (!file.exists()) {
			beingLoaded = false;
			throw new NoSuchResourceException();
		}
		FileInputStream fis = null;
		int value = 0;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			SubscriptionPersistenceUtils.loadSubscriptionListeners(
					this.getTopicList(), ois);



			// Load local variables

			// Load outputType
			this.outputType = (QName) ois.readObject();


			// Load operationDesc
			this.operationDesc = (WorkflowInvocationHelperDescriptor) ois.readObject();


			// Load input_desc
			this.input_desc = (OperationInputMessageDescriptor) ois.readObject();

			// Load output_desc
			this.output_desc = (OperationOutputTransportDescriptor) ois.readObject();


			// Load paramData
			this.paramData = (InputParameter[]) ois.readObject();


			// Load credentialAccess
			this.credentialAccess = (CredentialAccess) ois.readObject();

			// Load serviceOperationEPR
			this.serviceOperationEPR = (EndpointReference) ois.readObject();			


			// Load serviceOperationEPRString
			this.serviceOperationEPRString = (String) ois.readObject();


			// Load isSecure
			this.isSecure = ois.readBoolean();

		} catch (Exception e) {
			beingLoaded = false;
			throw new ResourceException("Failed to load resource", e);
		} finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception ee) {}
			}
		} 

		beingLoaded = false;
	}


	public void store() throws ResourceException {
		if(!beingLoaded){
			//store the resource properties
			resourcePropertyPersistenceHelper.store(this);

			FileOutputStream fos = null;
			File tmpFile = null;

			try {
				tmpFile = File.createTempFile(
						this.getClass().getName(), ".tmp",
						resourcePersistenceHelper.getStorageDirectory());
				fos = new FileOutputStream(tmpFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				SubscriptionPersistenceUtils.storeSubscriptionListeners(
						this.getTopicList(), oos);


				// Store local variables

				// Write outputType
				oos.writeObject(this.outputType);

				// Write operationDesc
				oos.writeObject(this.operationDesc);


				// Write input_desc
				oos.writeObject(this.input_desc);

				// Write output_desc
				oos.writeObject(this.output_desc);


				// Write paramData
				oos.writeObject(this.paramData);

				// Write credentialAccess
				oos.writeObject(this.credentialAccess);


				// Write serviceOperationEPR
				oos.writeObject(this.serviceOperationEPR);


				// Write serviceOperationEPRString
				oos.writeObject(this.serviceOperationEPRString);
				
				// Write isSecure
				oos.writeBoolean(this.isSecure);



			} catch (Exception e) {
				if (tmpFile != null) {
					tmpFile.delete();
				}
				throw new ResourceException("Failed to store resource", e);
			} finally {
				if (fos != null) {
					try { fos.close();} catch (Exception ee) {}
				}
			}

			File file = resourcePersistenceHelper.getKeyAsFile(this.getClass(), getID());
			if (file.exists()) {
				file.delete();
			}
			if (!tmpFile.renameTo(file)) {
				tmpFile.delete();
				throw new ResourceException("Failed to store resource");
			}
		}
	} 




	@Override
	public void initialize(Object resourceBean, QName resourceElementQName,
			Object id) throws ResourceException {

		
		logger.info("[initialize] Initializing persistency objects");

		super.initialize(resourceBean, resourceElementQName, id);

		try {
			resourcePropertyPersistenceHelper = new gov.nih.nci.cagrid.introduce.servicetools.XmlPersistenceHelper(WorkflowInvocationHelperResourceProperties.class,WorkflowHelperConfiguration.getConfiguration());
			resourcePersistenceHelper = new FilePersistenceHelper(this.getClass(),WorkflowHelperConfiguration.getConfiguration(),".resource");
		} catch (Exception ex) {
			logger.warn("Unable to initialize resource properties persistence helper", ex);
		}
		
		logger.info("[initialize] END");
	} // */

}
