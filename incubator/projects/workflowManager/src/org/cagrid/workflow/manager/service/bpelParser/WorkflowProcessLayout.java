package org.cagrid.workflow.manager.service.bpelParser;

import java.util.HashMap;
import java.util.Iterator;

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


	public InvokeProperties getFirstService() {
		return firstService;
	}

	
	public String getServiceNamespace( final String prefix ){
		
		String url = this.servicesNamespaces.get(prefix);
		return url;
	}
	
	public void printClass(){
		System.out.println("Printing object type: "+WorkflowProcessLayout.class.toString() );
		System.out.println("   name = "+name);
		System.out.println("   targetNamespace = "+targetNamespace);

		// Beginning Printing the variables list
		System.out.println("BEGIN VARIABLES");
		Iterator variablesIt = variables.keySet().iterator();
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

		// Beginning Printing the partnerLinks list
		System.out.println("BEGIN PARTNER_LINKS");
		Iterator partnerLinksIt = partnerLinks.keySet().iterator();
		while(partnerLinksIt.hasNext()) {
			String key = (String) partnerLinksIt.next();
			PartnerLink auxPrint = getPartnerLink(key);
			auxPrint.printClass();
		}
		System.out.println("END PARTNER_LINKS");

		System.out.println("BEGIN ENDPOINTS");
		Iterator endPointIt = servicesNamespaces.keySet().iterator();
		while(endPointIt.hasNext()) {
			String key = (String) endPointIt.next();
			String auxPrint = (String) servicesNamespaces.get(key);//getPartnerLink(key);
			System.out.println("key = "+ key + " endpoint = "+ auxPrint);
			//auxPrint.printClass();
		}
		System.out.println("END ENDPOINTS");

		System.out.println("Begin Services");
		InvokeProperties auxIt = firstService;
		while(auxIt != null){
			auxIt.printClass();
			String auxName = auxIt.getName();

			System.out.println("ServiceName = "+auxName);
			//System.out.println("ENDPOINT = "+ auxIt.servicesEndPoint.get(auxName));
			//if(auxIt != finalService){
			auxIt = auxIt.nextService;

			//	}else{
			//	break;
			//}
		}
		System.out.println("End Services");
	}

	WorkflowProcessLayout(){
		variables = new HashMap<String, Variable>();
		partnerLinks = new HashMap<String, PartnerLink>();
		servicesNamespaces = new HashMap<String, String>();
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
			System.out.println("   Printing object type: "+PartnerLink.class.toString() );
			System.out.println("      name = "+name);
			System.out.println("      partnerLinkType = "+ partnerLinkType);
		}

	}


	// It stores the data related to the copy tags of the assign
	class CopyOutputDirective{
		private String fromVariable;
		private String fromPart;
		private String toVariable;
		private String toPart;

		public void setFromVariable(String fromVariable){
			this.fromVariable = fromVariable;
		}
		public void setFromPart(String fromPart){
			this.fromPart = fromPart;
		}
		public void setToVariable(String toVariable){
			this.toVariable = toVariable;
		}
		public void setToPart(String toPart){
			this.toPart = toPart;
		}
		public String getFromVariable(){
			return fromVariable;	
		}
		public String getFromPart(){
			return fromPart;	
		}
		public String getToVariable(){
			return toVariable;	
		}
		public String getToPart(){
			return toPart;	
		}
		void printClass(){
			System.out.println("Printing object type: "+ CopyOutputDirective.class.toString() );
			System.out.println("  fromVariable = "+ fromVariable);
			System.out.println("  fromPart = "+ fromPart);
			System.out.println("  toVariable = "+ toVariable);
			System.out.println("  toPart = "+toPart);
		}
	}

	// this class implements the support for creating if clauses
	private class BpelSwitch{
		// TODO: not implemented yet	
	}



}




