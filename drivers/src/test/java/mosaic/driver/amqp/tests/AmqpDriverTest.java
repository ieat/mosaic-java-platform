package mosaic.driver.amqp.tests;

import java.util.concurrent.ExecutionException;

import mosaic.core.Serial;
import mosaic.core.SerialJunitRunner;
import mosaic.core.TestLoggingHandler;
import mosaic.core.configuration.ConfigUtils;
import mosaic.core.configuration.IConfiguration;
import mosaic.core.configuration.PropertyTypeConfiguration;
import mosaic.core.ops.IOperationCompletionHandler;
import mosaic.core.ops.IResult;
import mosaic.driver.queue.amqp.AmqpDriver;
import mosaic.driver.queue.amqp.AmqpExchangeType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerialJunitRunner.class)
@Serial
public class AmqpDriverTest {

	private static IConfiguration configuration;
	private static AmqpDriver wrapper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AmqpDriverTest.configuration = PropertyTypeConfiguration.create(
				AmqpDriverTest.class.getClassLoader(), "amqp-test.prop");
		AmqpDriverTest.wrapper = AmqpDriver
				.create(AmqpDriverTest.configuration);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		AmqpDriverTest.wrapper.destroy();
	}

	public void testDriver() throws InterruptedException, ExecutionException {
		Assert.assertNotNull(AmqpDriverTest.wrapper);
	}

	public void testDeclareExchange() throws InterruptedException,
			ExecutionException {
		String exchange = ConfigUtils.resolveParameter(
				AmqpDriverTest.configuration, "publisher.amqp.exchange",
				String.class, "");

		IOperationCompletionHandler<Boolean> handler = new TestLoggingHandler<Boolean>(
				"declare exchange");
		IResult<Boolean> r = AmqpDriverTest.wrapper.declareExchange(exchange,
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
		IResult<Boolean> r = AmqpDriverTest.wrapper.declareQueue(queue, true,
				false, true, false, handler);
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
		IResult<Boolean> r = AmqpDriverTest.wrapper.bindQueue(exchange, queue,
				routingKey, handler);
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
