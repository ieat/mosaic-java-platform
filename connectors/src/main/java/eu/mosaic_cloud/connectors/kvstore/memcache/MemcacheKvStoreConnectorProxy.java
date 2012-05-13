/*
 * #%L
 * mosaic-connectors
 * %%
 * Copyright (C) 2010 - 2012 Institute e-Austria Timisoara (Romania)
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

package eu.mosaic_cloud.connectors.kvstore.memcache;


import java.util.List;
import java.util.Map;

import eu.mosaic_cloud.connectors.core.ConfigProperties;
import eu.mosaic_cloud.connectors.kvstore.BaseKvStoreConnectorProxy;
import eu.mosaic_cloud.connectors.kvstore.generic.GenericKvStoreConnector;
import eu.mosaic_cloud.connectors.tools.ConnectorConfiguration;
import eu.mosaic_cloud.interoperability.core.Message;
import eu.mosaic_cloud.platform.core.utils.DataEncoder;
import eu.mosaic_cloud.platform.core.utils.EncodingException;
import eu.mosaic_cloud.platform.interop.idl.IdlCommon.CompletionToken;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.InitRequest;
import eu.mosaic_cloud.platform.interop.idl.kvstore.MemcachedPayloads;
import eu.mosaic_cloud.platform.interop.specs.kvstore.KeyValueMessage;
import eu.mosaic_cloud.platform.interop.specs.kvstore.MemcachedMessage;
import eu.mosaic_cloud.platform.interop.specs.kvstore.MemcachedSession;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

import com.google.protobuf.ByteString;


/**
 * Proxy for the driver for key-value distributed storage systems implementing
 * the memcached protocol. This is used by the {@link GenericKvStoreConnector}
 * to communicate with a memcached driver.
 * 
 * @author Georgiana Macariu
 * @param <TValue>
 *            type of stored data
 * 
 */
public final class MemcacheKvStoreConnectorProxy<TValue extends Object>
		extends BaseKvStoreConnectorProxy<TValue>
		implements
			IMemcacheKvStoreConnector<TValue>
{
	protected MemcacheKvStoreConnectorProxy (final ConnectorConfiguration configuration, final DataEncoder<TValue> encoder)
	{
		super (configuration, encoder);
		this.bucket = super.configuration.getConfigParameter (ConfigProperties.getString ("GenericKvStoreConnector.1"), String.class, "");
		this.transcript.traceDebugging ("created memcache kv store connector proxy for bucket `%s`.", this.bucket);
	}
	
	@Override
	public CallbackCompletion<Boolean> add (final String key, final int exp, final TValue data)
	{
		final CompletionToken token = this.generateToken ();
		this.transcript.traceDebugging ("adding to the record with key `%s` (with request token `%s`)...", key, token.getMessageId ());
		final MemcachedPayloads.AddRequest.Builder requestBuilder = MemcachedPayloads.AddRequest.newBuilder ();
		requestBuilder.setToken (token);
		requestBuilder.setKey (key);
		requestBuilder.setExpTime (exp);
		CallbackCompletion<Boolean> result = null;
		try {
			final byte[] dataBytes = this.encoder.encode (data, this.encoder.getEncodingMetadata ());
			requestBuilder.setValue (ByteString.copyFrom (dataBytes));
		} catch (final EncodingException exception) {
			this.exceptions.traceDeferredException (exception, "encoding the value for record with key `%s` failed; deferring!", key);
			result = CallbackCompletion.createFailure (exception);
		}
		if (result == null) {
			final Message message = new Message (MemcachedMessage.ADD_REQUEST, requestBuilder.build ());
			result = this.sendRequest (message, token, Boolean.class);
		}
		return (result);
	}
	
	@Override
	public CallbackCompletion<Boolean> append (final String key, final TValue data)
	{
		final CompletionToken token = this.generateToken ();
		this.transcript.traceDebugging ("appending to the record with key `%s` (with request token `%s`)...", key, token.getMessageId ());
		final MemcachedPayloads.AppendRequest.Builder requestBuilder = MemcachedPayloads.AppendRequest.newBuilder ();
		requestBuilder.setToken (token);
		requestBuilder.setKey (key);
		CallbackCompletion<Boolean> result = null;
		try {
			final byte[] dataBytes = this.encoder.encode (data, this.encoder.getEncodingMetadata ());
			requestBuilder.setValue (ByteString.copyFrom (dataBytes));
		} catch (final EncodingException exception) {
			this.exceptions.traceDeferredException (exception, "encoding the value for record with key `%s` failed; deferring!", key);
			result = CallbackCompletion.createFailure (exception);
		}
		if (result == null) {
			final Message message = new Message (MemcachedMessage.APPEND_REQUEST, requestBuilder.build ());
			result = this.sendRequest (message, token, Boolean.class);
		}
		return (result);
	}
	
	@Override
	public CallbackCompletion<Boolean> cas (final String key, final TValue data)
	{
		final CompletionToken token = this.generateToken ();
		this.transcript.traceDebugging ("cas-ing the record with key `%s` (with request token `%s`)...", key, token.getMessageId ());
		final MemcachedPayloads.CasRequest.Builder requestBuilder = MemcachedPayloads.CasRequest.newBuilder ();
		requestBuilder.setToken (token);
		requestBuilder.setKey (key);
		CallbackCompletion<Boolean> result = null;
		try {
			final byte[] dataBytes = this.encoder.encode (data, this.encoder.getEncodingMetadata ());
			requestBuilder.setValue (ByteString.copyFrom (dataBytes));
		} catch (final EncodingException exception) {
			this.exceptions.traceDeferredException (exception, "encoding the value for record with key `%s` failed; deferring!", key);
			result = CallbackCompletion.createFailure (exception);
		}
		if (result == null) {
			final Message message = new Message (MemcachedMessage.CAS_REQUEST, requestBuilder.build ());
			result = this.sendRequest (message, token, Boolean.class);
		}
		return (result);
	}
	
	@Override
	@SuppressWarnings ("unchecked")
	public CallbackCompletion<Map<String, TValue>> getBulk (final List<String> keys)
	{
		return (this.sendGetRequest (keys, (Class<Map<String, TValue>>) ((Class<?>) Map.class)));
	}
	
	@Override
	public CallbackCompletion<Void> initialize ()
	{
		this.transcript.traceDebugging ("initializing proxy...");
		final InitRequest.Builder requestBuilder = InitRequest.newBuilder ();
		requestBuilder.setToken (this.generateToken ());
		requestBuilder.setBucket (this.bucket);
		return (this.connect (MemcachedSession.CONNECTOR, new Message (KeyValueMessage.ACCESS, requestBuilder.build ())));
	}
	
	@Override
	public CallbackCompletion<Boolean> prepend (final String key, final TValue data)
	{
		final CompletionToken token = this.generateToken ();
		this.transcript.traceDebugging ("prepending to the record with key `%s` (with request token `%s`)...", key, token.getMessageId ());
		final MemcachedPayloads.PrependRequest.Builder requestBuilder = MemcachedPayloads.PrependRequest.newBuilder ();
		requestBuilder.setToken (token);
		requestBuilder.setKey (key);
		CallbackCompletion<Boolean> result = null;
		try {
			final byte[] dataBytes = this.encoder.encode (data, this.encoder.getEncodingMetadata ());
			requestBuilder.setValue (ByteString.copyFrom (dataBytes));
		} catch (final EncodingException exception) {
			this.exceptions.traceDeferredException (exception, "encoding the value for record with key `%s` failed; deferring!", key);
			result = CallbackCompletion.createFailure (exception);
		}
		if (result == null) {
			final Message message = new Message (MemcachedMessage.PREPEND_REQUEST, requestBuilder.build ());
			result = this.sendRequest (message, token, Boolean.class);
		}
		return (result);
	}
	
	@Override
	public CallbackCompletion<Boolean> replace (final String key, final int exp, final TValue data)
	{
		final CompletionToken token = this.generateToken ();
		this.transcript.traceDebugging ("replacing the record with key `%s` (with request token `%s`)...", key, token.getMessageId ());
		final MemcachedPayloads.ReplaceRequest.Builder requestBuilder = MemcachedPayloads.ReplaceRequest.newBuilder ();
		requestBuilder.setToken (token);
		requestBuilder.setKey (key);
		requestBuilder.setExpTime (exp);
		CallbackCompletion<Boolean> result = null;
		try {
			final byte[] dataBytes = this.encoder.encode (data, this.encoder.getEncodingMetadata ());
			requestBuilder.setValue (ByteString.copyFrom (dataBytes));
		} catch (final EncodingException exception) {
			this.exceptions.traceDeferredException (exception, "encoding the value for record with key `%s` failed; deferring!", key);
			result = CallbackCompletion.createFailure (exception);
		}
		if (result == null) {
			final Message message = new Message (MemcachedMessage.REPLACE_REQUEST, requestBuilder.build ());
			result = this.sendRequest (message, token, Boolean.class);
		}
		return (result);
	}
	
	/**
	 * Returns a proxy for key-value distributed storage systems.
	 * 
	 * @param configuration
	 *            the execution environment of a connector
	 * @param encoder
	 *            encoder used for serializing and deserializing data stored in
	 *            the key-value store
	 * @return the proxy
	 */
	public static <T extends Object> MemcacheKvStoreConnectorProxy<T> create (final ConnectorConfiguration configuration, final DataEncoder<T> encoder)
	{
		final MemcacheKvStoreConnectorProxy<T> proxy = new MemcacheKvStoreConnectorProxy<T> (configuration, encoder);
		return (proxy);
	}
	
	protected final String bucket;
}
