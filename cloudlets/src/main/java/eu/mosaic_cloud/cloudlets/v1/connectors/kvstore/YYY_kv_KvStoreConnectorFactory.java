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

package eu.mosaic_cloud.cloudlets.v1.connectors.kvstore;


import eu.mosaic_cloud.cloudlets.v1.connectors.core.ConnectorFactory;
import eu.mosaic_cloud.platform.v1.core.configuration.Configuration;
import eu.mosaic_cloud.platform.v1.core.serialization.DataEncoder;


/**
 * Factory for creating key-value store connectors.
 * 
 * @author Ciprian Craciun
 */
public interface YYY_kv_KvStoreConnectorFactory
			extends
				ConnectorFactory<YYY_kv_KvStoreConnector<?, ?>>
{
	/**
	 * Creates a key-value store connector.
	 * 
	 * @param configuration
	 * @param valueClass
	 * @param valueEncoder
	 * @param callback
	 * @param callbackContext
	 * @return
	 */
	<TContext, TValue, TExtra> YYY_kv_KvStoreConnector<TValue, TExtra> create (Configuration configuration, Class<TValue> valueClass, DataEncoder<TValue> valueEncoder, KvStoreConnectorCallback<TContext, TValue, TExtra> callback, TContext callbackContext);
}