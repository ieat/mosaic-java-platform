/*
 * #%L
 * mosaic-examples
 * %%
 * Copyright (C) 2010 - 2011 mOSAIC Project
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
package mosaic.cloudlet.tests;

import mosaic.cloudlet.core.CallbackArguments;
import mosaic.cloudlet.core.DefaultCloudletCallback;
import mosaic.cloudlet.core.ICloudletController;
import mosaic.cloudlet.resources.amqp.AmqpQueuePublishCallbackArguments;
import mosaic.cloudlet.resources.amqp.AmqpQueuePublisher;
import mosaic.cloudlet.resources.amqp.DefaultAmqpPublisherCallback;
import mosaic.core.configuration.ConfigurationIdentifier;
import mosaic.core.configuration.IConfiguration;
import mosaic.core.log.MosaicLogger;
import mosaic.core.utils.PojoDataEncoder;

public class PublisherCloudlet {

	public static final class LifeCycleHandler extends
			DefaultCloudletCallback<PublisherCloudletState> {

		@Override
		public void initialize(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet is being initialized.");
			ICloudletController<PublisherCloudletState> cloudlet = arguments
					.getCloudlet();
			IConfiguration configuration = cloudlet.getConfiguration();
			IConfiguration queueConfiguration = configuration
					.spliceConfiguration(ConfigurationIdentifier
							.resolveAbsolute("queue"));
			state.publisher = new AmqpQueuePublisher<PublisherCloudlet.PublisherCloudletState, String>(
					queueConfiguration, cloudlet, String.class,
					new PojoDataEncoder<String>(String.class));

		}

		@Override
		public void initializeSucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet initialized successfully.");
			ICloudletController<PublisherCloudletState> cloudlet = arguments
					.getCloudlet();
			cloudlet.initializeResource(state.publisher,
					new AmqpPublisherCallback(), state);

		}

		@Override
		public void destroy(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet is being destroyed.");
		}

		@Override
		public void destroySucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"Publisher cloudlet was destroyed successfully.");
		}
	}

	public static final class AmqpPublisherCallback
			extends
			DefaultAmqpPublisherCallback<PublisherCloudletState, AuthenticationToken> {

		@Override
		public void registerSucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet publisher registered successfully.");
			state.publisher.publish("TEST MESSAGE!!!!", null, "text/plain");
		}

		@Override
		public void unregisterSucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet publisher unregistered successfully.");
			// if unregistered as publisher is successful then destroy resource
			ICloudletController<PublisherCloudletState> cloudlet = arguments
					.getCloudlet();
			cloudlet.destroyResource(state.publisher, this);
		}

		@Override
		public void initializeSucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			// if resource initialized successfully then just register as a
			// publisher
			state.publisher.register();
		}

		@Override
		public void destroySucceeded(PublisherCloudletState state,
				CallbackArguments<PublisherCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"PublisherCloudlet publisher was destroyed successfully.");
			state.publisher = null;
			arguments.getCloudlet().destroy();
		}

		@Override
		public void publishSucceeded(
				PublisherCloudletState state,
				AmqpQueuePublishCallbackArguments<PublisherCloudletState, AuthenticationToken> arguments) {
			state.publisher.unregister();
		}

	}

	public static final class PublisherCloudletState {
		AmqpQueuePublisher<PublisherCloudletState, String> publisher;
	}
}