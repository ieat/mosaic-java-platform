/*
 * #%L
 * mosaic-platform-core
 * %%
 * Copyright (C) 2010 - 2013 Institute e-Austria Timisoara (Romania)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package eu.mosaic_cloud.drivers.ops;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.mosaic_cloud.drivers.exceptions.NullCompletionCallback;
import eu.mosaic_cloud.tools.exceptions.core.FallbackExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.BaseExceptionTracer;


/**
 * Basic implementation of an asynchronous operation. It uses Java {@link FutureTask} to implement the asynchronism.
 * 
 * @author Georgiana Macariu
 * @param <T>
 *            The type of the actual result of the asynchronous operation.
 */
public class GenericOperation<T>
			implements
				IOperation<T>
{
	/**
	 * Creates a new operation.
	 * 
	 * @param operation
	 *            the code to run
	 */
	public GenericOperation (final Callable<T> operation) {
		super ();
		this.operation = new GenericTask (operation);
		this.exceptions = FallbackExceptionTracer.defaultInstance;
	}
	
	/**
	 * Cancels the asynchronous operation.
	 * 
	 * @return <code>true</code> if operation was cancelled
	 */
	@Override
	public boolean cancel () {
		boolean cancelled = true;
		if ((this.operation != null) && (cancelled = this.operation.cancel (true))) {
			assert this.getHandler () != null : "Operation callback is NULL.";
			this.getHandler ().onFailure (new NullCompletionCallback ("Operation callback is NULL."));
		}
		return cancelled;
	}
	
	/**
	 * Waits if necessary for the computation to complete, and then retrieves its result.
	 * 
	 * @return the computed result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Override
	public T get ()
				throws InterruptedException, ExecutionException {
		T result = null;
		if (this.operation != null) {
			try {
				result = this.operation.get ();
			} catch (final ExecutionException e) {
				// FIXME: customize exception
				final Throwable e1 = e.getCause ();
				if (e1 instanceof UnsupportedOperationException) {
					this.exceptions.traceHandledException (e);
					this.exceptions.traceDeferredException (e1);
					this.getHandler ().onFailure (e1);
				} else {
					this.exceptions.traceIgnoredException (e);
				}
			}
		}
		return result;
	}
	
	/**
	 * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
	 * available.
	 * 
	 * @param timeout
	 *            the maximum time to wait
	 * @param unit
	 *            the time unit of the timeout argument
	 * @return the computed result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws TimeoutException
	 *             if the wait timed out
	 */
	@Override
	public T get (final long timeout, final TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
		T result = null;
		if (this.operation != null) {
			try {
				result = this.operation.get (timeout, unit);
			} catch (final ExecutionException e) {
				// FIXME: customize exception
				final Throwable e1 = e.getCause ();
				if (e1 instanceof UnsupportedOperationException) {
					this.exceptions.traceHandledException (e);
					this.exceptions.traceDeferredException (e1);
					this.getHandler ().onFailure (e1);
				} else {
					this.exceptions.traceIgnoredException (e);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the completion handler to be called when operation completes.
	 * 
	 * @return the completion handler to be called when operation completes
	 */
	public IOperationCompletionHandler<T> getHandler () {
		return this.complHandler;
	}
	
	/**
	 * Returns the enclosed {@link FutureTask} object which manages the actual execution of the operation.
	 * 
	 * @return the enclosed {@link FutureTask} object
	 */
	public FutureTask<T> getOperation () {
		return this.operation;
	}
	
	/**
	 * Returns <code>true</code> if this task was cancelled before it completed normally.
	 * 
	 * @return <code>true</code> if this task was cancelled before it completed
	 */
	@Override
	public boolean isCancelled () {
		boolean cancelled = true;
		if (this.operation != null) {
			cancelled = this.operation.isCancelled ();
		}
		return cancelled;
	}
	
	/**
	 * Returns <code>true</code> if this task completed. Completion may be due to normal termination, an exception, or
	 * cancellation -- in all of these cases, this method will return <code>true</code>.
	 * 
	 * @return <code>true</code> if this task completed
	 */
	@Override
	public boolean isDone () {
		boolean done = false;
		if (this.operation != null) {
			done = this.operation.isDone ();
		}
		return done;
	}
	
	/**
	 * Sets the completion handler to be called when operation completes.
	 * 
	 * @param compHandler
	 *            the completion handler to be called when operation completes
	 */
	public void setHandler (final IOperationCompletionHandler<T> complHandler) {
		this.complHandler = complHandler;
		this.cHandlerSet.countDown ();
	}
	
	private final CountDownLatch cHandlerSet = new CountDownLatch (1);
	private IOperationCompletionHandler<T> complHandler;
	private final BaseExceptionTracer exceptions;
	private final FutureTask<T> operation;
	
	private final class GenericTask
				extends FutureTask<T>
	{
		public GenericTask (final Callable<T> callable) {
			super (callable);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.FutureTask#run()
		 */
		@Override
		public void run () {
			super.run ();
			try {
				GenericOperation.this.cHandlerSet.await ();
				GenericOperation.this.complHandler.onSuccess (GenericTask.super.get ());
			} catch (final InterruptedException e) {
				GenericOperation.this.exceptions.traceIgnoredException (e);
			} catch (final ExecutionException e) {
				// FIXME: customize exception
				final Throwable e1 = e.getCause ();
				if (e1 instanceof UnsupportedOperationException) {
					GenericOperation.this.exceptions.traceHandledException (e);
					GenericOperation.this.exceptions.traceDeferredException (e1);
					GenericOperation.this.getHandler ().onFailure (e1);
				} else {
					GenericOperation.this.exceptions.traceIgnoredException (e);
				}
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.util.concurrent.FutureTask#setException(java.lang.Throwable)
		 */
		@Override
		protected void setException (final Throwable exception) {
			super.setException (exception);
			try {
				GenericOperation.this.cHandlerSet.await ();
			} catch (final InterruptedException e) {
				GenericOperation.this.exceptions.traceIgnoredException (e);
			}
			GenericOperation.this.complHandler.onFailure (exception);
		}
	}
}
