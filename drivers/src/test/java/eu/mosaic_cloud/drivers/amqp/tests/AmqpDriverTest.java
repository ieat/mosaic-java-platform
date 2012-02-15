/*
 * #%L
 * mosaic-drivers
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
package eu.mosaic_cloud.drivers.amqp.tests;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import eu.mosaic_cloud.drivers.queue.amqp.AmqpDriver;
import eu.mosaic_cloud.drivers.queue.amqp.AmqpExchangeType;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.platform.core.ops.IResult;
import eu.mosaic_cloud.platform.core.tests.TestLoggingHandler;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AmqpDriverTest {

	private static IConfiguration configuration;
	private AmqpDriver wrapper;
	private String clientId = UUID.randomUUID().toString();
	private BasicThreadingContext threadingContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AmqpDriverTest.configuration = PropertyTypeConfiguration.create(
				AmqpDriverTest.class.getClassLoader(), "amqp-test.prop");

	}

	@After
	public void tearDown() throws Exception {
		this.wrapper.destroy();
		this.threadingContext.destroy();
	}

	@Before
	public void setUp() throws IOException {
		QueueingExceptionTracer exceptions = QueueingExceptionTracer
				.create(NullExceptionTracer.defaultInstance);
		BasicThreadingSecurityManager.initialize();
		this.threadingContext = BasicThreadingContext.create(this,
				exceptions.catcher);
		this.threadingContext.initialize();
		this.wrapper = AmqpDriver.create(AmqpDriverTest.configuration,
				this.threadingContext);
	}

	public void testDriver() throws InterruptedException, ExecutionException {
		Assert.assertNotNull(this.wrapper);
	}

	public void testDeclareExchange() throws InterruptedException,
			ExecutionException {
		String exchange = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "publisher.amqp.exchange",
				String.class, "");

		IOperationCompletionHandler<Boolean> handler = new TestLoggingHandler<Boolean>(
				"declare exchange");
		IResult<Boolean> r = this.wrapper
				.declareExchange(this.clientId, exchange,
						AmqpExchangeType.DIRECT, false, false, false, handler);
		Assert.assertTrue(r.getResult());
	}

	public void testDeclareQueue() throws InterruptedException,
			ExecutionException {
		String queue = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "consumer.amqp.queue",
				String.class, "");
		IOperationCompletionHandler<Boolean> handler = new TestLoggingHandler<Boolean>(
				"declare queue");
		IResult<Boolean> r = this.wrapper.declareQueue(this.clientId, queue,
				true, false, true, false, handler);
		Assert.assertTrue(r.getResult());
	}

	public void testBindQueue() throws InterruptedException, ExecutionException {
		String exchange = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "publisher.amqp.exchange",
				String.class, "");
		String routingKey = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "publisher.amqp.routing_key",
				String.class, "");
		String queue = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "consumer.amqp.queue",
				String.class, "");
		IOperationCompletionHandler<Boolean> handler = new TestLoggingHandler<Boolean>(
				"bind queue");
		IResult<Boolean> r = this.wrapper.bindQueue(this.clientId, exchange,
				queue, routingKey, handler);
		Assert.assertTrue(r.getResult());
	}

	@Test
	public void testAll() throws InterruptedException, ExecutionException {
		testDriver();
		testDeclareExchange();
		testDeclareQueue();
		testBindQueue();
	}
}
