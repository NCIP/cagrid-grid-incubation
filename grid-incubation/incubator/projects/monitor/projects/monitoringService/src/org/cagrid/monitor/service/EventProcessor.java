package org.cagrid.monitor.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.cagrid.monitor.db.Event;
import org.cagrid.monitor.db.StatusType;
import org.cagrid.monitor.utils.CreateEvents;
import org.cagrid.monitor.utils.HibernateUtil;
import org.hibernate.Session;

public class EventProcessor  implements Runnable {
	private static Logger LOG = Logger.getLogger(EventProcessor.class);
	
	private boolean processingEvents = false;
	private int exp = 1;
	
	private static ExecutorService exec = null;
	
	public EventProcessor() {
		if (exec == null)
			exec = Executors.newFixedThreadPool(5);
	}
	
	public void run() {
		this.startMonitor();
	}
	
	public void startMonitor() {
		processingEvents = true;

		Collection<Event> eventsToRun = null;
		while(processingEvents) {
			eventsToRun = getRunnableEvents();

			for (Event event : eventsToRun) {
				event.setStatus(StatusType.PENDING);
				updateDB(event);
				EventRunner eventRunner = new EventRunner(event);
				exec.execute(eventRunner);
			}
			pollingDelay();
		}		
	}

	private void pollingDelay() {
		if (exp > 4) {
			exp = 1;
		} else {
			exp *= 2;
		}
		LOG.error("Delaying: "  + exp*1000);
		try {
			Thread.sleep(exp*1000);
		} catch (InterruptedException e) {
			stopMonitor();
			LOG.error("InterruptedException");
		}
	}
	
	public void stopMonitor() {
		processingEvents = false;		
		exec.shutdown();
	}
	
	public boolean isRunning() {
		return processingEvents;
	}
	
	private Collection<Event> getEvents() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Collection<Event> events = session.createQuery("from Event").list();
		session.getTransaction().commit();
		return events;
	}

	private void updateDB(Object obj) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.update(obj);
		session.getTransaction().commit();
	}

	private Collection<Event> getRunnableEvents() {
		Collection<Event> events = getEvents();
		ArrayList<Event> eventsToRun = new ArrayList<Event>();
		for (Event event : events) {
			Date nextRuntime = null;
			if (event.getLastRuntime()!=null) {
				nextRuntime = new Date(event.getLastRuntime().getTime()+event.getFrequency()); 
			} else {
				nextRuntime = new Date(0);
			}
			if (nextRuntime.before(new Date()) && event.getStatus().equals(StatusType.AVAILABLE)) {
				eventsToRun.add(event);
			}
		}
		return eventsToRun;
	}


	public static void main(String[] args) {
		CreateEvents.loadEvents();
		
		Thread eventProcessorThread = new Thread(new EventProcessor());
		eventProcessorThread.start();
	}
	
}