/*
 * #%L
 * mosaic-connectors
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

package eu.mosaic_cloud.connectors.v1.queue.amqp;


import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


/**
 * Interface for using for an AMQP resource as a consumer.
 * 
 * @author Georgiana Macariu
 * @param <TMessage>
 *            the type of the consumed data
 */
public interface ZZZ_amqp_AmqpQueueConsumerConnector<TMessage>
			extends
				ZZZ_amqp_AmqpQueueConnector
{
	/**
	 * Acknowledges a message.
	 * 
	 * @param message
	 *            the message to acknowledge
	 */
	CallbackCompletion<Void> acknowledge (AmqpMessageToken token);
}