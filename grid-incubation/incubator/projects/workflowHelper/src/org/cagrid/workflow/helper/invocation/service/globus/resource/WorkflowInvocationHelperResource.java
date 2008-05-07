package org.cagrid.workflow.helper.invocation.service.globus.resource;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Text;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.invocation.DeliveryEnumerator;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.ConversionUtil;
import org.cagrid.workflow.helper.util.ServiceInvocationUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceException;
import org.w3c.dom.Node;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;


/** 
 * The implementation of this WorkflowInvocationHelperResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInvocationHelperResource extends WorkflowInvocationHelperResourceBase {


	private QName outputType = null;
	private OperationInputMessageDescriptor input_desc = null;
	private OperationOutputTransportDescriptor output_desc = null;
	private InputParameter[] paramData = null;
	private WorkflowInvocationHelperDescriptor operationDesc = null;

	private GlobusCredential proxy = null;


	public synchronized boolean executeIfReady() {

		for (int i = 0; i < paramData.length; i++) {
			if (paramData[i] == null) {
				return false;
			}
		}

		try {
			setStatus(Status.RUNNING);
			//System.out.println("Set status to RUNNING"); // DEBUG
		} catch (ResourceException e2) {
			e2.printStackTrace();
		}

		final Thread th = new Thread(new Runnable() {

			public synchronized void run() {


				//DEBUG
				//System.out.println("-- Thread started");

				// we have all the input data needed to execute so lets execute
				// 1. make execution call with axis
				List<Node> service_response = new ArrayList<Node>();
				try {

					final boolean invocationIsSecure = (getProxy() != null);

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

								//DEBUG
								//System.out.println("Invoking "+ getWorkflowInvocationHelperDescriptor().getOperationQName().getLocalPart() +" for element: "+curr_array_str);


								// Invoke service according to its security configuration 
								Node response_node = null;
								if( invocationIsSecure ){
									
									response_node = ServiceInvocationUtil.generateSecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(), 
											new_input_params, getProxy()); 									
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

						/* Invoke service according to its security configuration */
						if( invocationIsSecure ){

							service_response.add(ServiceInvocationUtil.generateSecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(), 
									input_value, getProxy()));
						}
						else {

							service_response.add(ServiceInvocationUtil.generateUnsecureRequest(getOperationDesc(), getInput_desc(), getOutput_desc(),
									input_value));
						}

					}

				} catch (Exception e1) {
					e1.printStackTrace();
					try {
						setStatus(Status.ERROR);
						//System.out.println("Set status to ERROR"); // DEBUG
					} catch (ResourceException e) {
						e.printStackTrace();
					}
				}



				// DEBUG See list contents
				/*Node curr_node = null;
				System.out.println("----------------------------");
				for(ListIterator<Node> it = svc_response.listIterator(); it.hasNext(); ){
					curr_node = it.next();
					System.out.println("Curr node is: "+curr_node);
				}
				System.out.println("----------------------------");
				System.out.flush(); // */



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

								//DEBUG
								//System.out.println("\tfor query '" + pdesc.getLocationQuery() + "' we got\t'"+ data +"'");

								// send the data to the next workflow helper instance
								if( pdesc.getDestinationEPR() != null ){
									//DEBUG
									/*System.out.println("Setting data '"+ iparam.getData() +"'");
									System.out.flush(); // */

									
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
									System.err.print("No destination assigned to current parameter.");
									System.err.println("Value of parameter is: \n"+iparam.getData());
									System.err.flush();
								}

							} catch (MalformedURIException e) {
								try {
									setStatus(Status.ERROR);
									//System.out.println("Set status to ERROR"); // DEBUG
								} catch (ResourceException e1) {
									e1.printStackTrace();
								}
								e.printStackTrace();
							} catch (RemoteException e) {
								try {
									setStatus(Status.ERROR);
								//	System.out.println("Set status to ERROR"); // DEBUG
								} catch (ResourceException e1) {
									e1.printStackTrace();
								}
								e.printStackTrace();
							}
						}
					}

				}
				try {
					setStatus(Status.FINISHED);
					System.out.println("Set status to FINISHED ("+ getOperationDesc().getServiceURL() +")"); // DEBUG
				} catch (ResourceException e) {
					e.printStackTrace();
				}

				//DEBUG
				//System.out.println("-- Thread finished");
				return;
			}

			

		});

		/*       if (getWorkflowHelperInstanceStatusValue().getStatus() == null
            || getWorkflowHelperInstanceStatusValue().getStatus() != Status.RUNNING) {
            getWorkflowHelperInstanceStatusValue().setStatus(Status.RUNNING);
            th.start();
        }*/

		/* Start thread and wait for it to finish */
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
	private static void printParameters(InputParameter[] paramData){


		String output = "";
		output += "INPUT RECEIVED [printParameters]:\n";
		for(int i=0; i < paramData.length; i++){
			output += '['+paramData[i].getData()+" : "+paramData[i].getParamIndex()+"]\n";
		}
		output += "END PRINT PARAMETERS\n";
		System.out.println(output);
		System.out.flush();
		return;
	}


	public synchronized void setParameters(InputParameter[] params) {
		for (int i = 0; i < params.length; i++) {
			setParameter(params[i]);
		}
	}


	public synchronized void setParameter(InputParameter param) {

		if (param != null) {
			paramData[param.getParamIndex()] = param;
		}

		// poll to see if we can execute
		executeIfReady();
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
		this.input_desc = input_desc;

		// Prepare for receiving the arguments (if there's any)
		final int num_params = (this.input_desc.getInputParam() != null)? this.input_desc.getInputParam().length : 0 ; 
		paramData = new InputParameter[num_params];

		try {
			if( getOutput_desc() == null ){
				// status is still UNCONFIGURED

				this.setStatus(Status.UNCONFIGURED);

			}
			else { // both inputs and outputs ARE configured

				this.setStatus(Status.WAITING);
				executeIfReady(); // will run when no parameters are required
			}
		} catch (ResourceException e) {
			e.printStackTrace();
		}

	}


	public OperationOutputTransportDescriptor getOutput_desc() {
		return output_desc;
	}


	public synchronized void setOutput_desc(OperationOutputTransportDescriptor output_desc) {
		this.output_desc = output_desc;

		try {
			if( getOutput_desc() == null ){
				// status is still UNCONFIGURED
				this.setStatus(Status.UNCONFIGURED);
			}
			else { // both inputs and outputs ARE configured

				this.setStatus(Status.WAITING);
				executeIfReady(); // will run when no parameters are required
			}
		} catch (ResourceException e) {
			e.printStackTrace();
		}

	}

	public InputParameter[] getParamData() {
		return paramData;
	}


	public synchronized void setParamData(InputParameter[] paramData) {
		this.paramData = paramData;
	}


	public GlobusCredential getProxy() {
		return proxy;
	}


	public synchronized void setProxy(GlobusCredential proxy) {
		this.proxy = proxy;
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
	}

}
