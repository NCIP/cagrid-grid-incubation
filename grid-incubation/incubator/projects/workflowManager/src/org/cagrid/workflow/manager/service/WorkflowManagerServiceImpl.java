package org.cagrid.workflow.manager.service;

import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.manager.service.conversion.WorkflowProcessLayoutConverter;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.service.bpelParser.BpelParser;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;

// TODO: ask to Hawks why we have both options
//import workflowhelperservice.OperationInputMessageDescriptor;
/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase {

	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}

	public static void writeTextFile(String contents, String fullPathFilename)
		throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
						fullPathFilename));
			writer.write(contents);
			writer.flush();
			writer.close();
		}

	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstance(
			org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowManagerInstanceDescriptor)
		throws RemoteException {
			// System.out.println("Manager Service muito doido");
			org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
			WorkflowManagerInstanceResourceHome home;
			// BaseResourceHome home = null;
			org.globus.wsrf.ResourceKey resourceKey = null;
			org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
				.getCurrentContext();
			String servicePath = ctx.getTargetService();

			// workflowManagerInstanceResourceHome
			String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
				+ servicePath + "/" + "workflowManagerInstanceHome";
			System.out.println("homeName = " + homeName);
			try {
				javax.naming.Context initialContext = new javax.naming.InitialContext();
				home = (WorkflowManagerInstanceResourceHome) initialContext
					.lookup(homeName);
				resourceKey = home.createResource();
				System.out.println("resourceKey = " + resourceKey);

				// Grab the newly created resource
				WorkflowManagerInstanceResource thisResource = (WorkflowManagerInstanceResource) home
					.find(resourceKey);

				// set the workflow descriptor on the helper instance
				thisResource
					.setWorkflowManagerInstanceDescriptor(workflowManagerInstanceDescriptor);

				String transportURL = (String) ctx
					.getProperty(org.apache.axis.MessageContext.TRANS_URL);
				transportURL = transportURL.substring(0, transportURL
						.lastIndexOf('/') + 1);
				transportURL += "WorkflowManagerInstance";

				System.out.println("transportURL = " + transportURL);
				epr = org.globus.wsrf.utils.AddressingUtils
					.createEndpointReference(transportURL, resourceKey);
			} catch (Exception e) {
				throw new RemoteException(
						"Error looking up WorkflowManagerInstance  home:"
						+ e.getMessage(), e);
			}

			String auxFileName = null;
			try {
				auxFileName = java.io.File
					.createTempFile(
							"WorkflowDescriptor_"
							+ resourceKey.toString().replace('/', '-'),
							".bpel").getAbsolutePath();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("FileName = " + auxFileName);
			// now we have to parse the bpel file we received
			// first of all I am going to write it into the current directory
			try {
				// write the
				writeTextFile(workflowManagerInstanceDescriptor.getBpelFilename(),
						auxFileName);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RemoteException(
						"ERROR: Fail to write bpel file to disk: " + auxFileName);
			}

			BpelParser parserBpelWorkflowFile = new BpelParser();

			WorkflowProcessLayout workflowLayout = parserBpelWorkflowFile
				.startParsing(auxFileName);
			// Parser Bpel done
			System.out.println("End bpel parser");
			/*
			 * try{ workflowLayout.printClass(); // DEBUG }
			 * catch(NullPointerException npe){ System.err.println("We got a null
			 * WorkflowProcessLayout. A problem may be happened while parsing the
			 * BPEL file"); return null; } //
			 */

			System.out.println("Creating workflow");
			// TODO: the id just as the workflow name is not a strong name
			System.out.println("ID = " + workflowLayout.getName());

			String helper_url = "http://localhost:8080/wsrf/services/cagrid/WorkflowHelper"; 
			// TODO
			// Retrieve this address from some service discovery mechanism

			WorkflowHelperClient helper_client = null;
			try {
				EndpointReference helper_epr = new EndpointReference(helper_url);
				helper_client = new WorkflowHelperClient(helper_epr);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}

			// System.out.println("Checkpoint 2");
			WorkflowInstanceHelperDescriptor wfi_desc = new WorkflowInstanceHelperDescriptor();
			wfi_desc.setWorkflowID(workflowLayout.getName());
			wfi_desc.setWorkflowManagerEPR(epr);

			WorkflowInstanceHelperClient wfi_client = null;
			try {
				wfi_client = helper_client.createWorkflowInstanceHelper(wfi_desc);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}

			// System.out.println("Checkpoint 3");
			WorkflowInvocationHelperDescriptor[] invocation_descs = WorkflowProcessLayoutConverter.getWorkflowInvocationHelperDescriptors(workflowLayout);

			// list of clients related to a certain operation. This is needed to be
			// used during the next
			// step, when we set the Input/OutputParameterDescriptors for each
			// operation
			List<WorkflowInvocationHelperClient> operations_clients = new ArrayList<WorkflowInvocationHelperClient>();

			if (invocation_descs.length > 0) {
				System.out.println("WorkflowID = "+ invocation_descs[0].getWorkflowID());
			}
			System.out.println("Before create infocation services");
			for(int i = 0; i < invocation_descs.length; i++){
				WorkflowInvocationHelperClient curr_invocation_client = null;
				System.out.println("Creating operation");
				try {
					curr_invocation_client = wfi_client.createWorkflowInvocationHelper(invocation_descs[i]);
				} catch (MalformedURIException e) {
					System.out.println("Error creating helperInstance Client. Operation =  "+invocation_descs[i].getOperationQName());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				operations_clients.add(curr_invocation_client);
			}
			System.out.println("after");

			for (int i = invocation_descs.length -1; i >= 0; i--) {

				// DEBUG
				System.out.println("operationName = " + invocation_descs[i].getOperationQName());
				System.out.println("outputType = " + invocation_descs[i].getOutputType());
				System.out.println("serviceURL = " + invocation_descs[i].getServiceURL());

				// HARDCODE
				final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
				final String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";

				// invocation_descs.
				WorkflowInvocationHelperClient curr_invocation_client = operations_clients.get(i);

				if(i == 1){
					org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessageDescSecondService = new OperationInputMessageDescriptor();
					InputParameterDescriptor[] inputParamsSecondService = new InputParameterDescriptor[1];
					// configure each input parameter (variable name and namespace)
					inputParamsSecondService[0] = new InputParameterDescriptor(new QName("input"), new QName(XSD_NAMESPACE, "string"));
					System.out.println("paramQName = "+inputParamsSecondService[0].getParamQName());
					inputMessageDescSecondService.setInputParam(inputParamsSecondService);
					curr_invocation_client.configureInput(inputMessageDescSecondService);

					// Creating an empty outputDescriptor
					OperationOutputTransportDescriptor outputDescriptorSecondService = new OperationOutputTransportDescriptor();
					OperationOutputParameterTransportDescriptor outParametersSecondService [] = new OperationOutputParameterTransportDescriptor[0];

					// takes the reference to no service
					outputDescriptorSecondService.setParamDescriptor(outParametersSecondService);
					curr_invocation_client.configureOutput(outputDescriptorSecondService);			
				}

				if (i == 0) {
					System.out.println("Creating operation");
					//curr_invocation_client = wfi_client.createWorkflowInvocationHelper(invocation_descs[i]);
					// curr_invocation_client.configureInput(operationInputMessageDescriptor)

					OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
					InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[0];
					inputMessage_ras.setInputParam(inputParams_ras);
					curr_invocation_client.configureInput(inputMessage_ras);

					// Creating an empty outputDescriptor
					OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
					OperationOutputParameterTransportDescriptor outParameterDescriptor_ras[] = new OperationOutputParameterTransportDescriptor[1];
					System.out.println("before set input");
					outParameterDescriptor_ras[0] = new OperationOutputParameterTransportDescriptor();
					outParameterDescriptor_ras[0].setParamIndex(0);
					outParameterDescriptor_ras[0].setType(new QName(SOAPENCODING_NAMESPACE, "string"));
					System.out.println("before set namespaces");				
					outParameterDescriptor_ras[0].setQueryNamespaces(new QName[] { new QName("http://first.cagrid.org/First","ns0")});
					//	new QName(XSD_NAMESPACE, "xsd") );
					outParameterDescriptor_ras[0].setLocationQuery("/ns0:PrintResponse");
					System.out.println("before endpoint ref");
					outParameterDescriptor_ras[0].setDestinationEPR(new EndpointReferenceType[] { operations_clients.get(1).getEndpointReference() });
					System.out.println("after endpoint ref");

					// takes the reference to no service
					outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras);
					curr_invocation_client.configureOutput(outputDescriptor_ras);

				}

				// TODO Configure I/O for current operation
				/*
				 * curr_invocation_client.configureInput(opeInputParameterDescriptorationInputMessageDescriptor);
				 * curr_invocation_client.configureOutput(operationOutputTransportDescriptor); //
				 */

				// TODO Set parameter, if any is supposed to be set statically
				// operations_clients.add(curr_invocation_client);
			}

			// return the typed EPR
			WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference();

			ref.setEndpointReference(epr);

			// DEBUG
			System.out.println("END ManagerService");

			return ref;

		}

}
