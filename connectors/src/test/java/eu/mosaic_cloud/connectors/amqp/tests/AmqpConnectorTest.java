/*
 * #%L
 * mosaic-connectors
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
package eu.mosaic_cloud.connectors.amqp.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.mosaic_cloud.connectors.kvstore.tests.MemcachedConnectorTest;
import eu.mosaic_cloud.connectors.queue.amqp.AmqpConnector;
import eu.mosaic_cloud.drivers.interop.queue.amqp.AmqpStub;
import eu.mosaic_cloud.drivers.queue.amqp.AmqpExchangeType;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.platform.core.ops.IResult;
import eu.mosaic_cloud.platform.core.tests.Serial;
import eu.mosaic_cloud.platform.core.tests.SerialJunitRunner;
import eu.mosaic_cloud.platform.core.tests.TestLoggingHandler;
import eu.mosaic_cloud.platform.interop.amqp.AmqpSession;
import eu.mosaic_cloud.tools.exceptions.tools.AbortingExceptionTracer;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.tools.Threading;

@RunWith(SerialJunitRunner.class)
@Serial
public class AmqpConnectorTest {

	private static IConfiguration configuration;
	private static AmqpConnector connector;
	private static AmqpStub driverStub;

	@BeforeClass
	public static void setUpBeforeClass() throws Throwable {
		BasicThreadingSecurityManager.initialize ();
		ThreadingContext threading = BasicThreadingContext.create(
				MemcachedConnectorTest.class,
				AbortingExceptionTracer.defaultInstance.catcher);
		AmqpConnectorTest.configuration = PropertyTypeConfiguration.create(
				AmqpConnectorTest.class.getClassLoader(), "amqp-test.prop");

		ZeroMqChannel driverChannel = new ZeroMqChannel(
				ConfigUtils.resolveParameter(AmqpConnectorTest.configuration,
						"interop.driver.identifier", String.class, ""),
				threading, AbortingExceptionTracer.defaultInstance);
		driverChannel.register(AmqpSession.DRIVER);
		driverChannel.accept(ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "interop.channel.address",
				String.class, ""));

		AmqpConnectorTest.driverStub = AmqpStub.create(
				AmqpConnectorTest.configuration, driverChannel, threading);
		AmqpConnectorTest.connector = AmqpConnector.create(
				AmqpConnectorTest.configuration,
				Threading.sequezeThreadingContextOutOfDryRock());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Throwable {
		AmqpConnectorTest.connector.destroy();
		AmqpConnectorTest.driverStub.destroy();
	}

	public void testConnector() throws InterruptedException, ExecutionException {
		Assert.assertNotNull(AmqpConnectorTest.connector);
	}

	private List<IOperationCompletionHandler<Boolean>> getHandlers(
			String testName) {
		IOperationCompletionHandler<Boolean> handler = new TestLoggingHandler<Boolean>(
				testName);
		List<IOperationCompletionHandler<Boolean>> list = new ArrayList<IOperationCompletionHandler<Boolean>>();
		list.add(handler);
		return list;
	}

	public void testDeclareExchange() throws InterruptedException,
			ExecutionException {
		String exchange = ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "publisher.amqp.exchange",
				String.class, "");
		List<IOperationCompletionHandler<Boolean>> handlers = getHandlers("declare exchange");

		IResult<Boolean> r = AmqpConnectorTest.connector.declareExchange(
				exchange, AmqpExchangeType.DIRECT, false, false, false,
				handlers, null);
		Assert.assertTrue(r.getResult());
	}

	public void testDeclareQueue() throws InterruptedException,
			ExecutionException {
		String queue = ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "consumer.amqp.queue",
				String.class, "");
		List<IOperationCompletionHandler<Boolean>> handlers = getHandlers("declare queue");
		IResult<Boolean> r = AmqpConnectorTest.connector.declareQueue(queue,
				true, false, true, false, handlers, null);
		Assert.assertTrue(r.getResult());
	}

	public void testBindQueue() throws InterruptedException, ExecutionException {
		String exchange = ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "publisher.amqp.exchange",
				String.class, "");
		String routingKey = ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "publisher.amqp.routing_key",
				String.class, "");
		String queue = ConfigUtils.resolveParameter(
				AmqpConnectorTest.configuration, "consumer.amqp.queue",
				String.class, "");
		List<IOperationCompletionHandler<Boolean>> handlers = getHandlers("bind queue");
		IResult<Boolean> r = AmqpConnectorTest.connector.bindQueue(exchange,
				queue, routingKey, handlers, null);

		Assert.assertTrue(r.getResult());
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		testConnector();
		testDeclareExchange();
		testDeclareQueue();
		testBindQueue();
	}

}