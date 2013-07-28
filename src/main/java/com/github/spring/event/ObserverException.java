package com.github.spring.event;

/**
 * <p>
 * Indicates that a checked exception was thrown by an observer method during event notification.
 * </p>
 */
public class ObserverException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObserverException() {

	}

	public ObserverException(final String message) {
		super(message);
	}

	public ObserverException(final Throwable cause) {
		super(cause);
	}

	public ObserverException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
