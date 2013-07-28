package com.github.spring.event.annotation;

/**
 * <p>
 * Distinguishes conditional {@linkplain com.github.spring.event.annotation.Observes observer methods} from observer methods
 * which are always notified.
 * </p>
 * <p>
 * A conditional observer method is an observer method which is notified of an event only if an instance of the bean
 * that defines the observer method already exists in the current context.
 * </p>
 */
public enum Reception {
	/**
	 * <p>
	 * Specifies that an observer method is only called if the current instance of the bean declaring the observer
	 * method already exists.
	 * </p>
	 * <p>
	 * If there is no active context for the scope to which the bean declaring this observer method belongs, then the
	 * observer method is not called.
	 * </p>
	 */
	IF_EXISTS,

	/**
	 * Specifies that an observer method always receives event notifications.
	 */
	ALWAYS

}
