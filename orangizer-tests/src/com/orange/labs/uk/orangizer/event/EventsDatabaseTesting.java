package com.orange.labs.uk.orangizer.event;

import java.util.Date;
import java.util.List;

import android.test.AndroidTestCase;

import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;

public class EventsDatabaseTesting extends AndroidTestCase {

	private static final String ID = "test_id";
	private static final String SECOND_ID = "test_id2";
	private static final String NAME = "test_name";
	private static final String DESCRIPTION = "test_description";
	private static final String ADDRESS = "test_address";
	private static final Date STARTING_DATE = new Date();
	private static final Date ENDING_DATE = new Date();
	private static final RsvpStatus STATUS = RsvpStatus.ATTENDING;

	private EventsDatabase mDb;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DependencyResolverImpl.initialize(getContext());

		DependencyResolver resolver = DependencyResolverImpl.getInstance();
		mDb = resolver.getEventsDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		mDb.deleteAll();
	}

	public void testInsertion() {
		Event event = new Event.Builder().setId(ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();

		assertTrue(mDb.insert(event));

		mDb.deleteAll();

		assertEquals(0, mDb.getAllEvents().size());
	}

	public void testGetEvent() {
		Event event = new Event.Builder().setId(ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();

		assertTrue(mDb.insert(event));

		Event retrieved = mDb.getEvent(event.getId());

		assertNotNull(retrieved);

		assertEquals(retrieved.getId(), event.getId());
		assertEquals(retrieved.getName(), event.getName());
		assertEquals(retrieved.getAddress(), event.getAddress());
		assertEquals(retrieved.getDescription(), event.getDescription());
		assertEquals(retrieved.getStartingDate(), event.getStartingDate());
		assertEquals(retrieved.getEndingDate(), event.getEndingDate());
		assertEquals(retrieved.getStatus(), event.getStatus());

		mDb.deleteAll();
		assertEquals(0, mDb.getAllEvents().size());
	}

	public void testDeleteAll() {
		Event event = new Event.Builder().setId(ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();
		assertTrue(mDb.insert(event));

		event = new Event.Builder().setId(SECOND_ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();

		assertTrue(mDb.insert(event));

		assertEquals(2, mDb.getAllEvents().size());

		mDb.deleteAll();

		assertEquals(0, mDb.getAllEvents().size());
	}

	public void testDeleteEvent() {
		Event event = new Event.Builder().setId(ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();
		assertTrue(mDb.insert(event));

		assertTrue(mDb.delete(event));

		assertNull(mDb.getEvent(event.getId()));
	}

	public void testGetAllEvents() {
		Event firstEvent = new Event.Builder().setId(ID).setName(NAME).setDescription(DESCRIPTION)
				.setAddress(ADDRESS).setStartingDate(STARTING_DATE).setEndingDate(ENDING_DATE)
				.setStatus(STATUS).build();
		assertTrue(mDb.insert(firstEvent));

		Event secondEvent = new Event.Builder().setId(SECOND_ID).setName(NAME)
				.setDescription(DESCRIPTION).setAddress(ADDRESS).setStartingDate(STARTING_DATE)
				.setEndingDate(ENDING_DATE).setStatus(STATUS).build();
		assertTrue(mDb.insert(secondEvent));
		
		List<Event> events = mDb.getAllEvents();
		assertEquals(2, events.size());
		
		assertEquals(firstEvent.getId(), events.get(0).getId());
		assertEquals(secondEvent.getId(), events.get(1).getId());
		
		mDb.deleteAll();
		assertEquals(0, mDb.getAllEvents().size());
	}

}
