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

package eu.mosaic_cloud.cloudlets.implementation.v1.connectors.queue.amqp;


import eu.mosaic_cloud.cloudlets.implementation.v1.connectors.core.BaseConnector;
import eu.mosaic_cloud.cloudlets.v1.cloudlets.ICloudletController;
import eu.mosaic_cloud.cloudlets.v1.connectors.queue.amqp.IAmqpQueueConnector;
import eu.mosaic_cloud.cloudlets.v1.connectors.queue.amqp.IAmqpQueueConnectorCallback;
import eu.mosaic_cloud.platform.v1.core.configuration.IConfiguration;


/**
 * Base connector class for AMQP queuing systems.
 * 
 * @author Georgiana Macariu
 * 
 * @param <TConnector>
 *            the type of the base connector used by this cloudlet-level
 *            connector
 * @param <TCallback>
 *            the type of the callback class
 * @param <TContext>
 *            the type of the context of the cloudlet using the connector
 */
public abstract class BaseAmqpQueueConnector<TConnector extends eu.mosaic_cloud.connectors.v1.queue.amqp.IAmqpQueueConnector, TCallback extends IAmqpQueueConnectorCallback<TContext>, TContext>
		extends BaseConnector<TConnector, TCallback, TContext>
		implements
			IAmqpQueueConnector
{
	protected BaseAmqpQueueConnector (final ICloudletController<?> cloudlet, final TConnector connector, final IConfiguration configuration, final TCallback callback, final TContext context)
	{
		super (cloudlet, connector, configuration, callback, context);
	}
}