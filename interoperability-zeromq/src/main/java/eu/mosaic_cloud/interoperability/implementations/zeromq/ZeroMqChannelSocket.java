/*
 * #%L
 * interoperability-zeromq
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

package eu.mosaic_cloud.interoperability.implementations.zeromq;


import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.exceptions.core.ExceptionTracer;
import eu.mosaic_cloud.transcript.core.Transcript;
import eu.mosaic_cloud.transcript.tools.TranscriptExceptionTracer;
import org.zeromq.ZMQ;


public final class ZeroMqChannelSocket
		extends Object
{
	public ZeroMqChannelSocket (final String self, final Runnable dequeueTrigger, final ExceptionTracer exceptions)
	{
		super ();
		Preconditions.checkNotNull (self);
		this.transcript = Transcript.create (this);
		this.exceptions = TranscriptExceptionTracer.create (this.transcript, exceptions);
		this.self = self;
		this.inboundPackets = new LinkedBlockingQueue<Packet> ();
		this.outboundPackets = new LinkedBlockingQueue<Packet> ();
		this.dequeueTrigger = dequeueTrigger;
		this.shouldStop = false;
		this.looper = new Looper ();
		this.looper.start ();
	}
	
	public final void accept (final String endpoint)
	{
		Preconditions.checkNotNull (endpoint);
		this.transcript.traceDebugging ("accepting on `%s`...", endpoint);
		if (this.socket == null)
			try {
				Thread.sleep (100);
			} catch (final InterruptedException exception) {
				this.exceptions.traceIgnoredException (exception);
			}
		if (this.socket == null)
			throw (new IllegalStateException ());
		this.socket.bind (endpoint);
		try {
			Thread.sleep (100);
		} catch (final InterruptedException exception) {
			this.exceptions.traceIgnoredException (exception);
		}
	}
	
	public final void connect (final String endpoint)
	{
		Preconditions.checkNotNull (endpoint);
		this.transcript.traceDebugging ("connecting to `%s`...", endpoint);
		if (this.socket == null)
			try {
				Thread.sleep (100);
			} catch (final InterruptedException exception) {
				this.exceptions.traceIgnoredException (exception);
			}
		if (this.socket == null)
			throw (new IllegalStateException ());
		this.socket.connect (endpoint);
		try {
			Thread.sleep (100);
		} catch (final InterruptedException exception) {
			this.exceptions.traceIgnoredException (exception);
		}
	}
	
	public final Packet dequeue ()
	{
		return (this.inboundPackets.poll ());
	}
	
	public final Packet dequeue (final long timeout)
	{
		try {
			return (this.inboundPackets.poll (timeout, TimeUnit.MILLISECONDS));
		} catch (final InterruptedException exception) {
			this.exceptions.traceIgnoredException (exception);
			return (null);
		}
	}
	
	public final boolean enqueue (final Packet packet)
	{
		return (this.outboundPackets.offer (packet));
	}
	
	public final boolean enqueue (final Packet packet, final long timeout)
	{
		try {
			return (this.outboundPackets.offer (packet, timeout, TimeUnit.MILLISECONDS));
		} catch (final InterruptedException exception) {
			this.exceptions.traceIgnoredException (exception);
			return (false);
		}
	}
	
	public final void terminate ()
	{
		this.transcript.traceDebugging ("terminating...");
		this.shouldStop = true;
	}
	
	private final void failed ()
	{
		this.transcript.traceError ("socket failed; ignoring!");
	}
	
	private final void loop ()
	{
		this.transcript.traceDebugging ("loopping...");
		final ZMQ.Poller poller = ZeroMqChannelSocket.context.poller (3);
		while (true) {
			if (this.shouldStop)
				break;
			final int inboundPollIndex;
			final int outboundPollIndex;
			final int errorPollIndex;
			if (this.inboundPackets.remainingCapacity () > 0)
				inboundPollIndex = poller.register (this.socket, ZMQ.Poller.POLLIN);
			else
				inboundPollIndex = -1;
			if (!this.outboundPackets.isEmpty ())
				outboundPollIndex = poller.register (this.socket, ZMQ.Poller.POLLOUT);
			else
				outboundPollIndex = -1;
			errorPollIndex = poller.register (this.socket, ZMQ.Poller.POLLERR);
			if (poller.poll (5 * 1000) > 0) {
				if (poller.pollerr (errorPollIndex) || ((this.socket.getEvents () & ZMQ.Poller.POLLERR) != 0))
					this.failed ();
				if ((inboundPollIndex >= 0) && poller.pollin (inboundPollIndex))
					this.receive ();
				if ((outboundPollIndex >= 0) && poller.pollout (outboundPollIndex))
					this.send ();
			}
			poller.unregister (this.socket);
		}
	}
	
	private final void receive ()
	{
		this.transcript.traceDebugging ("receiving packet...");
		final String peer;
		final byte[] peer_;
		final byte[] delimiter;
		final byte[] header;
		final byte[] payload;
		peer_ = this.socket.recv (0);
		if (peer_ == null) {
			this.transcript.traceError ("error encountered while receiving packet peer part; ignoring!");
			this.receiveFlush ();
			return;
		}
		if (!this.socket.hasReceiveMore ()) {
			this.transcript.traceError ("error encountered while decoding packet: missing delimiter; ignoring!");
			this.receiveFlush ();
			return;
		}
		delimiter = this.socket.recv (0);
		if (delimiter == null) {
			this.transcript.traceError ("error encountered while receiving packet delimiter part: ignoring!");
			this.receiveFlush ();
			return;
		}
		if (delimiter.length != 0) {
			this.transcript.traceError ("error encountered while decoding packet: non-empty delimiter; ignoring!");
			this.receiveFlush ();
			return;
		}
		if (!this.socket.hasReceiveMore ()) {
			this.transcript.traceError ("error encounterd while decoding packet: missing header; ignoring!");
			this.receiveFlush ();
			return;
		}
		header = this.socket.recv (0);
		if (header == null) {
			this.transcript.traceError ("error encountered while receiving packet header part; ignoring!");
			this.receiveFlush ();
			return;
		}
		if (this.socket.hasReceiveMore ()) {
			payload = this.socket.recv (0);
			if (payload == null) {
				this.transcript.traceError ("error encountered while receiving packet payload part; ignoring!");
				this.receiveFlush ();
				return;
			}
		} else
			payload = null;
		if (this.socket.hasReceiveMore ()) {
			this.transcript.traceError ("error encountered while receiving packet: unexpected garbage; ignoring!");
			this.receiveFlush ();
			return;
		}
		peer = new String (peer_);
		final Packet packet = new Packet (peer, ByteBuffer.wrap (header), payload != null ? ByteBuffer.wrap (payload) : null);
		this.inboundPackets.add (packet);
		if (this.dequeueTrigger != null)
			try {
				this.dequeueTrigger.run ();
			} catch (final Error exception) {
				this.transcript.traceError ("error encountered while executing dequeue trigger; ignoring!");
			}
	}
	
	private final void receiveFlush ()
	{
		while (this.socket.hasReceiveMore ())
			this.socket.recv (0);
	}
	
	private final void send ()
	{
		this.transcript.traceDebugging ("sending packet...");
		final Packet packet = this.outboundPackets.remove ();
		if (!this.socket.send (packet.peer.getBytes (), ZMQ.SNDMORE)) {
			this.transcript.traceError ("error encountered while sending packet: ignoring!");
			return;
		}
		if (!this.socket.send (new byte[0], ZMQ.SNDMORE)) {
			this.transcript.traceError ("error encountered while sending packet: ignoring!");
			return;
		}
		final byte[] header = new byte[packet.header.remaining ()];
		packet.header.get (header);
		final byte[] payload;
		if (packet.payload != null) {
			payload = new byte[packet.payload.remaining ()];
			packet.payload.get (payload);
		} else
			payload = null;
		if (!this.socket.send (header, (payload != null) ? ZMQ.SNDMORE : 0)) {
			this.transcript.traceError ("error encountered while sending packet: ignoring!");
			return;
		}
		if (payload != null)
			if (!this.socket.send (payload, 0)) {
				this.transcript.traceError ("error encountered while sending packet: ignoring!");
				return;
			}
	}
	
	private final void setup ()
	{
		this.transcript.traceDebugging ("setting-up...");
		this.socket = ZeroMqChannelSocket.context.socket (ZMQ.XREP);
		this.socket.setIdentity (this.self.getBytes ());
	}
	
	private final void teardown ()
	{
		this.transcript.traceDebugging ("tearing-down...");
		this.socket.close ();
		this.socket = null;
	}
	
	private final Runnable dequeueTrigger;
	private final TranscriptExceptionTracer exceptions;
	private final LinkedBlockingQueue<Packet> inboundPackets;
	private final Thread looper;
	private final LinkedBlockingQueue<Packet> outboundPackets;
	private final String self;
	private boolean shouldStop;
	private ZMQ.Socket socket;
	private final Transcript transcript;
	static {
		context = ZMQ.context (1);
	}
	private static final ZMQ.Context context;
	
	public static final class Packet
			extends Object
	{
		public Packet (final String peer, final ByteBuffer header, final ByteBuffer payload)
		{
			super ();
			Preconditions.checkNotNull (peer);
			Preconditions.checkNotNull (header);
			this.peer = peer;
			this.header = header;
			this.payload = payload;
		}
		
		public final ByteBuffer header;
		public final ByteBuffer payload;
		public final String peer;
	}
	
	private final class Looper
			extends Thread
	{
		Looper ()
		{
			this.setName (String.format ("%s#%08x", ZeroMqChannelSocket.this.getClass ().getSimpleName (), Integer.valueOf (System.identityHashCode (ZeroMqChannelSocket.this))));
			this.setDaemon (true);
		}
		
		@Override
		public final void run ()
		{
			ZeroMqChannelSocket.this.setup ();
			ZeroMqChannelSocket.this.loop ();
			ZeroMqChannelSocket.this.teardown ();
		}
	}
}