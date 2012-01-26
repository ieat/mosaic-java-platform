/*
 * #%L
 * mosaic-examples-simple-components
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

package eu.mosaic_cloud.examples.components.simple;


import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import eu.mosaic_cloud.components.core.ComponentCallReference;
import eu.mosaic_cloud.components.core.ComponentCallReply;
import eu.mosaic_cloud.components.core.ComponentCallRequest;
import eu.mosaic_cloud.components.core.ComponentIdentifier;
import eu.mosaic_cloud.components.implementations.basic.BasicChannel;
import eu.mosaic_cloud.components.implementations.basic.BasicComponent;
import eu.mosaic_cloud.components.tools.DefaultChannelMessageCoder;
import eu.mosaic_cloud.components.tools.QueueingComponentCallbacks;
import eu.mosaic_cloud.tools.callbacks.implementations.basic.BasicCallbackReactor;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import eu.mosaic_cloud.tools.threading.tools.Threading;

import org.junit.Assert;
import org.junit.Test;


public class AbacusTest
{
	@Test
	public final void test ()
			throws Throwable
	{
		BasicThreadingSecurityManager.initialize ();
		final Pipe pipe1 = Pipe.open ();
		final Pipe pipe2 = Pipe.open ();
		final QueueingExceptionTracer exceptions = QueueingExceptionTracer.create (NullExceptionTracer.defaultInstance);
		final BasicThreadingContext threading = BasicThreadingContext.create (this, exceptions.catcher);
		final ComponentIdentifier peer = ComponentIdentifier.resolve (Strings.repeat ("00", 20));
		final BasicCallbackReactor reactor = BasicCallbackReactor.create (threading, exceptions);
		final DefaultChannelMessageCoder coder = DefaultChannelMessageCoder.defaultInstance;
		final BasicChannel serverChannel = BasicChannel.create (pipe1.source (), pipe2.sink (), coder, reactor, threading, exceptions);
		final BasicChannel clientChannel = BasicChannel.create (pipe2.source (), pipe1.sink (), coder, reactor, threading, exceptions);
		final BasicComponent serverComponent = BasicComponent.create (serverChannel, reactor, exceptions);
		final BasicComponent clientComponent = BasicComponent.create (clientChannel, reactor, exceptions);
		final AbacusComponentCallbacks serverCallbacks = new AbacusComponentCallbacks (exceptions);
		final QueueingComponentCallbacks clientCallbacks = QueueingComponentCallbacks.create (clientComponent);
		Assert.assertTrue (reactor.initialize (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (serverChannel.initialize (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (clientChannel.initialize (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (serverComponent.initialize (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (clientComponent.initialize (AbacusTest.defaultPollTimeout));
		serverComponent.assign (serverCallbacks);
		clientCallbacks.assign ();
		for (int index = 0; index < AbacusTest.defaultTries; index++) {
			final double operandA = (int) (Math.random () * 10);
			final double operandB = (int) (Math.random () * 10);
			final ComponentCallRequest request = ComponentCallRequest.create ("+", Arrays.asList (Double.valueOf (operandA), Double.valueOf (operandB)), ByteBuffer.allocate (0), ComponentCallReference.create ());
			clientComponent.call (peer, request);
			final ComponentCallReply reply = (ComponentCallReply) clientCallbacks.queue.poll (AbacusTest.defaultPollTimeout, TimeUnit.MILLISECONDS);
			Assert.assertNotNull (reply);
			Assert.assertTrue (reply.ok);
			Assert.assertNotNull (reply.outputsOrError);
			Assert.assertEquals (request.reference, reply.reference);
			Assert.assertTrue ((operandA + operandB) == ((Number) reply.outputsOrError).doubleValue ());
		}
		pipe1.sink ().close ();
		pipe2.sink ().close ();
		Assert.assertTrue (serverComponent.terminate (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (clientComponent.terminate (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (serverChannel.terminate (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (clientChannel.terminate (AbacusTest.defaultPollTimeout));
		Threading.sleep (AbacusTest.defaultPollTimeout);
		Assert.assertTrue (reactor.terminate (AbacusTest.defaultPollTimeout));
		Assert.assertTrue (threading.await (AbacusTest.defaultPollTimeout));
		Assert.assertNull (exceptions.queue.poll ());
	}
	
	public static final long defaultPollTimeout = 1000;
	public static final int defaultTries = 16;
}