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

package eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.httpg;


import eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.core.BaseConnectorsFactoryInitializer;
import eu.mosaic_cloud.platform.implementation.v2.connectors.core.ConnectorEnvironment;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.httpg.HttpgQueueConnectorCallback;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.httpg.HttpgQueueConnectorFactory;
import eu.mosaic_cloud.platform.v2.cloudlets.core.CloudletController;
import eu.mosaic_cloud.platform.v2.configuration.Configuration;
import eu.mosaic_cloud.platform.v2.connectors.core.ConnectorsFactory;
import eu.mosaic_cloud.platform.v2.connectors.core.ConnectorsFactoryBuilder;
import eu.mosaic_cloud.platform.v2.serialization.DataEncoder;

import com.google.common.base.Preconditions;


public final class HttpgQueueConnectorFactoryInitializer
			extends BaseConnectorsFactoryInitializer
{
	@Override
	protected void initialize_1 (final ConnectorsFactoryBuilder builder, final CloudletController<?> cloudlet, final ConnectorEnvironment environment, final ConnectorsFactory delegate) {
		Preconditions.checkNotNull (delegate);
		builder.register (HttpgQueueConnectorFactory.class, new HttpgQueueConnectorFactory () {
			@Override
			public <TContext, TRequestBody, TResponseBody, TExtra> HttpgQueueConnector<TContext, TRequestBody, TResponseBody, TExtra> create (final Configuration configuration, final Class<TRequestBody> requestBodyClass, final DataEncoder<TRequestBody> requestBodyEncoder, final Class<TResponseBody> responseBodyClass, final DataEncoder<TResponseBody> responseBodyEncoder, final HttpgQueueConnectorCallback<TContext, TRequestBody, TResponseBody, TExtra> callback, final TContext callbackContext) {
				final HttpgQueueConnector.Callback<TRequestBody, TResponseBody> backingCallback = new HttpgQueueConnector.Callback<TRequestBody, TResponseBody> ();
				final eu.mosaic_cloud.platform.v2.connectors.httpg.HttpgQueueConnector<TRequestBody, TResponseBody> backingConnector = delegate.getConnectorFactory (eu.mosaic_cloud.platform.v2.connectors.httpg.HttpgQueueConnectorFactory.class).create (configuration, requestBodyClass, requestBodyEncoder, responseBodyClass, responseBodyEncoder, backingCallback);
				return new HttpgQueueConnector<TContext, TRequestBody, TResponseBody, TExtra> (cloudlet, backingConnector, configuration, callback, callbackContext, backingCallback);
			}
		});
	}
	
	public static final HttpgQueueConnectorFactoryInitializer defaultInstance = new HttpgQueueConnectorFactoryInitializer ();
}
