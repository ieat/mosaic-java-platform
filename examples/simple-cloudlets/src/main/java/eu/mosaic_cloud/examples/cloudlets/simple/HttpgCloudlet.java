/*
 * #%L
 * mosaic-examples-simple-cloudlets
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

package eu.mosaic_cloud.examples.cloudlets.simple;


import java.util.UUID;

import eu.mosaic_cloud.cloudlets.tools.v1.callbacks.DefaultCloudletCallback;
import eu.mosaic_cloud.cloudlets.tools.v1.callbacks.DefaultHttpgQueueConnectorCallback;
import eu.mosaic_cloud.cloudlets.v1.cloudlets.CloudletCallbackArguments;
import eu.mosaic_cloud.cloudlets.v1.cloudlets.CloudletController;
import eu.mosaic_cloud.cloudlets.v1.connectors.httpg.HttpgQueueRequestedCallbackArguments;
import eu.mosaic_cloud.cloudlets.v1.connectors.httpg.YYY_httpg_HttpgQueueConnectorFactory;
import eu.mosaic_cloud.cloudlets.v1.core.YYY_core_Callback;
import eu.mosaic_cloud.connectors.v1.httpg.HttpgRequestMessage;
import eu.mosaic_cloud.connectors.v1.httpg.HttpgResponseMessage;
import eu.mosaic_cloud.connectors.v1.httpg.ZZZ_httpg_HttpgQueueConnector;
import eu.mosaic_cloud.platform.implementations.v1.serialization.PlainTextDataEncoder;
import eu.mosaic_cloud.platform.v1.core.configuration.Configuration;
import eu.mosaic_cloud.platform.v1.core.configuration.ConfigurationIdentifier;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


public class HttpgCloudlet
{
	public static final class Callbacks
				extends DefaultCloudletCallback<Context>
	{
		@Override
		public CallbackCompletion<Void> destroy (final Context context, final CloudletCallbackArguments<Context> arguments) {
			this.logger.info ("destroying cloudlet...");
			return (context.gateway.destroy ());
		}
		
		@Override
		public CallbackCompletion<Void> initialize (final Context context, final CloudletCallbackArguments<Context> arguments) {
			this.logger.info ("initializing cloudlet...");
			context.identity = UUID.randomUUID ().toString ();
			context.cloudlet = arguments.getCloudlet ();
			final Configuration cloudletConfiguration = context.cloudlet.getConfiguration ();
			final Configuration gatewayConfiguration = cloudletConfiguration.spliceConfiguration (ConfigurationIdentifier.resolveAbsolute ("gateway"));
			context.gateway = context.cloudlet.getConnectorFactory (YYY_httpg_HttpgQueueConnectorFactory.class).create (gatewayConfiguration, String.class, PlainTextDataEncoder.DEFAULT_INSTANCE, String.class, PlainTextDataEncoder.DEFAULT_INSTANCE, new GatewayCallbacks (), context);
			return (context.gateway.initialize ());
		}
	}
	
	public static final class Context
	{
		CloudletController<Context> cloudlet;
		ZZZ_httpg_HttpgQueueConnector<String, String> gateway;
		String identity;
	}
	
	public static final class GatewayCallbacks
				extends DefaultHttpgQueueConnectorCallback<Context, String, String, Void>
	{
		@Override
		public CallbackCompletion<Void> requested (final Context context, final HttpgQueueRequestedCallbackArguments<String> arguments) {
			final HttpgRequestMessage<String> request = arguments.getRequest ();
			final StringBuilder responseBody = new StringBuilder ();
			responseBody.append (String.format ("Cloudlet: %s\n", context.identity));
			responseBody.append (String.format ("HTTP version: %s\n", request.version));
			responseBody.append (String.format ("HTTP method: %s\n", request.method));
			responseBody.append (String.format ("HTTP path: %s\n", request.path));
			if (request.body != null) {
				responseBody.append ("HTTP body:\n");
				responseBody.append (request.body);
			} else
				responseBody.append ("HTTP body: empty\n");
			final HttpgResponseMessage<String> response = HttpgResponseMessage.create200 (request, responseBody.toString ());
			context.gateway.respond (response);
			return (YYY_core_Callback.SUCCESS);
		}
	}
}
