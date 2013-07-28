package com.github.spring.event.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
public @interface Observes {

	/**
	 * <p>
	 * Specifies {@linkplain com.github.spring.event.annotation.Reception under what conditions the observer method is
	 * notified}.
	 * </p>
	 * <p>
	 * By default, the observer method is notified even if no instance of the bean that defines the observer method
	 * already exists in the current context.
	 * </p>
	 */
	public Reception notifyObserver() default Reception.ALWAYS;

	/**
	 * <p>
	 * Specifies {@linkplain com.github.spring.event.annotation.Reception at what time the observer method is notified}.
	 * </p>
	 * <p>
	 * By default, the observer method is notified when the event is fired.
	 * </p>
	 */
	public TransactionPhase during() default TransactionPhase.IN_PROGRESS;
}
