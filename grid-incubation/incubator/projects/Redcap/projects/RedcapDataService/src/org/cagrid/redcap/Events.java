/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
 * Events.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.redcap;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "redcap_events_metadata")

public class Events  implements java.io.Serializable {
	
    private org.cagrid.redcap.EventsCalendar[] eventsCalendarRefs;
	
    private List<Forms> formsRefsCollection;
	
    private org.cagrid.redcap.EventArms eventArmRefs;
    
    private int eventId;  
   
	private java.lang.String eventName;

    public Events() {
    }

    public Events(
           org.cagrid.redcap.EventArms eventArmRefs,
           int eventId,
           org.cagrid.redcap.EventsCalendar[] eventsCalendarRefs,
           java.lang.String eventName,
           List<Forms> formsRefsCollection) {
           this.eventsCalendarRefs = eventsCalendarRefs;
           this.formsRefsCollection = formsRefsCollection;
           this.eventArmRefs = eventArmRefs;
           this.eventId = eventId;
           this.eventName = eventName;
    }


    //BIDIRECTIONAL
    @OneToMany(mappedBy="eventsCollection")
	@IndexColumn(name="project_id")
	
    public org.cagrid.redcap.EventsCalendar[] getEventsCalendarRefs() {
        return eventsCalendarRefs;
    }


    public void setEventsCalendarRefs(org.cagrid.redcap.EventsCalendar[] eventsCalendarRefs) {
        this.eventsCalendarRefs = eventsCalendarRefs;
    }

    public org.cagrid.redcap.EventsCalendar getEventsCalendarRefs(int i) {
        return this.eventsCalendarRefs[i];
    }

    public void setEventsCalendarRefs(int i, org.cagrid.redcap.EventsCalendar _value) {
        this.eventsCalendarRefs[i] = _value;
    }

    //BIDIRECTIONAL WORKS BUT NOT RIGHT APPROACH AS jointable has to be redcap_event_forms
    @LazyCollection(value=LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy="eventsRefsCollection")
	@NotFound(action=NotFoundAction.IGNORE)
    public List<Forms> getFormsRefsCollection() {
        return formsRefsCollection;
    }


    public void setFormsRefsCollection(List<Forms> formsRefsCollection) {
        this.formsRefsCollection = formsRefsCollection;
    }

    public org.cagrid.redcap.Forms getFormsRefsCollection(int i) {
        return this.formsRefsCollection.get(i);
    }

    public void setFormsRefsCollection(int i, org.cagrid.redcap.Forms _value) {
        this.formsRefsCollection.set(i, _value);
    }

    //BIDIRECTIONAL
    @ManyToOne
    @JoinColumn(name="arm_id")
    public org.cagrid.redcap.EventArms getEventArmRefs() {
        return eventArmRefs;
    }


    public void setEventArmRefs(org.cagrid.redcap.EventArms eventArmRefs) {
        this.eventArmRefs = eventArmRefs;
    }


    @Id
	@Column(name = "event_id")
    public int getEventId() {
        return eventId;
    }


    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    @Column(name = "descrip")
    public java.lang.String getEventName() {
        return eventName;
    }


    public void setEventName(java.lang.String eventName) {
        this.eventName = eventName;
    }
    
    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Events)) return false;
        Events other = (Events) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventsCalendarRefs==null && other.getEventsCalendarRefs()==null) || 
             (this.eventsCalendarRefs!=null &&
              java.util.Arrays.equals(this.eventsCalendarRefs, other.getEventsCalendarRefs()))) &&
            ((this.formsRefsCollection==null && other.getFormsRefsCollection()==null) || 
             (this.formsRefsCollection!=null &&
              java.util.Arrays.equals(this.formsRefsCollection.toArray(), other.getFormsRefsCollection().toArray()))) &&
            ((this.eventArmRefs==null && other.getEventArmRefs()==null) || 
             (this.eventArmRefs!=null &&
              this.eventArmRefs.equals(other.getEventArmRefs()))) &&
            this.eventId == other.getEventId() &&
            ((this.eventName==null && other.getEventName()==null) || 
             (this.eventName!=null &&
              this.eventName.equals(other.getEventName())));
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
        if (getEventsCalendarRefs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventsCalendarRefs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventsCalendarRefs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFormsRefsCollection() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFormsRefsCollection().toArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFormsRefsCollection().toArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEventArmRefs() != null) {
            _hashCode += getEventArmRefs().hashCode();
        }
        _hashCode += getEventId();
        if (getEventName() != null) {
            _hashCode += getEventName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
