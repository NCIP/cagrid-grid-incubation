/**
 * Data.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.redcap;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "redcap_data" ,
		uniqueConstraints={@UniqueConstraint(columnNames={"field_name","project_id"})})

public class Data  implements java.io.Serializable {
    

    private	DataPk pk;
	
	@EmbeddedId
	public DataPk getPk() {
		return pk;
	}

	public void setPk(DataPk pk) {
		this.pk = pk;
	}
	
	
	private org.cagrid.redcap.Forms dataRef;
    	
	private int eventId; 
	
	private java.lang.String fieldName; 
	
	private int projectId;
    
	private int record;
	
	private java.lang.String value;

    public Data() {
    }

    public Data(
           org.cagrid.redcap.Forms dataRef,
           int eventId,
           java.lang.String fieldName,
           int projectId,
           int record,
           java.lang.String value) {
           this.dataRef = dataRef;
           this.eventId = eventId;
           this.fieldName = fieldName;
           this.projectId = projectId;
           this.record = record;
           this.value = value;
           getPk().setFieldName(fieldName);
           getPk().setProjectId(projectId);
    }

    //BIDIRECTIONAL forms-data
    
    @LazyCollection(LazyCollectionOption.EXTRA)
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "field_name",referencedColumnName="field_name",insertable=false,updatable=false),
		@JoinColumn(name = "project_id",referencedColumnName="project_id",insertable=false,updatable=false)
	
	})
	@NotFound(action=NotFoundAction.IGNORE)
    
	//UNIDIRECTIONAL
	//@Transient
    public org.cagrid.redcap.Forms getDataRef() {
        return dataRef;
    }


    public void setDataRef(org.cagrid.redcap.Forms dataRef) {
        this.dataRef = dataRef;
    }


    @Column(name="event_id")
    public int getEventId() {
        return eventId;
    }


    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    @Column(name="field_name",insertable=false,updatable=false)
    public java.lang.String getFieldName() {
      	return getPk().getFieldName();
    }


   public void setFieldName(java.lang.String fieldName) {
    	getPk().setFieldName(fieldName);
    }


    @Column(name="project_id",insertable=false,updatable=false)
    public int getProjectId() {
   	   return getPk().getProjectId();
    }


    public void setProjectId(int projectId) {
       	getPk().setProjectId(projectId);
    }


    @Column(name="record",insertable=false,updatable=false)
    public int getRecord() {
        return record;
    }


    public void setRecord(int record) {
        this.record = record;
    }


    @Column(name="value")
    public java.lang.String getValue() {
        return value;
    }


    public void setValue(java.lang.String value) {
        this.value = value;
    }
    
    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Data)) return false;
        Data other = (Data) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataRef==null && other.getDataRef()==null) || 
             (this.dataRef!=null &&
              this.dataRef.equals(other.getDataRef()))) &&
            this.eventId == other.getEventId() &&
            ((getPk().getFieldName()==null && other.getPk().getFieldName()==null) || 
                    (getPk().getFieldName()!=null &&
                     getPk().getFieldName().equals(other.getPk().getFieldName()))) &&
                     
            getPk().getProjectId() == other.getPk().getProjectId() &&
            this.record == other.getRecord() &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue())));
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
        if (getDataRef() != null) {
            _hashCode += getDataRef().hashCode();
        }
        _hashCode += getEventId();
        if (getFieldName() != null) {
            _hashCode += getFieldName().hashCode();
        }
        _hashCode += getProjectId();
        _hashCode += getRecord();
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
