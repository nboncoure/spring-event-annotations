package com.github.spring.event;

import org.springframework.transaction.support.TransactionSynchronization;

public class TransactionSynchronizedRunnable implements TransactionSynchronization {

	private final Status desiredStatus;
	private final Runnable task;
	private final boolean before;

	public enum Status {
		ALL, SUCCESS, FAILURE
	}

	public TransactionSynchronizedRunnable(final Runnable task, final boolean before) {
		this(task, Status.ALL, before);
	}

	public TransactionSynchronizedRunnable(final Runnable task, final Status desiredStatus) {
		this(task, desiredStatus, false); // Status is only applicable after the transaction
	}

	private TransactionSynchronizedRunnable(final Runnable task, final Status desiredStatus, final boolean before) {
		this.task = task;
		this.desiredStatus = desiredStatus;
		this.before = before;
	}

	@Override
	public void suspend() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void flush() {

	}

	@Override
	public void beforeCommit(final boolean readOnly) {

	}

	@Override
	public void beforeCompletion() {
		if (before) {
			task.run();
		}
	}

	@Override
	public void afterCommit() {

	}

	@Override
	public void afterCompletion(final int status) {
		if (!before) {
			if (desiredStatus == Status.SUCCESS && status == STATUS_COMMITTED || desiredStatus == Status.FAILURE
			        && status != STATUS_COMMITTED || desiredStatus == Status.ALL) {
				task.run();
			}
		}
	}

}