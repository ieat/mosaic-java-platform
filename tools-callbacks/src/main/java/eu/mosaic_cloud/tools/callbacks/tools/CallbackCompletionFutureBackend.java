/*
 * #%L
 * mosaic-tools-callbacks
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

package eu.mosaic_cloud.tools.callbacks.tools;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletionBackend;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletionObserver;
import eu.mosaic_cloud.tools.callbacks.core.CallbackReactor;
import eu.mosaic_cloud.tools.threading.tools.Threading;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;


public final class CallbackCompletionFutureBackend<_Outcome_ extends Object>
			implements
				CallbackCompletionBackend
{
	private CallbackCompletionFutureBackend (final ListenableFuture<_Outcome_> future, final Executor executor) {
		super ();
		Preconditions.checkNotNull (future);
		Preconditions.checkNotNull (executor);
		this.future = future;
		this.executor = executor;
		this.completion = CallbackCompletion.createDeferred (this);
	}
	
	@Override
	public final boolean awaitCompletion (final CallbackCompletion<?> completion, final long timeout) {
		Preconditions.checkArgument (completion == this.completion);
		final Object outcome = Threading.awaitOrCatch ((ListenableFuture<Object>) this.future, timeout, CallbackCompletionFutureBackend.timeoutMarker, CallbackCompletionFutureBackend.exceptionMarker);
		if (outcome == CallbackCompletionFutureBackend.timeoutMarker)
			return (false);
		if (outcome == CallbackCompletionFutureBackend.exceptionMarker)
			return (true);
		return (true);
	}
	
	@Override
	public final Throwable getCompletionException (final CallbackCompletion<?> completion) {
		Preconditions.checkArgument (completion == this.completion);
		final Object outcome;
		try {
			outcome = Threading.await ((ListenableFuture<Object>) this.future, 0, CallbackCompletionFutureBackend.timeoutMarker);
		} catch (final ExecutionException exception) {
			return (exception.getCause ());
		}
		if (outcome == CallbackCompletionFutureBackend.timeoutMarker)
			throw (new IllegalStateException ());
		return (null);
	}
	
	@Override
	public final Object getCompletionOutcome (final CallbackCompletion<?> completion) {
		Preconditions.checkArgument (completion == this.completion);
		final Object outcome = Threading.awaitOrCatch ((ListenableFuture<Object>) this.future, 0, CallbackCompletionFutureBackend.timeoutMarker, CallbackCompletionFutureBackend.exceptionMarker);
		if (outcome == CallbackCompletionFutureBackend.timeoutMarker)
			throw (new IllegalStateException ());
		if (outcome == CallbackCompletionFutureBackend.exceptionMarker)
			throw (new IllegalStateException ());
		return (outcome);
	}
	
	@Override
	public final CallbackReactor getReactor () {
		return (null);
	}
	
	@Override
	public final void observeCompletion (final CallbackCompletion<?> completion, final CallbackCompletionObserver observer) {
		Preconditions.checkArgument (completion == this.completion);
		Preconditions.checkNotNull (observer);
		this.future.addListener (new Observer (observer), this.executor);
	}
	
	final void triggerObserver (final CallbackCompletionObserver observer) {
		CallbackCompletion.triggerObserver (this.completion, observer);
	}
	
	public final CallbackCompletion<_Outcome_> completion;
	private final Executor executor;
	private final ListenableFuture<_Outcome_> future;
	
	public static final <_Outcome_ extends Object> CallbackCompletion<_Outcome_> createCompletion (final ListenableFuture<_Outcome_> future) {
		return (new CallbackCompletionFutureBackend<_Outcome_> (future, CallbackCompletionFutureBackend.inlineExecutor).completion);
	}
	
	private static final Object exceptionMarker = new Object ();
	private static final Executor inlineExecutor = MoreExecutors.sameThreadExecutor ();
	private static final Object timeoutMarker = new Object ();
	
	private final class Observer
				extends Object
				implements
					Runnable
	{
		Observer (final CallbackCompletionObserver observer) {
			super ();
			this.observer = observer;
			this.triggered = new AtomicBoolean (false);
		}
		
		@Override
		public final void run () {
			Preconditions.checkState (this.triggered.compareAndSet (false, true));
			CallbackCompletionFutureBackend.this.triggerObserver (this.observer);
		}
		
		private final CallbackCompletionObserver observer;
		private final AtomicBoolean triggered;
	}
}
