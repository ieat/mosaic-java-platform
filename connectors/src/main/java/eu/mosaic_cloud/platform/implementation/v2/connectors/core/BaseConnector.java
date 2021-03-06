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

package eu.mosaic_cloud.platform.implementation.v2.connectors.core;


import eu.mosaic_cloud.platform.v2.connectors.core.Connector;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.callbacks.core.CallbackProxy;
import eu.mosaic_cloud.tools.transcript.core.Transcript;

import com.google.common.base.Preconditions;


/**
 * Base class for all connectors. Any connector will have an associated proxy. This proxy will handle all communication between
 * the connector and the proxy.
 * 
 * @author Georgiana Macariu
 * @param <TProxy>
 *            the type of the associated proxy
 */
public abstract class BaseConnector<TProxy extends BaseConnectorProxy>
			implements
				Connector,
				CallbackProxy
{
	protected BaseConnector (final TProxy proxy) {
		super ();
		Preconditions.checkNotNull (proxy);
		this.proxy = proxy;
		this.transcript = Transcript.create (this, true);
		this.transcript.traceDebugging ("creating the connector of type `%{object:class}`.", this);
		this.transcript.traceDebugging ("using the underlying connector proxy `%{object}`...", this.proxy);
	}
	
	@Override
	public CallbackCompletion<Void> destroy () {
		return this.proxy.destroy ();
	}
	
	@Override
	public CallbackCompletion<Void> initialize () {
		return this.proxy.initialize ();
	}
	
	protected final TProxy proxy;
	protected final Transcript transcript;
}
