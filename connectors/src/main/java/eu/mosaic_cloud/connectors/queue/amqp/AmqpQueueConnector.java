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

package eu.mosaic_cloud.connectors.queue.amqp;

import eu.mosaic_cloud.connectors.core.BaseConnector;
import eu.mosaic_cloud.connectors.tools.ConfigProperties;
import eu.mosaic_cloud.drivers.queue.amqp.AmqpExchangeType;
import eu.mosaic_cloud.drivers.queue.amqp.AmqpOutboundMessage;
import eu.mosaic_cloud.interoperability.core.Channel;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.interop.amqp.AmqpSession;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;

/**
 * Connector for queuing systems implementing the AMQP protocol.
 * 
 * @author Georgiana Macariu
 * 
 */
public class AmqpQueueConnector extends BaseConnector<AmqpQueueConnectorProxy> implements
        IAmqpQueueConnector {
    protected AmqpQueueConnector(final AmqpQueueConnectorProxy proxy) {
        super(proxy);
    }

    @Override
    public CallbackCompletion<Boolean> ack(final long delivery, final boolean multiple) {
        return this.proxy.ack(delivery, multiple);
    }

    @Override
    public CallbackCompletion<Boolean> bindQueue(final String exchange, final String queue,
            final String routingKey) {
        return this.proxy.bindQueue(exchange, queue, routingKey);
    }

    @Override
    public CallbackCompletion<Boolean> cancel(final String consumer) {
        return this.proxy.cancel(consumer);
    }

    @Override
    public CallbackCompletion<Boolean> consume(final String queue, final String consumer,
            final boolean exclusive, final boolean autoAck, final Object extra,
            final IAmqpQueueConsumerCallbacks consumerCallback) {
        return this.proxy.consume(queue, consumer, exclusive, autoAck, extra, consumerCallback);
    }

    @Override
    public CallbackCompletion<Boolean> declareExchange(final String name,
            final AmqpExchangeType type, final boolean durable, final boolean autoDelete,
            final boolean passive) {
        return this.proxy.declareExchange(name, type, durable, autoDelete, passive);
    }

    @Override
    public CallbackCompletion<Boolean> declareQueue(final String queue, final boolean exclusive,
            final boolean durable, final boolean autoDelete, final boolean passive) {
        return this.proxy.declareQueue(queue, exclusive, durable, autoDelete, passive);
    }

    @Override
    public CallbackCompletion<Void> destroy() {
        this.logger.trace("AmqpConnector was destroyed.");
        return this.proxy.destroy();
    }

    @Override
    public CallbackCompletion<Boolean> get(final String queue, final boolean autoAck) {
        return this.proxy.get(queue, autoAck);
    }

    @Override
    public CallbackCompletion<Boolean> publish(final AmqpOutboundMessage message) {
        return this.proxy.publish(message);
    }

    /**
     * Returns an AMQP connector. For AMQP it should always return a new
     * connector.
     * 
     * @param configuration
     *            the configuration parameters required by the connector. This
     *            should also include configuration settings for the
     *            corresponding driver.
     * @return the connector
     * @throws Throwable
     */
    public static AmqpQueueConnector create(final IConfiguration configuration,
            final ThreadingContext threading) {
        final String driverIdentity = ConfigUtils.resolveParameter(configuration,
                ConfigProperties.getString("AllConnector.1"), String.class, "");
        final String driverEndpoint = ConfigUtils.resolveParameter(configuration,
                ConfigProperties.getString("AllConnector.0"), String.class, "");
        final Channel channel = BaseConnector.createChannel(driverEndpoint, threading);
        channel.register(AmqpSession.CONNECTOR);
        final AmqpQueueConnectorProxy proxy = AmqpQueueConnectorProxy.create(configuration,
                driverIdentity, channel);
        return new AmqpQueueConnector(proxy);
    }
}
