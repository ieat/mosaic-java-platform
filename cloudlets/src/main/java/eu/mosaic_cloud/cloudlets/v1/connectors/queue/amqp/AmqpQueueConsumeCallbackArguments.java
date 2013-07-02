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


import eu.mosaic_cloud.cloudlets.v1.cloudlets.ICloudletController;
import eu.mosaic_cloud.cloudlets.v1.core.CallbackArguments;
import eu.mosaic_cloud.connectors.v1.queue.amqp.IAmqpMessageToken;


/**
 * The arguments of the cloudlet callback methods for the consume request.
 * 
 * @author Georgiana Macariu
 * 
 * @param <TMessage>
 *            the type of the consumed data
 * @param <TExtra>
 *            the type of the extra data; as an example, this data can be used
 *            correlation
 */
public class AmqpQueueConsumeCallbackArguments<TMessage>
		extends CallbackArguments
{
	/**
	 * Creates a new callback argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param message
	 *            information about the consume request
	 */
	public AmqpQueueConsumeCallbackArguments (final ICloudletController<?> cloudlet, final IAmqpMessageToken token, final TMessage message)
	{
		super (cloudlet);
		this.message = message;
		this.token = token;
	}
	
	/**
	 * Returns information about the consume request.
	 * 
	 * @return information about the consume request
	 */
	public TMessage getMessage ()
	{
		return this.message;
	}
	
	public IAmqpMessageToken getToken ()
	{
		return this.token;
	}
	
	private final TMessage message;
	private final IAmqpMessageToken token;
}