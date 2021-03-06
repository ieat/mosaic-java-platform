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

package eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.queue.generic;


import eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.queue.BaseQueueConsumerConnector;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.queue.QueueConsumerConnectorCallback;
import eu.mosaic_cloud.platform.v2.cloudlets.core.CloudletController;
import eu.mosaic_cloud.platform.v2.configuration.Configuration;


public class GenericQueueConsumerConnector<TContext, TMessage, TExtra>
			extends BaseQueueConsumerConnector<TContext, TMessage, TExtra>
{
	public GenericQueueConsumerConnector (final CloudletController<?> cloudlet, final eu.mosaic_cloud.platform.v2.connectors.queue.QueueConsumerConnector<TMessage> connector, final Configuration configuration, final QueueConsumerConnectorCallback<TContext, TMessage, TExtra> callback, final TContext context, final Callback<TMessage> backingCallback) {
		super (cloudlet, connector, configuration, callback, context, backingCallback);
	}
	
	public static class Callback<TMessage>
				extends BaseQueueConsumerConnector.Callback<TMessage>
	{}
}
