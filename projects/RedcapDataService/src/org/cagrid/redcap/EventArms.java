/**
 * EventArms.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.redcap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "redcap_events_arms")
public class EventArms  implements java.io.Serializable {
	
	private org.cagrid.redcap.Events[] eventFormsCollection;
    
    private org.cagrid.redcap.Projects projectsRef;
    
    private int armId;
	
	private java.lang.String armName;
	
	private int armNum;
	
	//@Id
	private int projectId;  

    public EventArms() {
    }

    public EventArms(
           int armId,
           java.lang.String armName,
           int armNum,
           org.cagrid.redcap.Events[] eventFormsCollection,
           int projectId,
           org.cagrid.redcap.Projects projectsRef) {
           this.eventFormsCollection = eventFormsCollection;
           this.projectsRef = projectsRef;
           this.armId = armId;
           this.armName = armName;
           this.armNum = armNum;
           this.projectId = projectId;
    }

    //BIDIRECTIONAL
    @OneToMany(mappedBy="eventArmRefs")
    @IndexColumn(name="event_id")
    
    //UNIDIRECTIONAL
//   @OneToMany
//	@JoinColumn(name="arm_id")
//	@IndexColumn(name="event_id")
    public org.cagrid.redcap.Events[] getEventFormsCollection() {
        return eventFormsCollection;
    }


    public void setEventFormsCollection(org.cagrid.redcap.Events[] eventFormsCollection) {
        this.eventFormsCollection = eventFormsCollection;
    }

    public org.cagrid.redcap.Events getEventFormsCollection(int i) {
        return this.eventFormsCollection[i];
    }

    public void setEventFormsCollection(int i, org.cagrid.redcap.Events _value) {
        this.eventFormsCollection[i] = _value;
    }

    //BIDIRECTIONAL
    @ManyToOne
    @JoinColumn(name="project_id",insertable=false,updatable=false) 
    
    //UNIDIRECTIONAL
    //@Transient
    public org.cagrid.redcap.Projects getProjectsRef() {
        return projectsRef;
    }


    public void setProjectsRef(org.cagrid.redcap.Projects projectsRef) {
        this.projectsRef = projectsRef;
    }

    @Id
	@Column(name = "arm_id")
    public int getArmId() {
        return armId;
    }


    public void setArmId(int armId) {
        this.armId = armId;
    }

    @Column(name = "arm_name")
    public java.lang.String getArmName() {
        return armName;
    }


    public void setArmName(java.lang.String armName) {
        this.armName = armName;
    }

    @Column(name = "arm_num")
    public int getArmNum() {
        return armNum;
    }


    public void setArmNum(int armNum) {
        this.armNum = armNum;
    }

    @Column(name = "project_id")
    public int getProjectId() {
        return projectId;
    }


    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventArms)) return false;
        EventArms other = (EventArms) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventFormsCollection==null && other.getEventFormsCollection()==null) || 
             (this.eventFormsCollection!=null &&
              java.util.Arrays.equals(this.eventFormsCollection, other.getEventFormsCollection()))) &&
            ((this.projectsRef==null && other.getProjectsRef()==null) || 
             (this.projectsRef!=null &&
              this.projectsRef.equals(other.getProjectsRef()))) &&
            this.armId == other.getArmId() &&
            ((this.armName==null && other.getArmName()==null) || 
             (this.armName!=null &&
              this.armName.equals(other.getArmName()))) &&
            this.armNum == other.getArmNum() &&
            this.projectId == other.getProjectId();
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
        if (getEventFormsCollection() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventFormsCollection());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventFormsCollection(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getProjectsRef() != null) {
            _hashCode += getProjectsRef().hashCode();
        }
        _hashCode += getArmId();
        if (getArmName() != null) {
            _hashCode += getArmName().hashCode();
        }
        _hashCode += getArmNum();
        _hashCode += getProjectId();
        __hashCodeCalc = false;
        return _hashCode;
    }
}
