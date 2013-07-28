package com.github.spring.event.annotation;

public enum TransactionPhase {

	/**
	 * <p>
	 * Identifies a regular observer method, called when the event is fired.
	 * </p>
	 */
	IN_PROGRESS,

	/**
	 * <p>
	 * Identifies a before completion observer method, called during the before completion phase of the transaction.
	 * </p>
	 */
	BEFORE_COMPLETION,

	/**
	 * <p>
	 * Identifies an after completion observer method, called during the after completion phase of the transaction.
	 * </p>
	 */
	AFTER_COMPLETION,

	/**
	 * <p>
	 * Identifies an after failure observer method, called during the after completion phase of the transaction, only
	 * when the transaction fails.
	 * </p>
	 */
	AFTER_FAILURE,

	/**
	 * <p>
	 * Identifies an after success observer method, called during the after completion phase of the transaction, only
	 * when the transaction completes successfully.
	 * </p>
	 */
	AFTER_SUCCESS
}
