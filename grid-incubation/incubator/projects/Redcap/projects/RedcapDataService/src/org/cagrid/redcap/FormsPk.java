/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.redcap;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;


@Embeddable
public class FormsPk implements Serializable {

	
	public java.lang.String fieldName;

	
	public int projectId;

	public FormsPk(){
		
	}
	
	public FormsPk(int projectId, String fieldName){
		this.projectId = projectId;
		this.fieldName = fieldName;
	}
	
	@Column(name = "field_name",insertable=false,updatable=false)
	public java.lang.String getFieldName() {
		return fieldName;
	}

	public void setFieldName(java.lang.String fieldName) {
		this.fieldName = fieldName;
	}

	@Column(name = "project_id",insertable=false,updatable=false)
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	@Transient
	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof FormsPk))
			return false;
		FormsPk other = (FormsPk) obj;
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
