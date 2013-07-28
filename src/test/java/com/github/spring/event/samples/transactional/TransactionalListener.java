package com.github.spring.event.samples.transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.spring.event.annotation.Observes;
import com.github.spring.event.annotation.TransactionPhase;

/**
 * The listener interface for receiving transactional events. The class that is interested in processing a transactional
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addTransactionalListener<code> method. When
 * the transactional event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see TransactionalEvent
 */
@Service
public class TransactionalListener {

	/** Logger. */
	static Logger logger = LoggerFactory.getLogger(TransactionalListener.class);

	/**
	 * User logged in.
	 * 
	 * @param e
	 *            the event
	 */
	public void userLoggedIn(@Observes(during = TransactionPhase.IN_PROGRESS) final CreatedEvent e) {
		logger.info("catch event in progress");
		e.eventHandled("inProgress");
	}

	/**
	 * Creation before completion.
	 * 
	 * @param e
	 *            the event
	 */
	public void creationBeforeCompletion(@Observes(during = TransactionPhase.BEFORE_COMPLETION) final CreatedEvent e) {
		logger.info("catch event before completion");
		e.eventHandled("beforeCompletion");
	}

	/**
	 * Creation after completion.
	 * 
	 * @param e
	 *            the event
	 */
	public void creationAfterCompletion(@Observes(during = TransactionPhase.AFTER_COMPLETION) final CreatedEvent e) {
		logger.info("catch event after completion");
		e.eventHandled("afterCompletion");
	}

	/**
	 * Creation failure.
	 * 
	 * @param e
	 *            the event
	 */
	public void creationFailure(@Observes(during = TransactionPhase.AFTER_FAILURE) final CreatedEvent e) {
		logger.info("catch event after failure");
		e.eventHandled("afterFailure");
	}

	/**
	 * Creation success.
	 * 
	 * @param e
	 *            the event
	 */
	public void creationSuccess(@Observes(during = TransactionPhase.AFTER_SUCCESS) final CreatedEvent e) {
		logger.info("catch event after success");
		e.eventHandled("afterSuccess");
	}
}
