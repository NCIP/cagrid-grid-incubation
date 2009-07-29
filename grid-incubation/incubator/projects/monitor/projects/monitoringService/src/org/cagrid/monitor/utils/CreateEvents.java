package org.cagrid.monitor.utils;

import java.util.List;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.monitor.db.Event;
import org.cagrid.monitor.db.Service;
import org.cagrid.monitor.db.StatusType;
import org.cagrid.monitor.db.Task;
import org.hibernate.Session;

public class CreateEvents {

	public static void loadEvents() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		List a = session.createQuery("from Event").list();
		if (!a.isEmpty())
			return;
		

		Service indexService = new Service();
		try {
			indexService
					.setEpr(new URI(
							"http://index.training.cagrid.org:8080/wsrf/services/DefaultIndexService"));
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		indexService.setEnabled(true);
		indexService.setName("Index");
		
		Service emoryService = new Service();
		try {
			emoryService
					.setEpr(new URI(
							"http://tma01.cci.emory.edu:8080/wsrf/services/cagrid/Analysis"));
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		emoryService.setEnabled(true);
		emoryService.setName("Emory");
		
		Service workflowService = new Service();
		try {
			workflowService
					.setEpr(new URI(
							"https://workflow-scufl.training.cagrid.org:8443/wsrf/services/cagrid/TavernaWorkflowService"));
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		workflowService.setEnabled(true);
		workflowService.setName("Workflow");

		Task ping = new Task();
		ping.setCommand("org.cagrid.monitor.command.Ping");
		ping.setName("ping");
		ping.setDescription("Connects to the Service's host and port to test connectivity");

		Task query = new Task();
		query.setCommand("org.cagrid.monitor.command.WSRFQuery");
		query.setName("WSRFQuery");
		query.setDescription("Retrieves the service's metadata");
		
		session.save(indexService);
		session.save(emoryService);
		session.save(workflowService);
		session.save(ping);
		session.save(query);
		
		Event event = new Event();
		event.setService(indexService);
		event.setEnabled(true);
		event.setTask(ping);
		event.setFrequency(2 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);

		event = new Event();
		event.setEnabled(true);
		event.setService(indexService);
		event.setTask(query);
		event.setFrequency(3 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);

		event = new Event();
		event.setEnabled(true);
		event.setService(emoryService);
		event.setTask(ping);
		event.setFrequency(4 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);

		event = new Event();
		event.setService(emoryService);
		event.setEnabled(true);
		event.setTask(query);
		event.setFrequency(6 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);

		event = new Event();
		event.setService(workflowService);
		event.setEnabled(true);
		event.setTask(ping);
		event.setFrequency(7 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);

		event = new Event();
		event.setEnabled(true);
		event.setService(workflowService);
		event.setTask(query);
		event.setFrequency(8 * 1000);
		event.setStatus(StatusType.AVAILABLE);
		session.save(event);
		
		session.getTransaction().commit();
	}
}
