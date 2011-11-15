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
import mosaic.core.log.MosaicLogger;

public class HelloWorldCloudlet {
	public static final class LifeCycleHandler extends
			DefaultCloudletCallback<HelloCloudletState> {

		@Override
		public void initialize(HelloCloudletState state,
				CallbackArguments<HelloCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"HelloWorld cloudlet is initializing...");
		}

		@Override
		public void initializeSucceeded(HelloCloudletState state,
				CallbackArguments<HelloCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"HelloWorld cloudlet was initialized successfully.");
			System.out.println("Hello world!");
			ICloudletController<HelloCloudletState> cloudlet = arguments
					.getCloudlet();
			cloudlet.destroy();
		}

		@Override
		public void destroy(HelloCloudletState state,
				CallbackArguments<HelloCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"HelloWorld cloudlet is being destroyed.");
		}

		@Override
		public void destroySucceeded(HelloCloudletState state,
				CallbackArguments<HelloCloudletState> arguments) {
			MosaicLogger.getLogger().info(
					"HelloWorld cloudlet was destroyed successfully.");
		}

	}

	public static final class HelloCloudletState {

	}
}