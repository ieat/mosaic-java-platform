/*
 * #%L
 * interoperability-examples
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

package eu.mosaic_cloud.interoperability.examples.kv;


import java.util.HashMap;
import java.util.concurrent.Future;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.callbacks.core.CallbackReference;
import eu.mosaic_cloud.interoperability.core.Channel;
import eu.mosaic_cloud.interoperability.core.Message;
import eu.mosaic_cloud.interoperability.core.Session;
import eu.mosaic_cloud.interoperability.core.SessionCallbacks;
import eu.mosaic_cloud.tools.OutcomeFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class KvClient
		implements
			SessionCallbacks
{
	public KvClient ()
	{
		this.logger = LoggerFactory.getLogger (this.getClass ());
		this.futures = new HashMap<Long, OutcomeFuture<?>> ();
		this.session = null;
		this.sequence = 0;
	}
	
	@Override
	public final synchronized CallbackReference created (final Session session)
	{
		Preconditions.checkState (this.session == null);
		final OutcomeFuture<Boolean> future = (OutcomeFuture<Boolean>) this.futures.remove (Long.valueOf (0));
		if (future != null)
			future.trigger.succeeded (Boolean.TRUE);
		this.session = session;
		return (null);
	}
	
	@Override
	public final synchronized CallbackReference destroyed (final Session session)
	{
		Preconditions.checkState (this.session == session);
		for (final OutcomeFuture<?> future : this.futures.values ())
			future.cancel (true);
		this.futures.clear ();
		return (null);
	}
	
	@Override
	public final synchronized CallbackReference failed (final Session session, final Throwable exception)
	{
		Preconditions.checkState (this.session == session);
		return (null);
	}
	
	public final synchronized Future<String> get (final String key)
	{
		Preconditions.checkState (this.session != null);
		final long sequence = this.sequence++;
		final OutcomeFuture<String> future = OutcomeFuture.create ();
		this.futures.put (Long.valueOf (sequence), future);
		this.session.send (new Message (KvMessage.GetRequest, new KvPayloads.GetRequest (sequence, key)));
		return (future);
	}
	
	public final synchronized Future<Boolean> initialize (final Channel channel, final String serverIdentifier)
	{
		Preconditions.checkState (this.session == null);
		Preconditions.checkState (this.sequence == 0);
		final OutcomeFuture<Boolean> future = OutcomeFuture.create ();
		this.futures.put (Long.valueOf (0), future);
		channel.connect (serverIdentifier, KvSession.Client, new Message (KvMessage.Access, null), this);
		return (future);
	}
	
	public final synchronized Future<Boolean> put (final String key, final String value)
	{
		Preconditions.checkState (this.session != null);
		final long sequence = this.sequence++;
		final OutcomeFuture<Boolean> future = OutcomeFuture.create ();
		this.futures.put (Long.valueOf (sequence), future);
		this.session.send (new Message (KvMessage.PutRequest, new KvPayloads.PutRequest (sequence, key, value)));
		return (future);
	}
	
	@Override
	public final synchronized CallbackReference received (final Session session, final Message message)
	{
		Preconditions.checkState (this.session == session);
		switch ((KvMessage) message.specification) {
			case GetReply : {
				final KvPayloads.GetReply reply = (KvPayloads.GetReply) message.payload;
				this.logger.info ("get replied: {}", reply.value);
				final OutcomeFuture<String> future = (OutcomeFuture<String>) this.futures.remove (Long.valueOf (reply.sequence));
				if (future != null)
					future.trigger.succeeded (reply.value);
			}
				break;
			case Ok : {
				final KvPayloads.Ok reply = (KvPayloads.Ok) message.payload;
				this.logger.info ("ok replied: {}");
				final OutcomeFuture<Boolean> future = (OutcomeFuture<Boolean>) this.futures.remove (Long.valueOf (reply.sequence));
				if (future != null)
					future.trigger.succeeded (Boolean.TRUE);
			}
				break;
			case Error : {
				final KvPayloads.Error reply = (KvPayloads.Error) message.payload;
				this.logger.info ("error replied: {}");
				final OutcomeFuture<Boolean> future = (OutcomeFuture<Boolean>) this.futures.remove (Long.valueOf (reply.sequence));
				if (future != null)
					future.trigger.succeeded (Boolean.FALSE);
			}
				break;
			default: {
				this.logger.error ("unexpected message: {}", message.specification);
				session.send (new Message (KvMessage.Aborted, null));
			}
				break;
		}
		return (null);
	}
	
	private final HashMap<Long, OutcomeFuture<?>> futures;
	private final Logger logger;
	private long sequence;
	private Session session;
}