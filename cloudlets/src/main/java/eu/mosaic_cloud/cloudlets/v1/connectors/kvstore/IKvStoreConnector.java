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


import java.util.List;

import eu.mosaic_cloud.cloudlets.v1.connectors.core.IConnector;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


/**
 * Basic interface for cloudlets to access key-value storages.
 * 
 * @author Georgiana Macariu
 * 
 * @param <TValue>
 *            the type of the values exchanged with the key-value store using
 *            this connector
 * @param <TExtra>
 *            the type of the extra data; as an example, this data can be used
 *            correlation
 */
public interface IKvStoreConnector<TValue, TExtra>
		extends
			IConnector,
			eu.mosaic_cloud.connectors.v1.kvstore.IKvStoreConnector<TValue>
{
	/**
	 * Deletes the given key.
	 * 
	 * @param key
	 *            the key to delete
	 * @param extra
	 *            some application specific data
	 * @return a result handle for the operation
	 */
	CallbackCompletion<Void> delete (String key, TExtra extra);
	
	/**
	 * Gets data associated with a single key.
	 * 
	 * @param key
	 *            the key
	 * @param extra
	 *            some application specific data
	 * @return a result handle for the operation
	 */
	CallbackCompletion<TValue> get (String key, TExtra extra);
	
	/**
	 * Lists the keys in the bucket associated with the connector.
	 * 
	 * @param extra
	 *            some application specific data
	 * @return a result handle for the operation
	 */
	CallbackCompletion<List<String>> list (TExtra extra);
	
	/**
	 * Stores the given data and associates it with the specified key.
	 * 
	 * @param key
	 *            the key under which this data should be stored
	 * @param data
	 *            the data
	 * @param extra
	 *            some application specific data
	 * @return a result handle for the operation
	 */
	CallbackCompletion<Void> set (String key, TValue value, TExtra extra);
}