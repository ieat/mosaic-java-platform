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

package eu.mosaic_cloud.cloudlets.implementations.v1.connectors.components;


import eu.mosaic_cloud.cloudlets.implementations.v1.connectors.core.BaseConnectorFactory;
import eu.mosaic_cloud.cloudlets.v1.cloudlets.CloudletController;
import eu.mosaic_cloud.cloudlets.v1.connectors.components.ComponentConnectorCallbacks;
import eu.mosaic_cloud.connectors.implementations.v1.core.ConnectorEnvironment;
import eu.mosaic_cloud.connectors.v1.core.ConnectorsFactory;
import eu.mosaic_cloud.platform.implementations.v1.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.v1.core.configuration.Configuration;

import com.google.common.base.Preconditions;


public final class ComponentConnectorFactory
			extends BaseConnectorFactory<eu.mosaic_cloud.cloudlets.v1.connectors.components.ComponentConnector<?>>
			implements
				eu.mosaic_cloud.cloudlets.v1.connectors.components.ComponentConnectorFactory
{
	public ComponentConnectorFactory (final CloudletController<?> cloudlet, final eu.mosaic_cloud.connectors.v1.components.ComponentConnector backingConnector, final ConnectorEnvironment environment, final ConnectorsFactory delegate) {
		super (cloudlet, environment, delegate);
		Preconditions.checkNotNull (backingConnector);
		this.backingConnector = backingConnector;
	}
	
	@Override
	public final <TContext, TExtra> ComponentConnector<TContext, TExtra> create (final ComponentConnectorCallbacks<TContext, TExtra> callbacks, final TContext callbacksContext) {
		final Configuration configuration = PropertyTypeConfiguration.createEmpty ();
		final ComponentConnector<TContext, TExtra> connector = new ComponentConnector<TContext, TExtra> (this.cloudlet, this.backingConnector, configuration, callbacks, callbacksContext);
		return connector;
	}
	
	private final eu.mosaic_cloud.connectors.v1.components.ComponentConnector backingConnector;
}
