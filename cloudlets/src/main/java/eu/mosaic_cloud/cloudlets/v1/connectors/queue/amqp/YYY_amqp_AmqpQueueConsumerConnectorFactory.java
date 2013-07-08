/*
 * #%L
 * mosaic-cloudlets
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

package eu.mosaic_cloud.cloudlets.v1.connectors.queue.amqp;


import eu.mosaic_cloud.platform.v1.core.configuration.Configuration;
import eu.mosaic_cloud.platform.v1.core.serialization.DataEncoder;


/**
 * Factory for creating amqp queue consumer connectors.
 * 
 * @author Ciprian Craciun
 */
public interface YYY_amqp_AmqpQueueConsumerConnectorFactory
			extends
				AmqpQueueConnectorFactory<YYY_amqp_AmqpQueueConsumerConnector<?, ?>>
{
	<TContext, TMessage, TExtra> YYY_amqp_AmqpQueueConsumerConnector<TMessage, TExtra> create (Configuration configuration, Class<TMessage> messageClass, DataEncoder<TMessage> messageEncoder, AmqpQueueConsumerConnectorCallback<TContext, TMessage, TExtra> callback, TContext callbackContext);
}