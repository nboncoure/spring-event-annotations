package com.github.spring.event.samples.transactional;

import com.github.spring.event.EventHandledCallback;
import com.github.spring.event.EventHandledCallbackAware;

/**
 * The Class CreatedEvent.
 */
public class CreatedEvent extends EventHandledCallbackAware {

	/**
	 * Instantiates a new created event.
	 * 
	 * @param eventHandledCallback
	 *            the event handled callback
	 */
	public CreatedEvent(final EventHandledCallback eventHandledCallback) {
		super(eventHandledCallback);
	}
}
