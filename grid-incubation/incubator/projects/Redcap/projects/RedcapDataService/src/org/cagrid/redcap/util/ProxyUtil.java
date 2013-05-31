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
package org.cagrid.redcap.util;

import java.util.ArrayList;
import org.cagrid.redcap.Data;
import org.cagrid.redcap.EventArms;
import org.cagrid.redcap.Events;
import org.cagrid.redcap.EventsCalendar;
import org.cagrid.redcap.Forms;
import org.cagrid.redcap.Projects;

public class ProxyUtil {

	public Object getProxy(Object object) {
		if (object instanceof Projects) {
			object = getProjectsProxy(object);
		} else if (object instanceof EventArms) {
			object = getEventArmsProxy(object);
		} else if (object instanceof Events) {
			object = getEventsProxy(object);
		} else if (object instanceof EventsCalendar) {
			object = getEventsCalendarProxy(object);
		} else if (object instanceof Forms) {
			object = getFormsProxy(object);
		} else if (object instanceof Data) {
			object = getDataProxy(object);
		}
		return object;
	}

	private Object getProjectsProxy(Object projects) {
		Projects projectsTemp = (Projects) projects;
		projectsTemp.setEventArmsCollection(new EventArms[0]);
		projectsTemp.setEventsCalendarRef(new EventsCalendar[0]);
		projectsTemp.setFormsRefCollection(new Forms[0]);
		return projectsTemp;
	}

	private Object getEventArmsProxy(Object eventArms) {
		EventArms eventArmsTemp = (EventArms) eventArms;
		eventArmsTemp.setEventFormsCollection(new Events[0]);
		eventArmsTemp.setProjectsRef(new Projects());
		return eventArmsTemp;
	}

	private Object getEventsProxy(Object events) {
		Events eventsTemp = (Events) events;
		eventsTemp.setEventArmRefs(new EventArms());
		eventsTemp.setEventsCalendarRefs(new EventsCalendar[0]);
		eventsTemp.setFormsRefsCollection(new ArrayList<Forms>());
		return eventsTemp;
	}

	private Object getEventsCalendarProxy(Object eventsCalendar) {
		EventsCalendar eventsCalendarTemp = (EventsCalendar) eventsCalendar;
		eventsCalendarTemp.setEventsCollection(new Events());
		eventsCalendarTemp.setProjectsCalendarRef(new Projects());
		return eventsCalendarTemp;
	}

	private Object getDataProxy(Object data) {
		Data dataTemp = (Data) data;
		dataTemp.setDataRef(new Forms());
		return dataTemp;
	}

	private Object getFormsProxy(Object forms) {
		Forms formsTemp = (Forms)forms;
		formsTemp.setEventsRefsCollection(new Events[0]);
		formsTemp.setProjectsFormsRef(new Projects());
		formsTemp.setFormsRef(new Data[0]);
		return formsTemp;
	}
}
