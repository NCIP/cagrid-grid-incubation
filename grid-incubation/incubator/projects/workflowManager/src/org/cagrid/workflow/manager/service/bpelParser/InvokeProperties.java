package org.cagrid.workflow.manager.service.bpelParser;

import java.util.ArrayList;

import javax.xml.namespace.QName;


// the Invoke Properties class contains all data related to the service call information
public  class InvokeProperties{
	private String name;		
	private String partnerLink;
	private QName portType;
	private QName operation;
	private String inputVariable;
	private String outputVariable;
	private boolean isReceive;
	private boolean isReply;
	ArrayList <CopyOutputDirective> copyCommandsList;

	// TODO: this implementation supports just a direct 1-1 workflow
	InvokeProperties nextService;

	InvokeProperties(){
		copyCommandsList = new ArrayList<CopyOutputDirective>();	
		isReceive = false;
		isReply = false;
	}
	
	
	// Getters and Setters
	public void setName(String name){
		this.name = name;	
	}
	public void setPartnerLink(String partnerLink){
		this.partnerLink = partnerLink;	
	}
	public void setPortType(QName operation_qname){
		this.portType = operation_qname;	
	}
	public void setOperation(QName operation){
		this.operation = operation;
	}
	public void setInputVariable(String inputVariable){
		this.inputVariable = inputVariable;
	}
	public void setOutputVariable(String outputVariable){
		this.outputVariable = outputVariable;	
	}
	public void setIsReceive(){
		this.isReceive = true;
	}
	public void setIsReply(){
		this.isReply = true;
	}
	void setNextService(InvokeProperties nextService){
		this.nextService = nextService;
	}
	public String getName(){
		return name;	
	}
	public String getPartnerLink(){
		return partnerLink;	
	}
	public QName getPortType(){
		return portType;	
	}
	public QName getOperation(){
		return operation;	
	}
	public String getInputVariable(){
		return inputVariable;		
	}
	public String getOutputVariable(){
		return outputVariable;	
	}
	public boolean getIsReceive(){
		return isReceive;	
	}
	public boolean getIsReply(){
		return isReply;	
	}
	public InvokeProperties getNextService(){
		return nextService;	
	}

	void printClass(){
		System.out.println("Invoke desc");
		System.out.println("  name = "+name);
		System.out.println("  partnerLink = "+partnerLink);
		System.out.println("  portType = "+portType);
		System.out.println("  operation = "+operation);
		System.out.println("  inputVariable = "+inputVariable);
		System.out.println("  outputVariable = "+outputVariable);
		System.out.println("  isReceive = "+isReceive);
		System.out.println("  isReply = "+isReply);
		System.out.println("  copyArraySize = "+copyCommandsList.size());
		for(int i=0 ; i < copyCommandsList.size(); i++){
			copyCommandsList.get(i).printClass();
		}
		
	}
	
	
	/**
	 * @return the copyCommandsList
	 */
	public CopyOutputDirective getCopyCommand(int index) {
		return copyCommandsList.get(index);
	}
	
	
	public int getCopyCommandSize(){
		
		return this.copyCommandsList.size();
	}
	
	
	/**
	 * @param copyCommandsList the copyCommandsList to set
	 */
	public void addCopyCommand(CopyOutputDirective copy) {
		this.copyCommandsList.add(copy);
	}
	

}