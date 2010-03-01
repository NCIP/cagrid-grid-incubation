package org.cagrid.redcap;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class DataPk implements Serializable{

	private java.lang.String fieldName;

	private int projectId;

	public DataPk(){
		
	}
	
	public DataPk(int projectId, String fieldName){
		this.projectId = projectId;
		this.fieldName = fieldName;
	}
	
	 
    @Column(name="field_name",insertable=false,updatable=false)
	public java.lang.String getFieldName() {
		return fieldName;
	}

	
	public void setFieldName(java.lang.String fieldName) {
		this.fieldName = fieldName;
	}

	@Column(name="project_id",insertable=false,updatable=false)
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	@Transient
	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DataPk))
			return false;
		DataPk other = (DataPk) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.fieldName == null && other.getFieldName() == null) || (this.fieldName != null && this.fieldName
						.equals(other.getFieldName())))
				&& this.projectId == other.getProjectId();
		__equalsCalc = null;
		return _equals;
	}

	@Transient
	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
	
		if (getFieldName() != null) {
			_hashCode += getFieldName().hashCode();
		}
		_hashCode += getProjectId();
		
		__hashCodeCalc = false;
		return _hashCode;
	}
	
}
