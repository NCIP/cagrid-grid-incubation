package org.cagrid.workflow.manager.service.bpelParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

public class Variable{
	private	String name;
	private	QName messageType; 
	// TODO Message type might be qualified by a namespace. We must store 
	//this namespace so its type are fully specified
	private List<VariablePart> parts;

	Variable(){
		parts = new ArrayList<VariablePart>();
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
	public void setPart(String partName, String type, QName messageType){
		// verify if the part have alread been inserted in the variable
		Iterator listIt = parts.iterator();
		
		// iterates over the parts and return if the part is inserted
		while(listIt.hasNext()){
			if(((VariablePart)listIt.next()).getName() == partName)return;
		}
		// we did not find the part, so we have to insert it
		VariablePart variablePartAux = new VariablePart();
		variablePartAux.setName(partName);
		variablePartAux.setNamespace(messageType);
		variablePartAux.setType(type);
		
		parts.add(variablePartAux);
	}
	
	public int getPartIndex(String partName){
		VariablePart variableAux = new VariablePart();
		variableAux.setName(partName);
		return parts.indexOf(variableAux);
	}

	public QName getPartQName(int index){
		return parts.get(index).getNamespace();
	}
	
	public String getPartType(int index){
		return parts.get(index).getType();
	}
	void printClass(){
		System.out.println("   Printing object type: "+Variable.class.toString() );
		System.out.println("      name = "+ name);
		System.out.println("      partnerLinkType = "+ messageType);
		System.out.println("      Variable Parts: "+parts.size());
		Iterator listIt = parts.iterator();
		while(listIt.hasNext()){
			//System.out.println("         "+( (VariablePart) listIt.next() ).print());
			((VariablePart)listIt.next()).print();
		}
	}
	public class VariablePart{
		String name;
		String type;
		QName namespace;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the namespace
		 */
		public QName getNamespace() {
			return namespace;
		}
		/**
		 * @param namespace the namespace to set
		 */
		public void setNamespace(QName namespace) {
			this.namespace = namespace;
		}
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}		
		public boolean equals(VariablePart part){
			return this.name == part.name;			
		}		
		public void print(){
			System.out.println("name : "+this.getName());
			System.out.println("type : "+this.getType());
			System.out.println("namespace"+this.getNamespace().toString());
		}
		
	}
}

