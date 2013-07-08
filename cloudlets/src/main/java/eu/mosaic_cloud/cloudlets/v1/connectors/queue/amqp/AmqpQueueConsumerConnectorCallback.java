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


import eu.mosaic_cloud.cloudlets.v1.core.GenericCallbackCompletionArguments;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


/**
 * Interface for AMQP queue consumers. This will be implemented by cloudlets which need to receive messages from a queue.
 * 
 * @author Georgiana Macariu
 * @param <TContext>
 *            the type of the cloudlet context
 * @param <TMessage>
 *            the type of consumed data
 * @param <TExtra>
 *            the type of the extra data; as an example, this data can be used correlation
 */
public interface AmqpQueueConsumerConnectorCallback<TContext, TMessage, TExtra>
			extends
				AmqpQueueConnectorCallback<TContext>
{
	/**
	 * Handles unsuccessful message acknowledge events.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            the arguments of the callback
	 */
	CallbackCompletion<Void> acknowledgeFailed (TContext context, GenericCallbackCompletionArguments<TExtra> arguments);
	
	/**
	 * Handles successful message acknowledge events.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            the arguments of the callback
	 */
	CallbackCompletion<Void> acknowledgeSucceeded (TContext context, GenericCallbackCompletionArguments<TExtra> arguments);
	
	/**
	 * Called when this consumer receives a message. This will deliver the message
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            the arguments of the callback
	 */
	CallbackCompletion<Void> consume (TContext context, AmqpQueueConsumeCallbackArguments<TMessage> arguments);
}