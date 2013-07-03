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

package eu.mosaic_cloud.connectors.v1.httpg;


import eu.mosaic_cloud.connectors.v1.queue.IQueueConnectorFactory;
import eu.mosaic_cloud.platform.v1.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.v1.core.serialization.DataEncoder;


public interface IHttpgQueueConnectorFactory
			extends
				IQueueConnectorFactory<IHttpgQueueConnector<?, ?>>
{
	<TRequestBody, TResponseBody> IHttpgQueueConnector<TRequestBody, TResponseBody> create (IConfiguration configuration, Class<TRequestBody> requestBodyClass, DataEncoder<TRequestBody> requestBodyEncoder, Class<TResponseBody> responseBodyClass, DataEncoder<TResponseBody> responseBodyEncoder, IHttpgQueueCallback<TRequestBody, TResponseBody> callback);
}
