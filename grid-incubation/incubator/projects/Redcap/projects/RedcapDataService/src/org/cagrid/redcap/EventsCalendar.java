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
/**
 * EventsCalendar.java
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
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "redcap_events_calendar")
public class EventsCalendar  implements java.io.Serializable {
	
	//BIDIRECTIONAL
	@ManyToOne
	@JoinColumn(name="event_id",insertable=false,updatable=false)
    private org.cagrid.redcap.Events eventsCollection;
    
    //UNIDIRECTIONAL
	@ManyToOne
	@JoinColumn(name="project_id",insertable=false,updatable=false)
    private org.cagrid.redcap.Projects projectsCalendarRef;
   
    @Column(name = "baseline_date")
    private java.util.Date baselineDate;
	
	@Id
	@Column(name = "cal_id")
    private int calId;
	
	@Column(name = "event_date")
    private java.util.Date eventDate;
	
	@Column(name = "event_id")
    private int eventId;
	
	@Column(name = "event_status")
    private int eventStatus;
	
	@Column(name = "event_time")
    private java.lang.String eventTime;
	
	@Column(name = "group_id")
    private String groupId;
	
	@Column(name = "notes")
    private java.lang.String notes;
	
	@Column(name = "note_type")
    private java.lang.String noteType;
	
	@Column(name = "project_id")
    private int projectId;
	
	@Column(name = "record")
    private java.lang.String record;

    public EventsCalendar() {
    }

    public EventsCalendar(
           java.util.Date baselineDate,
           int calId,
           java.util.Date eventDate,
           int eventId,
           int eventStatus,
           java.lang.String eventTime,
           org.cagrid.redcap.Events eventsCollection,
           java.lang.String groupId,
           java.lang.String noteType,
           java.lang.String notes,
           int projectId,
           org.cagrid.redcap.Projects projectsCalendarRef,
           java.lang.String record) {
           this.eventsCollection = eventsCollection;
           this.projectsCalendarRef = projectsCalendarRef;
           this.baselineDate = baselineDate;
           this.calId = calId;
           this.eventDate = eventDate;
           this.eventId = eventId;
           this.eventStatus = eventStatus;
           this.eventTime = eventTime;
           this.groupId = groupId;
           this.notes = notes;
           this.noteType = noteType;
           this.projectId = projectId;
           this.record = record;
    }


    public org.cagrid.redcap.Events getEventsCollection() {
        return eventsCollection;
    }


    public void setEventsCollection(org.cagrid.redcap.Events eventsCollection) {
        this.eventsCollection = eventsCollection;
    }


    public org.cagrid.redcap.Projects getProjectsCalendarRef() {
        return projectsCalendarRef;
    }


    public void setProjectsCalendarRef(org.cagrid.redcap.Projects projectsCalendarRef) {
        this.projectsCalendarRef = projectsCalendarRef;
    }


    public java.util.Date getBaselineDate() {
        return baselineDate;
    }


    public void setBaselineDate(java.util.Date baselineDate) {
        this.baselineDate = baselineDate;
    }


    public int getCalId() {
        return calId;
    }

    public void setCalId(int calId) {
        this.calId = calId;
    }


    public java.util.Date getEventDate() {
        return eventDate;
    }


    public void setEventDate(java.util.Date eventDate) {
        this.eventDate = eventDate;
    }


    public int getEventId() {
        return eventId;
    }


    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    public int getEventStatus() {
        return eventStatus;
    }


    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }


    public java.lang.String getEventTime() {
        return eventTime;
    }


    public void setEventTime(java.lang.String eventTime) {
        this.eventTime = eventTime;
    }


    public java.lang.String getGroupId() {
        return groupId;
    }


    public void setGroupId(java.lang.String groupId) {
        this.groupId = groupId;
    }


    public java.lang.String getNotes() {
        return notes;
    }


    public void setNotes(java.lang.String notes) {
        this.notes = notes;
    }


    public java.lang.String getNoteType() {
        return noteType;
    }


    public void setNoteType(java.lang.String noteType) {
        this.noteType = noteType;
    }


    public int getProjectId() {
        return projectId;
    }


    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public java.lang.String getRecord() {
        return record;
    }


    public void setRecord(java.lang.String record) {
        this.record = record;
    }
    
    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventsCalendar)) return false;
        EventsCalendar other = (EventsCalendar) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventsCollection==null && other.getEventsCollection()==null) || 
             (this.eventsCollection!=null &&
              this.eventsCollection.equals(other.getEventsCollection()))) &&
            ((this.projectsCalendarRef==null && other.getProjectsCalendarRef()==null) || 
             (this.projectsCalendarRef!=null &&
              this.projectsCalendarRef.equals(other.getProjectsCalendarRef()))) &&
            ((this.baselineDate==null && other.getBaselineDate()==null) || 
             (this.baselineDate!=null &&
              this.baselineDate.equals(other.getBaselineDate()))) &&
            this.calId == other.getCalId() &&
            ((this.eventDate==null && other.getEventDate()==null) || 
             (this.eventDate!=null &&
              this.eventDate.equals(other.getEventDate()))) &&
            this.eventId == other.getEventId() &&
            this.eventStatus == other.getEventStatus() &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.groupId==null && other.getGroupId()==null) || 
             (this.groupId!=null &&
              this.groupId.equals(other.getGroupId()))) &&
            ((this.notes==null && other.getNotes()==null) || 
             (this.notes!=null &&
              this.notes.equals(other.getNotes()))) &&
            ((this.noteType==null && other.getNoteType()==null) || 
             (this.noteType!=null &&
              this.noteType.equals(other.getNoteType()))) &&
            this.projectId == other.getProjectId() &&
            ((this.record==null && other.getRecord()==null) || 
             (this.record!=null &&
              this.record.equals(other.getRecord())));
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
        if (getEventsCollection() != null) {
            _hashCode += getEventsCollection().hashCode();
        }
        if (getProjectsCalendarRef() != null) {
            _hashCode += getProjectsCalendarRef().hashCode();
        }
        if (getBaselineDate() != null) {
            _hashCode += getBaselineDate().hashCode();
        }
        _hashCode += getCalId();
        if (getEventDate() != null) {
            _hashCode += getEventDate().hashCode();
        }
        _hashCode += getEventId();
        _hashCode += getEventStatus();
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getGroupId() != null) {
            _hashCode += getGroupId().hashCode();
        }
        if (getNotes() != null) {
            _hashCode += getNotes().hashCode();
        }
        if (getNoteType() != null) {
            _hashCode += getNoteType().hashCode();
        }
        _hashCode += getProjectId();
        if (getRecord() != null) {
            _hashCode += getRecord().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
