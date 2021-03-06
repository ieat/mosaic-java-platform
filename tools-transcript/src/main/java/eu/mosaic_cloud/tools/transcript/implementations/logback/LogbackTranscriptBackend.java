/*
 * #%L
 * mosaic-tools-transcript
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

package eu.mosaic_cloud.tools.transcript.implementations.logback;


import eu.mosaic_cloud.tools.exceptions.core.ExceptionResolution;
import eu.mosaic_cloud.tools.miscellaneous.ExtendedFormatter;
import eu.mosaic_cloud.tools.transcript.core.TranscriptBackend;
import eu.mosaic_cloud.tools.transcript.core.TranscriptTraceType;

import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import ch.qos.logback.classic.Logger;


public final class LogbackTranscriptBackend
			extends Object
			implements
				TranscriptBackend
{
	private LogbackTranscriptBackend (final Logger logger, final ExtendedFormatter formatter) {
		super ();
		Preconditions.checkNotNull (logger);
		Preconditions.checkNotNull (formatter);
		this.logger = logger;
		this.formatter = formatter;
	}
	
	@Override
	public final <_Logger_ extends Object> _Logger_ adaptAs (final Class<_Logger_> loggerClass) {
		final Object logger;
		if (loggerClass == org.slf4j.Logger.class)
			logger = this.logger;
		else if (loggerClass == Logger.class)
			logger = this.logger;
		else
			logger = null;
		return (loggerClass.cast (logger));
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception) {
		this.trace (this.map (resolution), null, null, exception);
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception, final String message) {
		this.trace (this.map (resolution), message, null, exception);
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception, final String format, final Object ... tokens) {
		this.trace (this.map (resolution), format, tokens, exception);
	}
	
	@Override
	public final void trace (final TranscriptTraceType type, final String message) {
		this.trace (type, message, null, null);
	}
	
	@Override
	public final void trace (final TranscriptTraceType type, final String format, final Object ... tokens) {
		this.trace (type, format, tokens, null);
	}
	
	private final String format (final String format, final Object[] tokens) {
		if (format == null) {
			if (tokens != null)
				throw (new IllegalArgumentException ());
			return ("");
		}
		if (tokens == null)
			return (format);
		return (this.formatter.format (format, tokens));
	}
	
	private final TranscriptTraceType map (final ExceptionResolution resolution) {
		switch (resolution) {
			case Handled :
				return (TranscriptTraceType.Trace);
			case Deferred :
				return (TranscriptTraceType.Debugging);
			case Ignored :
				return (TranscriptTraceType.Warning);
			default :
				throw (new AssertionError ());
		}
	}
	
	private final void trace (final TranscriptTraceType type, final String format, final Object[] tokens, final Throwable exception) {
		Preconditions.checkNotNull (type);
		switch (type) {
			case Information :
				if (this.logger.isInfoEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.info (message, exception);
					else
						this.logger.info (message);
				}
				break;
			case Warning :
				if (this.logger.isWarnEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.warn (message, exception);
					else
						this.logger.warn (message);
				}
				break;
			case Error :
				if (this.logger.isErrorEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.error (message, exception);
					else
						this.logger.error (message);
				}
				break;
			case Debugging :
				if (this.logger.isDebugEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.debug (message, exception);
					else
						this.logger.debug (message);
				}
				break;
			case Trace :
				if (this.logger.isTraceEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.trace (message, exception);
					else
						this.logger.trace (message);
				}
				break;
		}
	}
	
	private final ExtendedFormatter formatter;
	private final Logger logger;
	
	public static final LogbackTranscriptBackend create (final Class<?> owner) {
		Preconditions.checkNotNull (owner);
		final String loggerName = owner.getName ();
		return (new LogbackTranscriptBackend ((Logger) LoggerFactory.getLogger (loggerName), ExtendedFormatter.defaultInstance));
	}
	
	public static final LogbackTranscriptBackend create (final Object owner, final boolean individual) {
		Preconditions.checkNotNull (owner);
		final String loggerName;
		if (individual)
			loggerName = String.format ("%s.%08x", owner.getClass ().getName ().replace ('$', '.'), Integer.valueOf (System.identityHashCode (owner)));
		else
			loggerName = owner.getClass ().getName ().replace ('$', '.');
		return (new LogbackTranscriptBackend ((Logger) LoggerFactory.getLogger (loggerName), ExtendedFormatter.defaultInstance));
	}
}
