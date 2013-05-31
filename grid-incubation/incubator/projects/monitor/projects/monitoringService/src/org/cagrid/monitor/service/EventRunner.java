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
package org.cagrid.monitor.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.cagrid.monitor.command.Command;
import org.cagrid.monitor.command.CommandResult;
import org.cagrid.monitor.command.CommandUtils;
import org.cagrid.monitor.command.Parameter;
import org.cagrid.monitor.db.Event;
import org.cagrid.monitor.db.Job;
import org.cagrid.monitor.db.ResultType;
import org.cagrid.monitor.db.Service;
import org.cagrid.monitor.db.StatusType;
import org.cagrid.monitor.db.Task;
import org.cagrid.monitor.utils.HibernateUtil;
import org.hibernate.Session;

public class EventRunner implements Runnable {
	private static Logger LOG = Logger.getLogger(EventRunner.class);
	
	private Event event = null;
	private Job currentJob = null;
	
	public EventRunner(Event event) {
		this.event = event;
	}

	public void run() {
		currentJob = new Job();
		currentJob.setStarted(new Date());
		currentJob.setEvent_id(event.getId());
		currentJob.setStatus(StatusType.RUNNING);
		currentJob.setDuration(0);
		//currentJob.setResult(StatusType.RUNNING.toString());
		event.setStatus(StatusType.RUNNING);
		event.setLastRuntime(new Date());
		saveJob();
		updateEvent();
		
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
		String formattedDate = formatter.format(currentJob.getStarted());
		LOG.error("Event: " + event.getService().getName() + "-" + event.getTask().getName() + "-Start time: " + formattedDate);
		CommandResult commandResult = doTaskOnService(event.getService(), event.getTask());
		currentJob.setDuration(new Date().getTime() - currentJob.getStarted().getTime());
		currentJob.setResult(null);
		if (commandResult.getReturnCode()==0) {
			LOG.error(event.getService().getName() + "-" + event.getTask().getName() + " TASK SUCCESS"); 
			currentJob.setResult(ResultType.SUCCESS);
		} else {
			LOG.error(event.getService().getName() + "-" + event.getTask().getName() + " TASK FAIL"); 			
			currentJob.setResult(ResultType.FAILURE);
		}
		event.setStatus(StatusType.AVAILABLE);
		event.setLastRuntime(new Date());
		currentJob.setStatus(StatusType.STOPPED);
		currentJob.setDuration((new Date()).getTime()-currentJob.getStarted().getTime());
		saveJob();
		updateEvent();
	}
	
	private void updateEvent() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.update(event);
		session.getTransaction().commit();		
	}

	private void saveJob() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(currentJob);
		session.getTransaction().commit();		
	}

	public static Event getEvent(long eventId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Event event = (Event) session.load(Event.class, eventId);
		session.getTransaction().commit();
		return event;
	}
	
	private CommandResult doTaskOnService(Service service, Task task) {
		Command commandObj = CommandUtils.loadCommand(task.getCommand());
		Parameter parameter = new Parameter("service", service);
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(parameter);
		commandObj.setParameters(parameters);
		return commandObj.run();
	}

}
