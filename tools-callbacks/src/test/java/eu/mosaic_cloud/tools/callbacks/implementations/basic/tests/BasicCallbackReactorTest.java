/*
 * #%L
 * mosaic-tools-callbacks
 * %%
 * Copyright (C) 2010 - 2012 Institute e-Austria Timisoara (Romania)
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

package eu.mosaic_cloud.tools.callbacks.implementations.basic.tests;


import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import eu.mosaic_cloud.tools.callbacks.core.CallbackFuture;
import eu.mosaic_cloud.tools.callbacks.core.CallbackReference;
import eu.mosaic_cloud.tools.callbacks.implementations.basic.BasicCallbackReactor;
import eu.mosaic_cloud.tools.callbacks.tools.QueueCallbacks;
import eu.mosaic_cloud.tools.callbacks.tools.QueueingQueueCallbacks;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import eu.mosaic_cloud.tools.threading.tools.Threading;

import org.junit.Assert;
import org.junit.Test;


public final class BasicCallbackReactorTest
{
	@Test
	public final void test ()
			throws Exception
	{
		BasicThreadingSecurityManager.initialize ();
		final QueueingExceptionTracer exceptions = QueueingExceptionTracer.create (NullExceptionTracer.defaultInstance);
		final BasicThreadingContext threading = BasicThreadingContext.create (this, exceptions.catcher);
		final BasicCallbackReactor reactor = BasicCallbackReactor.create (threading, exceptions);
		Assert.assertTrue (reactor.initialize (BasicCallbackReactorTest.defaultPollTimeout));
		final LinkedList<QueueCallbacks<Integer>> triggers = new LinkedList<QueueCallbacks<Integer>> ();
		for (int index = 0; index < BasicCallbackReactorTest.defaultQueueCount; index++) {
			final QueueCallbacks<Integer> trigger = reactor.register (QueueCallbacks.class, null);
			triggers.add (trigger);
		}
		final ConcurrentLinkedQueue<CallbackFuture> futures = new ConcurrentLinkedQueue<CallbackFuture> ();
		{
			int counter = 0;
			for (int index = 0; index < BasicCallbackReactorTest.defaultCallCount; index++) {
				for (final QueueCallbacks<Integer> trigger : triggers) {
					final CallbackReference reference = trigger.enqueue (Integer.valueOf (counter));
					final CallbackFuture future = reactor.resolve (reference);
					futures.add (future);
					counter++;
				}
			}
		}
		for (final QueueCallbacks<Integer> trigger : triggers) {
			final CallbackReference reference = trigger.enqueue (Integer.valueOf (-1));
			final CallbackFuture future = reactor.resolve (reference);
			Assert.assertTrue (future.cancel (false));
		}
		final LinkedList<QueueingQueueCallbacks<Integer>> callbacks = new LinkedList<QueueingQueueCallbacks<Integer>> ();
		for (int index = 0; index < BasicCallbackReactorTest.defaultQueueCount; index++) {
			final QueueingQueueCallbacks<Integer> callback = QueueingQueueCallbacks.create ();
			callbacks.add (callback);
			reactor.assign (triggers.get (index), callback);
		}
		for (final CallbackFuture future : futures)
			Assert.assertNull (future.get (BasicCallbackReactorTest.defaultPollTimeout, TimeUnit.MILLISECONDS));
		Threading.sleep (BasicCallbackReactorTest.defaultPollTimeout);
		{
			int counter = 0;
			for (int index = 0; index < BasicCallbackReactorTest.defaultCallCount; index++)
				for (final QueueingQueueCallbacks<Integer> callback : callbacks) {
					Assert.assertEquals (Integer.valueOf (counter), callback.queue.poll ());
					counter++;
				}
			for (final QueueingQueueCallbacks<Integer> callback : callbacks)
				Assert.assertNull (callback.queue.poll ());
		}
		Assert.assertTrue (reactor.terminate (BasicCallbackReactorTest.defaultPollTimeout));
		Assert.assertTrue (threading.join (BasicCallbackReactorTest.defaultPollTimeout));
		Assert.assertNull (exceptions.queue.poll ());
	}
	
	public static final int defaultCallCount = 16;
	public static final long defaultPollTimeout = 1000;
	public static final int defaultQueueCount = 16;
}
