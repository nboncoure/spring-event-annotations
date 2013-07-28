package com.github.spring.event.samples.transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.spring.event.Event;
import com.github.spring.event.EventHandledCallback;
import com.github.spring.event.samples.qualifiers.Item;
import com.github.spring.event.samples.qualifiers.ItemCreated;

@Service
public class ObjectEntityService {

	/** Logger. */
	static Logger logger = LoggerFactory.getLogger(ObjectEntityService.class);

	/** The item created event. */
	@Autowired
	@ItemCreated
	private Event<Item> itemCreatedEvent;

	/** The repository. */
	@Autowired
	private ObjectEntityRepository repository;

	/** The created event. */
	@Autowired
	private Event<CreatedEvent> createdEvent;

	/** The event handled callback. */
	private EventHandledCallback eventHandledCallback;

	@Autowired
	public void setRepository(final ObjectEntityRepository repository) {
		this.repository = repository;
	}

	/**
	 * Sets the event handled callback.
	 * 
	 * @param eventHandledCallback
	 *            the new event handled callback
	 */
	public void setEventHandledCallback(final EventHandledCallback eventHandledCallback) {
		this.eventHandledCallback = eventHandledCallback;
	}

	public ObjectEntity findById(Long id) {
		return null;
	}

	@Transactional
	public ObjectEntity create(final ObjectEntity resource) {
		logger.info("fire event");
		createdEvent.fire(new CreatedEvent(eventHandledCallback));
		itemCreatedEvent.fire(new Item(eventHandledCallback));

		logger.info("persist entity");
		return repository.save(resource);
	}
}
