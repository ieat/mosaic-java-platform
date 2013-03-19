/*
 * #%L
 * mosaic-connectors
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

package eu.mosaic_cloud.connectors.tools;


import eu.mosaic_cloud.connectors.core.IConnector;
import eu.mosaic_cloud.connectors.core.IConnectorFactory;
import eu.mosaic_cloud.connectors.core.IConnectorsFactory;

import com.google.common.base.Preconditions;


public abstract class BaseConnectorFactory<TConnector extends IConnector>
		extends Object
		implements
			IConnectorFactory<TConnector>
{
	protected BaseConnectorFactory (final ConnectorEnvironment environment, final IConnectorsFactory delegate)
	{
		super ();
		Preconditions.checkNotNull (environment);
		this.environment = environment;
		this.delegate = delegate;
	}
	
	protected final IConnectorsFactory delegate;
	protected final ConnectorEnvironment environment;
}
