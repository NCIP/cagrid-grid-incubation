package org.cagrid.workflow.manager.service.bpelParser;

import javax.xml.namespace.QName;

public class Variable{
	private	String name;
	private	QName messageType; 
	// TODO Message type might be qualified by a namespace. We must store this namespace so its type are fully specified

	Variable(){
	}
	// dummy functions used to set and get variables
	public void setName(String name){
		this.name = name;
	}
	public void setMessageType(QName messageType){
		this.messageType = messageType;	
	}
	public String getName(){
		return name;	
	}

	public QName getMessageType(){
		return messageType;	
	}
	void printClass(){
		System.out.println("   Printing object type: "+Variable.class.toString() );
		System.out.println("      name = "+ name);
		System.out.println("      partnerLinkType = "+ messageType);
	}
}

