/*
 * #%L
 * mosaic-drivers-stubs-kv-common
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

package eu.mosaic_cloud.drivers.kvstore.riak.interop;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import eu.mosaic_cloud.drivers.ConfigProperties;
import eu.mosaic_cloud.drivers.DriverNotFoundException;
import eu.mosaic_cloud.drivers.exceptions.ConnectionException;
import eu.mosaic_cloud.drivers.interop.AbstractDriverStub;
import eu.mosaic_cloud.drivers.interop.DriverConnectionData;
import eu.mosaic_cloud.drivers.kvstore.riak.AbstractKeyValueDriver;
import eu.mosaic_cloud.drivers.kvstore.riak.KeyValueDriverFactory;
import eu.mosaic_cloud.drivers.kvstore.riak.KeyValueOperations;
import eu.mosaic_cloud.drivers.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.drivers.ops.IResult;
import eu.mosaic_cloud.interoperability.core.Message;
import eu.mosaic_cloud.interoperability.core.Session;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;
import eu.mosaic_cloud.platform.implementation.v2.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.interop.idl.IdlCommon;
import eu.mosaic_cloud.platform.interop.idl.IdlCommon.AbortRequest;
import eu.mosaic_cloud.platform.interop.idl.IdlCommon.CompletionToken;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.DeleteRequest;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.GetRequest;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.InitRequest;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.ListRequest;
import eu.mosaic_cloud.platform.interop.idl.kvstore.KeyValuePayloads.SetRequest;
import eu.mosaic_cloud.platform.interop.specs.kvstore.KeyValueMessage;
import eu.mosaic_cloud.platform.interop.specs.kvstore.KeyValueSession;
import eu.mosaic_cloud.platform.v2.configuration.Configuration;
import eu.mosaic_cloud.platform.v2.serialization.EncodingMetadata;
import eu.mosaic_cloud.tools.exceptions.core.FallbackExceptionTracer;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;
import eu.mosaic_cloud.tools.transcript.core.Transcript;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Stub for the driver for key-value distributed storage systems. This is used for communicating with a key-value driver.
 * 
 * @author Georgiana Macariu
 */
public class KeyValueStub
			extends AbstractDriverStub
{
	/**
	 * Creates a new stub for the key-value store driver.
	 * 
	 * @param config
	 *            the configuration data for the stub and driver
	 * @param transmitter
	 *            the transmitter object which will send responses to requests submitted to this stub
	 * @param driver
	 *            the driver used for processing requests submitted to this stub
	 * @param commChannel
	 *            the channel for communicating with connectors
	 */
	public KeyValueStub (final Configuration config, final KeyValueResponseTransmitter transmitter, final AbstractKeyValueDriver driver, final ZeroMqChannel commChannel) {
		super (config, transmitter, driver, commChannel);
	}
	
	@Override
	public synchronized void destroy () {
		synchronized (AbstractDriverStub.MONITOR) {
			final int ref = AbstractDriverStub.decDriverReference (this);
			if ((ref == 0)) {
				final DriverConnectionData cData = KeyValueStub.readConnectionData (this.configuration);
				KeyValueStub.stubs.remove (cData);
			}
		}
		super.destroy ();
	}
	
	/**
	 * Handles basic key-value store operations.
	 * 
	 * @param message
	 *            the message containing the operation request
	 * @param session
	 *            the session
	 * @param driver
	 *            the driver to handle the operation request
	 * @param transmitterClass
	 *            class of the response transmitter
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings ("unchecked")
	protected void handleKVOperation (final Message message, final Session session, final AbstractKeyValueDriver driver, final Class<? extends KeyValueResponseTransmitter> transmitterClass)
				throws IOException, ClassNotFoundException {
		byte[] data;
		boolean unknownMessage = false;
		final KeyValueMessage kvMessage = (KeyValueMessage) message.specification;
		CompletionToken token = null;
		String key;
		final String messagePrefix = "KeyValueStub - Received request for ";
		switch (kvMessage) {
			case ACCESS :
				KeyValueStub.logger.trace ("Received initiation message");
				final KeyValuePayloads.InitRequest initRequest = (InitRequest) message.payload;
				token = initRequest.getToken ();
				final String bucket = initRequest.getBucket ();
				driver.registerClient (token.getClientId (), bucket);
				break;
			case ABORTED :
				KeyValueStub.logger.trace ("Received termination message");
				final IdlCommon.AbortRequest abortRequest = (AbortRequest) message.payload;
				token = abortRequest.getToken ();
				driver.unregisterClient (token.getClientId ());
				break;
			case SET_REQUEST :
				final KeyValuePayloads.SetRequest setRequest = (SetRequest) message.payload;
				token = setRequest.getToken ();
				key = setRequest.getKey ();
				data = setRequest.getValue ().toByteArray ();
				final eu.mosaic_cloud.platform.interop.common.kv.KeyValueMessage messageData = new eu.mosaic_cloud.platform.interop.common.kv.KeyValueMessage (key, data, setRequest.getEnvelope ().getContentEncoding (), setRequest.getEnvelope ().getContentType ());
				KeyValueStub.logger.trace (messagePrefix + kvMessage.toString () + " key: " + key + " - request id: " + token.getMessageId () + " client id: " + token.getClientId ());
				// NOTE: execute operation
				final DriverOperationFinishedHandler setCallback = new DriverOperationFinishedHandler (token, session, driver.getClass (), transmitterClass);
				final IResult<Boolean> resultSet = driver.invokeSetOperation (token.getClientId (), messageData, setCallback);
				setCallback.setDetails (KeyValueOperations.SET, resultSet);
				break;
			case GET_REQUEST :
				final KeyValuePayloads.GetRequest getRequest = (GetRequest) message.payload;
				token = getRequest.getToken ();
				final DriverOperationFinishedHandler getCallback = new DriverOperationFinishedHandler (token, session, driver.getClass (), transmitterClass);
				if (getRequest.getKeyCount () != 1) {
					// NOTE: error - the simple driver can handle only single-key get
					KeyValueStub.logger.error ("Basic driver can handle only single-key GET.");
					driver.handleUnsupportedOperationError (kvMessage.toString (), getCallback);
					break;
				}
				key = getRequest.getKey (0);
				KeyValueStub.logger.trace (messagePrefix + kvMessage.toString () + " key: " + key + " - request id: " + token.getMessageId () + " client id: " + token.getClientId ());
				final EncodingMetadata expectedEncoding = new EncodingMetadata (getRequest.getEnvelope ().getContentType (), getRequest.getEnvelope ().getContentEncoding ());
				final IResult<eu.mosaic_cloud.platform.interop.common.kv.KeyValueMessage> resultGet = driver.invokeGetOperation (token.getClientId (), key, expectedEncoding, getCallback);
				getCallback.setDetails (KeyValueOperations.GET, resultGet);
				break;
			case DELETE_REQUEST :
				final KeyValuePayloads.DeleteRequest delRequest = (DeleteRequest) message.payload;
				token = delRequest.getToken ();
				key = delRequest.getKey ();
				KeyValueStub.logger.trace (messagePrefix + kvMessage.toString () + " key: " + key + " - request id: " + token.getMessageId () + " client id: " + token.getClientId ());
				final DriverOperationFinishedHandler delCallback = new DriverOperationFinishedHandler (token, session, driver.getClass (), transmitterClass);
				final IResult<Boolean> resultDelete = driver.invokeDeleteOperation (token.getClientId (), key, delCallback);
				delCallback.setDetails (KeyValueOperations.DELETE, resultDelete);
				break;
			case LIST_REQUEST :
				final KeyValuePayloads.ListRequest listRequest = (ListRequest) message.payload;
				token = listRequest.getToken ();
				KeyValueStub.logger.trace (messagePrefix + kvMessage.toString () + " - request id: " + token.getMessageId () + " client id: " + token.getClientId ());
				final DriverOperationFinishedHandler listCallback = new DriverOperationFinishedHandler (token, session, driver.getClass (), transmitterClass);
				final IResult<List<String>> resultList = driver.invokeListOperation (token.getClientId (), listCallback);
				listCallback.setDetails (KeyValueOperations.LIST, resultList);
				break;
			case ERROR :
				token = ((IdlCommon.Error) message.payload).getToken ();
				unknownMessage = true;
				break;
			case OK :
				token = ((IdlCommon.Ok) message.payload).getToken ();
				unknownMessage = true;
				break;
			case GET_REPLY :
				token = ((KeyValuePayloads.GetReply) message.payload).getToken ();
				unknownMessage = true;
				break;
			case LIST_REPLY :
				token = ((KeyValuePayloads.ListReply) message.payload).getToken ();
				unknownMessage = true;
				break;
			default :
				break;
		}
		if (unknownMessage) {
			this.handleUnknownMessage (session, driver, kvMessage.toString (), token, transmitterClass);
		}
	}
	
	protected void handleUnknownMessage (final Session session, final AbstractKeyValueDriver driver, final String messageType, final CompletionToken token, final Class<? extends KeyValueResponseTransmitter> transmitterClass) {
		KeyValueStub.logger.error ("Unexpected message type: " + messageType + " - request id: " + token.getMessageId () + " client id: " + token.getClientId ());
		// NOTE: create callback
		final DriverOperationFinishedHandler failCallback = new DriverOperationFinishedHandler (token, session, driver.getClass (), transmitterClass);
		driver.handleUnsupportedOperationError (messageType, failCallback);
	}
	
	@Override
	protected void startOperation (final Message message, final Session session)
				throws IOException, ClassNotFoundException {
		Preconditions.checkArgument (message.specification instanceof KeyValueMessage);
		final AbstractKeyValueDriver driver = super.getDriver (this.driverClass);
		this.handleKVOperation (message, session, driver, KeyValueResponseTransmitter.class);
	}
	
	private Class<? extends AbstractKeyValueDriver> driverClass;
	
	/**
	 * Returns a stub for the key-value store driver.
	 * 
	 * @param config
	 *            the configuration data for the stub and driver
	 * @param the
	 *            context for creating threads
	 * @param channel
	 *            the channel used by the driver for receiving requests
	 * @return the driver stub
	 */
	public static KeyValueStub create (final Configuration config, final ThreadingContext threadingContext, final ZeroMqChannel channel) {
		final DriverConnectionData cData = KeyValueStub.readConnectionData (config);
		KeyValueStub stub;
		synchronized (AbstractDriverStub.MONITOR) {
			stub = KeyValueStub.stubs.get (cData);
			try {
				if (stub == null) {
					KeyValueStub.logger.trace ("KeyValueStub: create new stub.");
					final KeyValueResponseTransmitter transmitter = new KeyValueResponseTransmitter ();
					final String driverName = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_6, String.class, "");
					final AbstractKeyValueDriver driver = KeyValueDriverFactory.createDriver (driverName, config, threadingContext);
					stub = new KeyValueStub (config, transmitter, driver, channel);
					stub.driverClass = KeyValueDriverFactory.DriverType.valueOf (driverName.toUpperCase (Locale.ENGLISH)).getDriverClass ();
					KeyValueStub.stubs.put (cData, stub);
					AbstractDriverStub.incDriverReference (stub);
					channel.accept (KeyValueSession.DRIVER, stub);
				} else {
					KeyValueStub.logger.trace ("KeyValueStub: use existing stub.");
					AbstractDriverStub.incDriverReference (stub);
				}
			} catch (final DriverNotFoundException e) {
				FallbackExceptionTracer.defaultInstance.traceDeferredException (e);
				final ConnectionException e1 = new ConnectionException ("The required key-value driver cannot be provided: " + e.getMessage (), e);
				FallbackExceptionTracer.defaultInstance.traceIgnoredException (e1);
			}
		}
		return stub;
	}
	
	public static KeyValueStub createDetached (final Configuration config, final ThreadingContext threadingContext, final ZeroMqChannel channel) {
		KeyValueStub stub;
		try {
			KeyValueStub.logger.trace ("KeyValueStub: create new stub.");
			final KeyValueResponseTransmitter transmitter = new KeyValueResponseTransmitter ();
			final String driverName = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_6, String.class, "");
			final AbstractKeyValueDriver driver = KeyValueDriverFactory.createDriver (driverName, config, threadingContext);
			stub = new KeyValueStub (config, transmitter, driver, channel);
			stub.driverClass = KeyValueDriverFactory.DriverType.valueOf (driverName.toUpperCase (Locale.ENGLISH)).getDriverClass ();
			AbstractDriverStub.incDriverReference (stub);
			channel.accept (KeyValueSession.DRIVER, stub);
		} catch (final DriverNotFoundException e) {
			FallbackExceptionTracer.defaultInstance.traceDeferredException (e);
			final ConnectionException e1 = new ConnectionException ("The required key-value driver cannot be provided: " + e.getMessage (), e);
			FallbackExceptionTracer.defaultInstance.traceIgnoredException (e1);
			stub = null;
		}
		return stub;
	}
	
	/**
	 * Reads resource connection data from the configuration data.
	 * 
	 * @param config
	 *            the configuration data
	 * @return resource connection data
	 */
	protected static DriverConnectionData readConnectionData (final Configuration config) {
		final String resourceHost = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_0, String.class, "localhost");
		final int resourcePort = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_1, Integer.class, 0);
		final String driver = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_6, String.class, "");
		final String user = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_5, String.class, "");
		final String passwd = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_4, String.class, "");
		DriverConnectionData cData;
		if ("".equals (user) && "".equals (passwd)) {
			cData = new DriverConnectionData (resourceHost, resourcePort, driver);
		} else {
			cData = new DriverConnectionData (resourceHost, resourcePort, driver, user, passwd);
		}
		return cData;
	}
	
	private static final Logger logger = Transcript.create (KeyValueStub.class).adaptAs (Logger.class);
	private static Map<DriverConnectionData, KeyValueStub> stubs = new HashMap<DriverConnectionData, KeyValueStub> ();
	
	/**
	 * Handler for processing responses of the requests submitted to the stub. This will basically call the transmitter
	 * associated with the stub.
	 * 
	 * @author Georgiana Macariu
	 */
	@SuppressWarnings ("rawtypes")
	protected class DriverOperationFinishedHandler
				implements
					IOperationCompletionHandler
	{
		public DriverOperationFinishedHandler (final CompletionToken complToken, final Session session, final Class<? extends AbstractKeyValueDriver> driverClass, final Class<? extends KeyValueResponseTransmitter> transmitterClass) {
			this.complToken = complToken;
			this.signal = new CountDownLatch (1);
			this.driver = KeyValueStub.this.getDriver (driverClass);
			this.transmitter = KeyValueStub.this.getResponseTransmitter (transmitterClass);
			this.session = session;
		}
		
		@Override
		public void onFailure (final Throwable error) {
			try {
				this.signal.await ();
			} catch (final InterruptedException e) {
				KeyValueStub.this.exceptions.traceIgnoredException (e);
			}
			this.driver.removePendingOperation (this.result);
			// NOTE: result is error
			this.transmitter.sendResponse (this.session, this.complToken, this.operation, error.getMessage (), true);
		}
		
		@Override
		public void onSuccess (final Object response) {
			try {
				this.signal.await ();
			} catch (final InterruptedException e) {
				KeyValueStub.this.exceptions.traceIgnoredException (e);
			}
			this.driver.removePendingOperation (this.result);
			if (this.operation.equals (KeyValueOperations.GET)) {
				final Map<String, Object> resMap = new HashMap<String, Object> ();
				resMap.put ("dummy", response);
				this.transmitter.sendResponse (this.session, this.complToken, this.operation, resMap, false);
			} else {
				this.transmitter.sendResponse (this.session, this.complToken, this.operation, response, false);
			}
		}
		
		public void setDetails (final KeyValueOperations operation, final IResult<?> result) {
			this.operation = operation;
			this.result = result;
			this.signal.countDown ();
		}
		
		private final CompletionToken complToken;
		private final AbstractKeyValueDriver driver;
		private KeyValueOperations operation;
		private IResult<?> result;
		private final Session session;
		private final CountDownLatch signal;
		private final KeyValueResponseTransmitter transmitter;
	}
}
