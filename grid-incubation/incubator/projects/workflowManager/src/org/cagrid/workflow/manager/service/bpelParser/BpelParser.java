package org.cagrid.workflow.manager.service.bpelParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.oasisOpen.docs.wsbpel.x20.process.executable.ProcessDocument;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TAssign;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TCopy;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TInvoke;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TProcess;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TSequence;
import org.tempuri.xmlSchema.OperationSignature;
import org.tempuri.xmlSchema.OperationsDescriptor;
import org.tempuri.xmlSchema.OperationsDescriptorDocument;
import org.tempuri.xmlSchema.OutputType;
import org.tempuri.xmlSchema.Parameter;
import org.tempuri.xmlSchema.ServiceDesc;
import org.tempuri.xmlSchema.Signatures;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BpelParser {


	private BpelParser() {

	}



	/** 
	 * Build a list of workflow portions. Each portion belongs to the same container and its componentes
	 * are an InstanceHelper and corresponding InvocationHelpers.
	 * 
	 *  @param bpelDoc BPEL document describing a workflow
	 *  @param operationsDesc 
	 *  @return An array of workflow portions
	 *  
	 * */
	// InstanceHelper descriptor unnecessary
	// TODO A portion should comprise of an entire container, not a service
	public static WorkflowPortionDescriptor[] extractWorkflowPortions(
			ProcessDocument bpelDoc, OperationsDescriptorDocument operationsDesc, String workflowID) {


		WorkflowPortionDescriptor[] portions;



		OperationsDescriptor rootElement = operationsDesc.getOperationsDescriptor();  // Retrieve document's root element
		ServiceDesc[] serviceArray = rootElement.getServiceArray();   // Retrieve description of services
		portions = new WorkflowPortionDescriptor[serviceArray.length];    // The number of InstanceHelpers is the same as the number of service descriptions


		// Create description for each portion
		for(int i=0 ; i < serviceArray.length; i++){


			ServiceDesc curr_service = serviceArray[i];
			portions[i] = new WorkflowPortionDescriptor();


			// Retrieve WorkflowHelperService URL
			try {
				URL serviceURL = new URL(curr_service.getServiceURL());

				// Infer the URL of the InstanceHelper which is responsible for the current InvocationHelper
				String instanceHelperURL = serviceURL.getProtocol() + "://" + serviceURL.getHost() + ':' 
				+ serviceURL.getPort() + "/wsrf/services/cagrid/WorkflowHelper";
				portions[i].setWorkflowHelperServiceLocation(instanceHelperURL);	

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}



			// Create descriptions for the operations managed by the current InstanceHelper
			WorkflowStageDescriptor[] invocationHelperDescs = BpelParser.extractWorkflowStageDescriptors(curr_service.getOperations(),
					curr_service.getServiceNamespace(), curr_service.getServiceURL(), workflowID); 
			portions[i].setInvocationHelperDescs(invocationHelperDescs);
		}


		return portions;
	}



	/**
	 * Build the description for each operation contained in the received object. 
	 * Note: Connections between stages are not determined within this method.
	 * 
	 * @param operationsAncestor Object that contains operation descriptions
	 * @param workflowID 
	 * @return Array of operation descriptions in the form of WorkflowStageDescriptor objects 
	 * */
	private static WorkflowStageDescriptor[] extractWorkflowStageDescriptors(
			Signatures operationsAncestor, String serviceNamespace, String serviceURL, String workflowID) {


		OperationSignature[] operations = operationsAncestor.getSignatureArray();
		WorkflowStageDescriptor[] stagesDescription = new WorkflowStageDescriptor[operations.length];


		// Create decription for each individual stage
		for(int j=0; j < operations.length; j++){


			OperationSignature curr_signature = operations[j];
			stagesDescription[j] = new WorkflowStageDescriptor();



			// Create basic description
			WorkflowInvocationHelperDescriptor basicDescription  = new WorkflowInvocationHelperDescriptor();
			QName operationQname = new QName(serviceNamespace, curr_signature.getName());
			basicDescription.setOperationQName(operationQname);
			basicDescription.setServiceURL(serviceURL);
			basicDescription.setWorkflowID(workflowID);
//			basicDescription.setWorkflowInvocationSecurityDescriptor(workflowInvocationSecurityDescriptor); // Coming soon...

			OutputType output = curr_signature.getOutput();
			if( output != null ){
				QName outputType = new QName(output.getOutputTypeNamespace(), output.getOutputType());
				basicDescription.setOutputType(outputType);
			}
			stagesDescription[j].setBasicDescription(basicDescription);



			// Create description of inputs
			OperationInputMessageDescriptor inputsDescription = new OperationInputMessageDescriptor();
			Parameter[] inputs = curr_signature.getInputArray();
			InputParameterDescriptor[] inputParam = new InputParameterDescriptor[0];

			if(inputs != null){

				inputParam = new InputParameterDescriptor[inputs.length];
				for(int k=0; k < inputs.length; k++){  // Iterate over each input parameter described

					Parameter curr_input = inputs[k];
					inputParam[k] = new InputParameterDescriptor();
					inputParam[k].setParamQName(new QName(curr_input.getName()));
					inputParam[k].setParamType(new QName(curr_input.getTypeNamespace(), curr_input.getType()));
				}

			}
			inputsDescription.setInputParam(inputParam);
			stagesDescription[j].setInputsDescription(inputsDescription);

			// Description of output transport can't be done here because we don't have the EPR for each stage yet.

		}

		return stagesDescription;
	}



	/**
	 * Retrieve the invoke commands associated with each operation within a BPEL process
	 * 
	 * @param bpelDoc Description of a BPEL process
	 * @return All invoke commands associated with each operation
	 * @throws Exception 
	 * */
	public static Map<String, List<TInvoke> > extractInvokeOperations(
			ProcessDocument bpelDoc, QName[] namespaces) throws Exception {


		HashMap<String, List<TInvoke> > invokeOperations = new HashMap<String, List<TInvoke> >();




		TProcess processElement = bpelDoc.getProcess();

		// Get simple invoke activity, if any
		TInvoke invokeCmd = processElement.getInvoke();
		if( invokeCmd != null ){


			String operationName = invokeCmd.getOperation(); 
			List<TInvoke> currList = (invokeOperations.containsKey(operationName))?  invokeOperations.get(operationName) : new ArrayList<TInvoke>();

			currList.add(invokeCmd);
			invokeOperations.put(operationName, currList);

		}


		// Get the invoke commands within any structured activities

		// For now, we only implement the retrieval for a sequence activity 
		TSequence sequence = processElement.getSequence();
		invokeOperations = extractInvokeOperationsFromSequence(sequence, invokeOperations, namespaces);


		return invokeOperations;
	}



	/* Retrieve all invoke commands within a sequence element and add it to the existing associations */
	private static HashMap<String, List<TInvoke>> extractInvokeOperationsFromSequence(
			TSequence sequence, HashMap<String, List<TInvoke>> invokeOperations, QName[] namespaces) throws Exception {

		HashMap<String, List<TInvoke>> invokeCmds = invokeOperations;


		// Retrieve all invoke commands within the received sequence element and associate them with the corresponding operation QName
		TInvoke[] invokeArray = sequence.getInvokeArray();
		if( invokeArray != null ){


			for(int i=0; i < invokeArray.length; i++){


				TInvoke curr_invoke = invokeArray[i];
				QName operationQName = new QName(curr_invoke.getOperation()); //getQNameForElement(curr_invoke.getOperation(), namespaces);
				String key = operationQName.toString();   
				List<TInvoke> currList = (invokeCmds.containsKey(key))?  invokeCmds.get(key) : new ArrayList<TInvoke>();

				currList.add(curr_invoke);
				invokeCmds.put(key, currList);

			}
		}



		// Get the invoke commands within any nested structured activities

		// For now, we only implement the retrieval for a sequence activity 
		TSequence[] innerSequences = sequence.getSequenceArray();
		if( innerSequences != null ){

			// Retrieve the invoke commands recursively
			for(int i=0 ; i < innerSequences.length; i++){

				invokeCmds = extractInvokeOperationsFromSequence(innerSequences[i], invokeCmds, namespaces);
			}
		}


		return invokeCmds;
	}


	private static QName getQNameForElement(String element, QName[] namespaces) throws Exception {
		
		
		// Retrieve prefix that references the operation namespace
		String prefix = "tns";  // By default, the operation belongs to the default namespace
		String elementNoPrefix = element; // "Pure" operation name - without any namespace prefix
		int colonIndex = element.indexOf((int)':');
		if( colonIndex != -1 ){
		
			elementNoPrefix = element.substring(colonIndex + 1);
			prefix = element.substring(0, colonIndex);
		}
		
		
		// Search for the prefix among the received namespaces
		String namespace = null;
		for(int i=0 ; i < namespaces.length; i++){
		
			QName currNS = namespaces[i];
			String currNS_prefix = currNS.getLocalPart();
			if( prefix.equals(currNS_prefix) ){
				
				namespace = currNS.getNamespaceURI();
				break;
			}
		}
		if( namespace == null )  throw new Exception("Namespace for element "+ element +" doesn't exist among given namespaces");
		
		
		
		QName elementQname = new QName(namespace, elementNoPrefix);
		return elementQname;
	}



	/**
	 * Retrieve the copy commands within a BPEL document and group them by the source variable they refer to.
	 * 
	 * 
	 * */
	public static Map<String, ArrayList<TCopy> > extractCopyOperations(
			ProcessDocument bpelDoc) {



		Map<String, ArrayList<TCopy>> copyOperations = new HashMap<String, ArrayList<TCopy> >();
		TProcess processElement = bpelDoc.getProcess();


		// Get simple invoke activity, if any
		TAssign copyCmd = processElement.getAssign();
		if( copyCmd != null ){


			TCopy[] copies = copyCmd.getCopyArray();

			for(int i=0 ; i < copies.length; i++){

				TCopy curr_copyCmd = copies[i];

				// Retrieve the current source variable
				String sourceVariable = curr_copyCmd.getFrom().getVariable();

				// Update list of copy commands associated with the output variable
				ArrayList<TCopy> curr_list = copyOperations.containsKey(sourceVariable) ? copyOperations.get(sourceVariable) : new ArrayList<TCopy>();
				curr_list.add(curr_copyCmd);
				copyOperations.put(sourceVariable, curr_list);
			}

		}


		// Get the invoke commands within any structured activities

		// For now, we only implement the retrieval for a sequence activity 
		TSequence sequence = processElement.getSequence();
		copyOperations = extractCopyOperationsFromSequence(sequence, copyOperations);



		return copyOperations;

	}



	private static Map<String, ArrayList<TCopy>> extractCopyOperationsFromSequence(
			TSequence sequence, Map<String, ArrayList<TCopy>> copyOperations) {


		Map<String, ArrayList<TCopy>> copyCmds = copyOperations;


		// Retrieve all copy commands within the received sequence element and associate them with the corresponding input variable
		TAssign[] assignArray = sequence.getAssignArray();

		if( assignArray != null ){

			for(int i=0; i < assignArray.length; i++){


				TCopy[] copyArray = assignArray[i].getCopyArray();
				if( copyArray != null ){


					for(int j=0; j < copyArray.length; j++){


						TCopy curr_copy = copyArray[j];
						String inputVariableName = curr_copy.getFrom().getVariable(); 
						ArrayList<TCopy> currList = (copyCmds.containsKey(inputVariableName))?  copyCmds.get(inputVariableName) : new ArrayList<TCopy>();

						currList.add(curr_copy);
						copyCmds.put(inputVariableName, currList);
					}
				}
			}
			
			
			
			// Get the copy commands within any nested structured activities

			// For now, we only implement the retrieval for a sequence activity 
			TSequence[] innerSequences = sequence.getSequenceArray();
			if( innerSequences != null ){

				// Retrieve the copy commands recursively
				for(int j=0 ; j < innerSequences.length; j++){

					copyCmds = extractCopyOperationsFromSequence(innerSequences[j], copyCmds);
				}
			}
		}

		return copyCmds;
	}

	
	/* Obtain a list of all namespaces declared within a BPEL document. We assume the namespaces are declared
	 * in the document's root element */
	public static QName[] extractNamespaces(ProcessDocument bpelDoc) throws Exception {
		
		ArrayList<QName> processNamespaces = new ArrayList<QName>(); 
		
		TProcess process = bpelDoc.getProcess();
		QName tns = new QName(process.getTargetNamespace(), "tns");
		processNamespaces.add(tns);
		
		
		// Retrieve root element's attributes
		Node processNode = process.getDomNode();
		NamedNodeMap attribs = processNode.getAttributes();
		for(int i=0; i < attribs.getLength() ; i++ ){
			
			
			Node curr_attrib = attribs.item(i);
			String attrName = curr_attrib.getNodeName();
			String attr_value = curr_attrib.getNodeValue();
			
			
			Pattern nsDeclPattern = Pattern.compile("^xmlns:.*");
			Matcher m = nsDeclPattern.matcher(attrName);
			if( m.matches() ){
				
				// Store namespace prefix and URI
				int colonIndex = attrName.indexOf((int)':');
				if( colonIndex != -1 ){
				
					String prefix = attrName.substring(colonIndex + 1);
					String namespaceURI = attr_value;
					
					processNamespaces.add(new QName(namespaceURI, prefix));
				}
				else throw new Exception("Found invalid name for namespace declaration: "+ attrName);
			}	
		}
		
		
		
		return processNamespaces.toArray(new QName[0]);
	}
	

}



