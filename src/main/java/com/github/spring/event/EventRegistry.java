package com.github.spring.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import com.github.spring.event.TransactionSynchronizedRunnable.Status;
import com.github.spring.event.annotation.TransactionPhase;

@SuppressWarnings("rawtypes")
public class EventRegistry implements Event {
	private static final Logger LOG = LoggerFactory.getLogger(EventRegistry.class);

	private List<DefaultEvent> notQualifiedEvents = new ArrayList<DefaultEvent>();
	private List<QualifiedEvent> qualifiedEvents = new ArrayList<QualifiedEvent>();
	private Executor executor;

	public EventRegistry() {

	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void registerEvent(Object bean, Method method, Class<?> parameterType, TransactionPhase transactionPhase,
	        Annotation qualifier) {
		Assert.notNull(bean, "Cannot register event: Item reference cannot be null");
		Assert.notNull(method, "Cannot register event: Method reference cannot be null");
		Assert.notNull(parameterType, "Cannot register event: Paremeter type cannot be null");
		Assert.notNull(transactionPhase, "Cannot register event: Transaction phase cannot be null");

		if (qualifier == null) {
			notQualifiedEvents.add(new DefaultEvent(bean, method, parameterType, transactionPhase));
		} else {
			qualifiedEvents.add(new QualifiedEvent(bean, method, parameterType, transactionPhase, qualifier));
		}
	}

	public void fire(final Object observable) {
		LOG.info("fire event for {}", observable.getClass().getName());
		for (final DefaultEvent e : notQualifiedEvents) {
			if (!e.isSupported(observable.getClass())) {
				continue;
			}
			execute(observable, e);
		}
	}

	void fire(final Object observable, final Annotation qualifier) {
		LOG.info("fire qualified event \"{}\", \"{}\"", observable.getClass().getName(), qualifier);
		boolean observerFound = false;
		for (final QualifiedEvent e : qualifiedEvents) {
			if (!e.isSupported(observable.getClass(), qualifier)) {
				continue;
			}
			observerFound = true;
			execute(observable, e);
		}
		if (!observerFound) {
			LOG.warn("observer not found for qualified event \"{}\", \"{}\"", observable.getClass().getName(),
			        qualifier);
		}
	}

	private void execute(final Object event, final DefaultEvent listener) {
		final Runnable deferredEvent = new Runnable() {
			@Override
			public void run() {
				listener.doFire(event);
			}
		};

		if (TransactionPhase.IN_PROGRESS.equals(listener.getTransactionPhase())
		        || !TransactionSynchronizationManager.isSynchronizationActive()) {
			// immediate dispatch
			executor.execute(deferredEvent);
		} else {
			final Runnable transactionalDeferredEvent = new Runnable() {
				@Override
				public void run() {
					executor.execute(deferredEvent);
				}
			};

			switch (listener.getTransactionPhase()) {
			case BEFORE_COMPLETION:
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizedRunnable(
				        transactionalDeferredEvent, true));
				break;
			case AFTER_COMPLETION:
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizedRunnable(
				        transactionalDeferredEvent, false));
				break;
			case AFTER_SUCCESS:
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizedRunnable(
				        transactionalDeferredEvent, Status.SUCCESS));
				break;
			case AFTER_FAILURE:
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizedRunnable(
				        transactionalDeferredEvent, Status.FAILURE));
				break;
			}
		}
	}

	private class DefaultEvent {
		protected final Class<?> parameterType;
		private final Method method;
		private final Object bean;

		/** The transaction phase. */
		private final TransactionPhase transactionPhase;

		private DefaultEvent(Object bean, Method method, Class<?> parameterType, TransactionPhase transactionPhase) {
			this.bean = bean;
			this.method = method;
			this.parameterType = parameterType;
			this.transactionPhase = transactionPhase;
		}

		private boolean isSupported(Class<?> eventType) {
			return parameterType.isAssignableFrom(eventType);
		}

		public TransactionPhase getTransactionPhase() {
			return transactionPhase;
		}

		void doFire(Object event) {
			try {
				method.invoke(bean, event);
			} catch (Throwable e) {
				throw new RuntimeException("error while handling event " + event.getClass().getName(), e);
			}
		}

	}

	private class QualifiedEvent extends DefaultEvent {

		private final Annotation qualifier;

		private QualifiedEvent(Object bean, Method method, Class<?> parameterType, TransactionPhase transactionPhase,
		        Annotation qualifier) {
			super(bean, method, parameterType, transactionPhase);
			this.qualifier = qualifier;
		}

		private boolean isSupported(Class<?> eventType, Annotation qaulifier) {
			return parameterType.isAssignableFrom(eventType) && this.qualifier.equals(qaulifier);
		}
	}
}
