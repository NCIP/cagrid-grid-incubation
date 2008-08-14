package org.cagrid.workflow.helper.instrumentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.cagrid.workflow.helper.descriptor.EventTimePeriod; 


/** A set of measurements associated to a specific type of event. It is allowed to store multiple records for the same event. */
public class InstrumentationRecord {


	private String identifier = null;   // A name for this record
	private Map<Integer, EventTimePeriod > records = null;    // List of event times
	private int timestamp = -1;   // Logical time to order the observed events


	public InstrumentationRecord(String identifier) {
		this.identifier = identifier;
		this.records = new HashMap<Integer, EventTimePeriod>();
	}


	public String getIdentifier() {
		return identifier;
	}


	/** Generate an unique identifier for an event. This method assumes it will be called only once 
	 * for each observed event and also in the order the events occur. */
	private int generateKeyForEvent(String event){

		return /*event.toString() + '_' + */this.timestamp;
	}


	/** Add a measurement to this record. COLATERAL EFFECT: Internal timestamp is incremented within this method */
	public void addRecord(EventTimePeriod measurement) throws Exception {

		String subject = measurement.getEvent();

		++this.timestamp;  // Update logical time for the next event
		int event_key = this.generateKeyForEvent(subject);

		if( !this.records.containsKey(event_key) ){

			this.records.put(this.timestamp, measurement);

		}
		else throw new Exception("Event "+ subject.toString() +" already recorded. Can't record duplicated events");
	}


	public void removeRecord(String subject){

		int event_key = this.generateKeyForEvent(subject);
		if( this.records.containsKey(event_key) ){

			this.records.remove(event_key);
		}
	}


	public void replaceRecord(EventTimePeriod measurement) throws Exception {

		String subject = measurement.getEvent();
		this.removeRecord(subject);
		this.records.put(this.timestamp, measurement);
	}


	/** Mark the start of an event 
	 * @throws Exception */
	public void eventStart(String event) throws Exception{

		EventTimePeriod record = new EventTimePeriod();
		long startTime = System.nanoTime();
		
		record.setEvent(event);			
		record.setStartTime(startTime);
		this.addRecord(record);
	}


	/** Mark the end of an event 
	 * @throws Exception */
	public void eventEnd(String event) throws Exception{

		EventTimePeriod record = this.getRecord(event);
		if(record == null){
			throw new Exception("Could not find the record for the received event");
		}

		long endTime = System.nanoTime();
		record.setEndTime(endTime);
		this.replaceRecord(record);

		return;
	}



	/** Retrieve the record for an event */
	public EventTimePeriod getRecord(String subject){

		int event_key = this.generateKeyForEvent(subject);
		return this.records.get(event_key);
	}


	/** Build an iterator to the set of records store in this object */
	public Iterator<EventTimePeriod> iterator(){
		return new InstrumentationRecordIterator(this);
	}

	
	/** Build an array containing all the stored instrumented events */
	public EventTimePeriod[] retrieveRecordAsArray(){
		
		EventTimePeriod[] records_array = new EventTimePeriod[this.records.size()];  // Array to return
		
		Set<Entry<Integer, EventTimePeriod>> recordsSet = this.records.entrySet();
		Iterator<Entry<Integer, EventTimePeriod>> records_iterator = recordsSet.iterator();
		
		// Build the array in a way the events will be ordered by timestamp
		while( records_iterator.hasNext() ){
			
			Entry<Integer, EventTimePeriod> curr_record = records_iterator.next();
			records_array[curr_record.getKey()] = curr_record.getValue();
		}
		
		return records_array;
	}


	/** Class that enables inspection of each measurement stored within this record */
	public class InstrumentationRecordIterator implements Iterator<EventTimePeriod> {


		private InstrumentationRecord record;
		private Iterator<Entry<Integer, EventTimePeriod>> entries_iterator;



		public InstrumentationRecordIterator(InstrumentationRecord record){
			this.record = record;
			Set<Entry<Integer, EventTimePeriod>> entries = this.record.records.entrySet();
			this.entries_iterator = entries.iterator();
		}


		public boolean hasNext() {
			return this.entries_iterator.hasNext();
		}


		public void remove() {
			this.entries_iterator.remove();			
		}


		public EventTimePeriod next() {

			Entry<Integer, EventTimePeriod> next_entry = this.entries_iterator.next();
			return next_entry.getValue();			
		}

	}

}

