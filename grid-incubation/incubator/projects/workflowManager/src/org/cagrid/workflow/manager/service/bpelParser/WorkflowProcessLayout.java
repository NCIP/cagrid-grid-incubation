package org.cagrid.workflow.manager.service.bpelParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;


public class WorkflowProcessLayout{

	private String name;
	private String targetNamespace;

	private InvokeProperties firstService = null;
	private InvokeProperties finalService = null;

	// stores the list variables define in the workflow. The data
	// stored is encapsulated by the Variable class, defined below
	// The key is the variable name
	HashMap <String, Variable> variables;

	// It does the same of the variables, but stores PartnerLink 
	// objects. The key is also the name
	HashMap <String, PartnerLink> partnerLinks;

	// It stores the following data <key<serviceName> , namespace>
	HashMap <String, String> servicesNamespaces;
	
	// for each service we have on the system we store the command
	// to redirect its output
	//HashMap <String, ArrayList<CopyOutputDirective>> copyCommands;

		

	public InvokeProperties getFirstService() {
		return firstService;
	}

	
	public String getServiceNamespace( final String prefix ){
		
		String url = this.servicesNamespaces.get(prefix);
		return url;
	}
	
	
	/**
	 * Retrieve all registered namespace-prefix associations 
	 * 
	 * @return An array of QNames with all existing namespace-prefix associations in this instance 
	 * */
	public QName[] getAllNamespaces(){
		
		List<QName> qnames = new ArrayList<QName>(this.servicesNamespaces.size());
		
		// Retrieve all registered namespaces
		Set<Entry<String, String>> entries = this.servicesNamespaces.entrySet();
		Iterator<Entry<String, String>> entries_iter = entries.iterator();
		
		while( entries_iter.hasNext() ){
			
			Entry<String, String> curr_entry = entries_iter.next();
			QName curr_qname = new QName(curr_entry.getValue(), curr_entry.getKey());
			
			qnames.add(curr_qname);			
		}
		
		QName[] namespaces = qnames.toArray(new QName[qnames.size()]);

		
		return namespaces;
	}
	
	
	
	public void printClass(){
		System.out.println("   name = "+name);
		System.out.println("   targetNamespace = "+targetNamespace);
		// Beginning Printing the variables list
		System.out.println("BEGIN VARIABLES");
		Iterator<String> variablesIt = variables.keySet().iterator();
		while(variablesIt.hasNext()) {
			String key = (String)variablesIt.next();
			Variable auxPrint = getVariable(key);
			if(auxPrint != null){
				auxPrint.printClass();
			}else{
				System.out.println("Warnning: does not exist variable with key: "+key);
			}

		}
		System.out.println("END VARIABLES");

		System.out.println("BEGIN PARTNER_LINKS");
		Iterator<String> partnerLinksIt = partnerLinks.keySet().iterator();
		while(partnerLinksIt.hasNext()) {
			String key = (String) partnerLinksIt.next();
			PartnerLink auxPrint = getPartnerLink(key);
			auxPrint.printClass();
		}
		System.out.println("END PARTNER_LINKS");

		System.out.println("BEGIN NAMESPACES");
		Iterator<String> endPointIt = servicesNamespaces.keySet().iterator();
		while(endPointIt.hasNext()) {
			String key = (String) endPointIt.next();
			String auxPrint = (String) servicesNamespaces.get(key);//getPartnerLink(key);
			System.out.println("key = "+ key + " namespace = "+ auxPrint);
			//auxPrint.printClass();
		}
		System.out.println("END NAMESPACES");

		System.out.println("Begin Services");
		InvokeProperties auxIt = firstService;
		while(auxIt != null){
			auxIt.printClass();
			String auxName = auxIt.getName();
			System.out.println("ServiceName = "+auxName);
			auxIt = auxIt.nextService;
		}
		System.out.println("End Services");
	}

	WorkflowProcessLayout(){
		variables = new HashMap<String, Variable>();
		partnerLinks = new HashMap<String, PartnerLink>();
		servicesNamespaces = new HashMap<String, String>();
//		copyCommands =  new HashMap <String, ArrayList<CopyOutputDirective>> ();
	}

	public void setName(String name){
		this.name = name;
	}
	public void setTargetNamespace(String targetNamespace){
		this.targetNamespace = targetNamespace;
	}
	public String getName(){
		return name;	
	}
	public String getTargetNamespace(){
		return targetNamespace;	
	}

	public Variable getVariable(String key){
		return variables.get(key);
	}
	public void setEndpoint(String key, String address)throws Exception{
		try{
			System.out.println("[WorkflowProcessLayout.setEndpoint] New endpoint "+ key + '=' + address);
			
			servicesNamespaces.put(key, address);
		}catch(Exception e){
			throw new Exception(e);
		}

	}
	public void setVariable(Variable variable)throws Exception{
		try{
			variables.put(variable.getName(), variable);
		}catch(Exception e){
			throw new Exception(e);	
		}	
	}
	public void setPartnerLink(PartnerLink partnerLink) throws Exception{
		try{
			partnerLinks.put(partnerLink.getName(), partnerLink);
		}catch(Exception e){
			throw new Exception(e);
		}	
	}
	public PartnerLink getPartnerLink(String key){
		return (PartnerLink) partnerLinks.get(key);
	}

	public void addInvokeProprieties(InvokeProperties invokeProperty){
		if(firstService == null){
			firstService = invokeProperty;
			finalService = invokeProperty;
			finalService.setNextService(null);	
		}else{
			finalService.setNextService(invokeProperty);
			finalService = invokeProperty;
			invokeProperty.setNextService(null);	
		}
	}

		class PartnerLink{
		private String name;
		private QName partnerLinkType;

		// dummy functions used to set and get variables
		public void setName(String name){
			this.name = name;	
		}
		public void setPartnerLinkType(QName plink_qname){
			this.partnerLinkType = plink_qname;		
		}

		public String getName(){
			return name;	
		}
		public QName getPartnerLinkType(){
			return partnerLinkType;	
		}

		void printClass(){
			System.out.println("      name = "+name);
			System.out.println("      partnerLinkType = "+ partnerLinkType);
		}

	}



	// this class implements the support for creating if clauses
	private class BpelSwitch{
		// TODO: not implemented yet	
	}



}




