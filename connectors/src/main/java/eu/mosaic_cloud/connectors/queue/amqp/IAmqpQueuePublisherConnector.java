/*
 * #%L
 * mosaic-cloudlets
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

import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

/**
 * Interface for registering and using for an AMQP resource as a publisher.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            the type of the context of the cloudlet using this accessor
 * @param <Message>
 *            the type of the published data
 */
public interface IAmqpQueuePublisherConnector<Message> extends IAmqpQueueConnector {

	/**
	 * Publishes a message to a queue.
	 * 
	 * @param data
	 *            the data to publish
	 */
	CallbackCompletion<Void> publish(Message data);
}