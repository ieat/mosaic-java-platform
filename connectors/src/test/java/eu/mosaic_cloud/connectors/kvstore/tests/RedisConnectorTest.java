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
package eu.mosaic_cloud.connectors.kvstore.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.connectors.kvstore.KeyValueStoreConnector;
import eu.mosaic_cloud.drivers.interop.kvstore.KeyValueStub;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.platform.core.ops.IResult;
import eu.mosaic_cloud.platform.core.tests.TestLoggingHandler;
import eu.mosaic_cloud.platform.core.utils.PojoDataEncoder;
import eu.mosaic_cloud.platform.interop.kvstore.KeyValueSession;
import eu.mosaic_cloud.tools.exceptions.tools.AbortingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class RedisConnectorTest {

	private KeyValueStoreConnector<String> connector;
	private static BasicThreadingContext threading;
	private static String keyPrefix;
	private static KeyValueStub driverStub;

	@BeforeClass
	public static void setUpBeforeClass() throws Throwable {
		BasicThreadingSecurityManager.initialize();
		RedisConnectorTest.threading = BasicThreadingContext.create(
				RedisConnectorTest.class,
				AbortingExceptionTracer.defaultInstance.catcher);
		RedisConnectorTest.threading.initialize();
		IConfiguration config = PropertyTypeConfiguration.create(
				RedisConnectorTest.class.getClassLoader(), "redis-test.prop");

		ZeroMqChannel driverChannel = ZeroMqChannel.create(
				ConfigUtils.resolveParameter(config,
						"interop.driver.identifier", String.class, ""),
				RedisConnectorTest.threading,
				AbortingExceptionTracer.defaultInstance);
		driverChannel.register(KeyValueSession.DRIVER);
		driverChannel.accept(ConfigUtils.resolveParameter(config,
				"interop.channel.address", String.class, ""));

		RedisConnectorTest.driverStub = KeyValueStub.create(config,
				RedisConnectorTest.threading, driverChannel);
		RedisConnectorTest.keyPrefix = UUID.randomUUID().toString();
	}

	@Before
	public void setUp() throws Throwable {
		IConfiguration config = PropertyTypeConfiguration.create(
				RedisConnectorTest.class.getClassLoader(), "redis-test.prop");
		this.connector = KeyValueStoreConnector.create(config,
				new PojoDataEncoder<String>(String.class),
				RedisConnectorTest.threading);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Throwable {
		RedisConnectorTest.driverStub.destroy();
		RedisConnectorTest.threading.destroy();
	}

	@After
	public void tearDown() throws Throwable {
		this.connector.destroy();
	}

	public void testConnection() {
		Assert.assertNotNull(this.connector);
	}

	private static <T> List<IOperationCompletionHandler<T>> getHandlers(
			String testName) {
		IOperationCompletionHandler<T> handler = new TestLoggingHandler<T>(
				testName);
		List<IOperationCompletionHandler<T>> list = new ArrayList<IOperationCompletionHandler<T>>();
		list.add(handler);
		return list;
	}

	public void testSet() throws IOException {
		String k1 = RedisConnectorTest.keyPrefix + "_key_fantastic";
		List<IOperationCompletionHandler<Boolean>> handlers1 = getHandlers("set 1");
		IResult<Boolean> r1 = this.connector.set(k1, "fantastic", handlers1,
				null);
		Assert.assertNotNull(r1);

		String k2 = RedisConnectorTest.keyPrefix + "_key_famous";
		List<IOperationCompletionHandler<Boolean>> handlers2 = getHandlers("set 2");
		IResult<Boolean> r2 = this.connector.set(k2, "famous", handlers2, null);
		Assert.assertNotNull(r2);

		try {
			Assert.assertTrue(r1.getResult());
			Assert.assertTrue(r2.getResult());
		} catch (InterruptedException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		} catch (ExecutionException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public void testGet() throws IOException, ClassNotFoundException {
		String k1 = RedisConnectorTest.keyPrefix + "_key_fantastic";
		List<IOperationCompletionHandler<String>> handlers = getHandlers("get");
		IResult<String> r1 = this.connector.get(k1, handlers, null);

		try {
			Assert.assertEquals("fantastic", r1.getResult().toString());
		} catch (InterruptedException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		} catch (ExecutionException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public void testDelete() {
		String k1 = RedisConnectorTest.keyPrefix + "_key_fantastic";
		List<IOperationCompletionHandler<Boolean>> handlers = getHandlers("delete");
		IResult<Boolean> r1 = this.connector.delete(k1, handlers, null);
		try {
			Assert.assertTrue(r1.getResult());
		} catch (InterruptedException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		} catch (ExecutionException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}

		List<IOperationCompletionHandler<String>> handlers1 = getHandlers("get after delete");
		IResult<String> r2 = this.connector.get(k1, handlers1, null);

		try {
			Assert.assertNull(r2.getResult());
		} catch (InterruptedException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		} catch (ExecutionException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public void testList() {
		List<IOperationCompletionHandler<List<String>>> handlers = new ArrayList<IOperationCompletionHandler<List<String>>>();
		handlers.add(new TestLoggingHandler<List<String>>("list"));
		IResult<List<String>> r1 = this.connector.list(handlers, null);
		try {
			Assert.assertNotNull(r1.getResult());
			String k2 = RedisConnectorTest.keyPrefix + "_key_famous";
			Assert.assertTrue(r1.getResult().contains(k2));
		} catch (InterruptedException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		} catch (ExecutionException e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	@Test
	public void testConnector() throws IOException, ClassNotFoundException {
		testConnection();
		testSet();
		testGet();
		testList();
		testDelete();
	}

	public static void main(String... args) {
		JUnitCore
				.main("eu.mosaic_cloud.connectors.kvstore.tests.RedisConnectorTest");
	}

	public static void _main(String... args) throws Throwable {
		BasicThreadingSecurityManager.initialize();
		BasicThreadingContext threading = BasicThreadingContext.create(
				RedisConnectorTest.class,
				AbortingExceptionTracer.defaultInstance.catcher);
		threading.initialize();
		IConfiguration config = PropertyTypeConfiguration.create(
				RedisConnectorTest.class.getClassLoader(), "redis-test.prop");
		KeyValueStoreConnector<String> connector = KeyValueStoreConnector
				.create(config, new PojoDataEncoder<String>(String.class),
						threading);
		String keyPrefix = UUID.randomUUID().toString();

		ZeroMqChannel driverChannel = ZeroMqChannel.create(
				ConfigUtils.resolveParameter(config,
						"interop.driver.identifier", String.class, ""),
				threading, AbortingExceptionTracer.defaultInstance);
		driverChannel.register(KeyValueSession.DRIVER);
		driverChannel.accept(ConfigUtils.resolveParameter(config,
				"interop.channel.address", String.class, ""));
		KeyValueStub driverStub = KeyValueStub.create(config, threading,
				driverChannel);

		String k1 = keyPrefix + "_key_fantastic";
		List<IOperationCompletionHandler<Boolean>> handlers1 = getHandlers("add 1");
		IResult<Boolean> r1 = connector.set(k1, "fantastic", handlers1, null);
		boolean result = r1.getResult();
		Preconditions.checkArgument(result);

		List<IOperationCompletionHandler<String>> handlers = getHandlers("get");
		IResult<String> r3 = connector.get(k1, handlers, null);
		Preconditions.checkArgument("fantastic".equals(r3.getResult()));

		List<IOperationCompletionHandler<List<String>>> handlersl = new ArrayList<IOperationCompletionHandler<List<String>>>();
		handlersl.add(new TestLoggingHandler<List<String>>("list"));
		IResult<List<String>> r4 = connector.list(handlersl, null);
		List<String> list = r4.getResult();
		Preconditions.checkArgument(list != null);

		List<IOperationCompletionHandler<Boolean>> handlersd = getHandlers("delete");
		IResult<Boolean> r5 = connector.delete(k1, handlersd, null);
		Preconditions.checkArgument(r5.getResult());

		IResult<String> r6 = connector.get(k1, handlers, null);
		Preconditions.checkArgument(r6.getResult() == null);

		connector.destroy();
		driverStub.destroy();
		threading.destroy();
	}
}
