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

import eu.mosaic_cloud.cloudlets.connectors.kvstore.IKvStoreConnector;
import eu.mosaic_cloud.cloudlets.connectors.kvstore.IKvStoreConnectorFactory;
import eu.mosaic_cloud.cloudlets.connectors.kvstore.KvStoreCallbackCompletionArguments;
import eu.mosaic_cloud.cloudlets.core.CallbackArguments;
import eu.mosaic_cloud.cloudlets.core.CloudletCallbackArguments;
import eu.mosaic_cloud.cloudlets.core.CloudletCallbackCompletionArguments;
import eu.mosaic_cloud.cloudlets.core.ICallback;
import eu.mosaic_cloud.cloudlets.core.ICloudletController;
import eu.mosaic_cloud.cloudlets.tools.DefaultCloudletCallback;
import eu.mosaic_cloud.cloudlets.tools.DefaultKvStoreConnectorCallback;
import eu.mosaic_cloud.platform.core.configuration.ConfigurationIdentifier;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.utils.PlainTextDataEncoder;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.threading.tools.Threading;

import org.slf4j.Logger;


public class StoringCloudlet
{
	private static CallbackCompletion<Void> maybeSetValue (final StoringCloudletContext context)
	{
		{
			// FIXME: DON'T DO THIS IN YOUR CODE... This is for throttling...
			Threading.sleep (context.delay);
		}
		if (context.count < context.limit) {
			final String key = UUID.randomUUID ().toString ();
			final String data = String.format ("Test value %d! (%s)", Integer.valueOf (context.count), key);
			context.logger.info ("StoringCloudlet setting value `{}` -> `{}`.", key, data);
			context.store.set (key, data, key);
			context.count += 1;
		} else {
			context.cloudlet.destroy ();
		}
		return ICallback.SUCCESS;
	}
	
	public static final class LifeCycleHandler
			extends DefaultCloudletCallback<StoringCloudletContext>
	{
		@Override
		public CallbackCompletion<Void> destroy (final StoringCloudletContext context, final CloudletCallbackArguments<StoringCloudletContext> arguments)
		{
			context.logger.info ("PublisherCloudlet destroying...");
			return context.store.destroy ();
		}
		
		@Override
		public CallbackCompletion<Void> destroySucceeded (final StoringCloudletContext context, final CloudletCallbackCompletionArguments<StoringCloudletContext> arguments)
		{
			context.logger.info ("PublisherCloudlet destroyed successfully.");
			return ICallback.SUCCESS;
		}
		
		@Override
		public CallbackCompletion<Void> initialize (final StoringCloudletContext context, final CloudletCallbackArguments<StoringCloudletContext> arguments)
		{
			context.cloudlet = arguments.getCloudlet ();
			context.logger = this.logger;
			context.logger.info ("PublisherCloudlet initializing...");
			final IConfiguration configuration = context.cloudlet.getConfiguration ();
			final IConfiguration storeConfiguration = configuration.spliceConfiguration (ConfigurationIdentifier.resolveAbsolute ("store"));
			context.store = context.cloudlet.getConnectorFactory (IKvStoreConnectorFactory.class).create (storeConfiguration, String.class, PlainTextDataEncoder.DEFAULT_INSTANCE, new StoreCallback (), context);
			return context.store.initialize ();
		}
		
		@Override
		public CallbackCompletion<Void> initializeSucceeded (final StoringCloudletContext context, final CloudletCallbackCompletionArguments<StoringCloudletContext> arguments)
		{
			context.logger.info ("PublisherCloudlet initialized successfully.");
			return (StoringCloudlet.maybeSetValue (context));
		}
	}
	
	public static final class StoreCallback
			extends DefaultKvStoreConnectorCallback<StoringCloudletContext, String, String>
	{
		@Override
		public CallbackCompletion<Void> destroySucceeded (final StoringCloudletContext context, final CallbackArguments arguments)
		{
			context.logger.info ("PublisherCloudlet publisher destroyed successfully.");
			return ICallback.SUCCESS;
		}
		
		@Override
		public CallbackCompletion<Void> getSucceeded (final StoringCloudletContext context, final KvStoreCallbackCompletionArguments<String, String> arguments)
		{
			final String key = arguments.getExtra ();
			final String value = arguments.getValue ();
			context.logger.info ("StoringCloudlet got value `{}` -> `{}`.", key, value);
			return StoringCloudlet.maybeSetValue (context);
		}
		
		@Override
		public CallbackCompletion<Void> initializeSucceeded (final StoringCloudletContext context, final CallbackArguments arguments)
		{
			context.logger.info ("PublisherCloudlet publisher initialized successfully.");
			return ICallback.SUCCESS;
		}
		
		@Override
		public CallbackCompletion<Void> setSucceeded (final StoringCloudletContext context, final KvStoreCallbackCompletionArguments<String, String> arguments)
		{
			final String key = arguments.getExtra ();
			context.logger.info ("StoringCloudlet getting value `{}`.", key);
			context.store.get (key, key);
			return ICallback.SUCCESS;
		}
	}
	
	public static final class StoringCloudletContext
	{
		ICloudletController<StoringCloudletContext> cloudlet;
		int count = 0;
		int delay = 100;
		int limit = 1000;
		Logger logger;
		IKvStoreConnector<String, String> store;
	}
}
