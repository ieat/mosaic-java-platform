/*
 * #%L
 * mosaic-platform-core
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

package eu.mosaic_cloud.platform.core.exceptions;


import eu.mosaic_cloud.tools.exceptions.core.ExceptionResolution;
import eu.mosaic_cloud.tools.exceptions.core.FallbackExceptionTracer;
import eu.mosaic_cloud.tools.transcript.core.Transcript;
import eu.mosaic_cloud.tools.transcript.tools.TranscriptExceptionTracer;


public final class ExceptionTracer
{
	public ExceptionTracer ()
	{
		super ();
		this.delegateTracer = TranscriptExceptionTracer.create (Transcript.create (this), FallbackExceptionTracer.defaultInstance);
	}
	
	public void trace (final ExceptionResolution resolution, final Throwable exception, final String message_)
	{
		final String message = message_ == null ? "encountered exception" : message_; // NOPMD
		switch (resolution) {
			case Handled :
				this.delegateTracer.traceHandledException (exception, message);
				break;
			case Ignored :
				this.delegateTracer.traceIgnoredException (exception, message);
				break;
			case Deferred :
				this.delegateTracer.traceDeferredException (exception, message);
				break;
			default:
				break;
		}
	}
	
	public void trace (final ExceptionResolution resolution, final Throwable exception, final String format, final Object ... tokens)
	{
		this.trace (resolution, exception, String.format (format, tokens));
	}
	
	public static void traceDeferred (final Throwable exception)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Deferred, exception, null);
	}
	
	public static void traceDeferred (final Throwable exception, final String format, final Object ... tokens)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Deferred, exception, format, tokens);
	}
	
	public static void traceHandled (final Throwable exception)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Handled, exception, null);
	}
	
	public static void traceHandled (final Throwable exception, final String format, final Object ... tokens)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Handled, exception, format, tokens);
	}
	
	public static void traceIgnored (final Throwable exception)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Ignored, exception, null);
	}
	
	public static void traceIgnored (final Throwable exception, final String format, final Object ... tokens)
	{
		ExceptionTracer.DEFAULT_INSTANCE.trace (ExceptionResolution.Ignored, exception, format, tokens);
	}
	
	private final TranscriptExceptionTracer delegateTracer;
	private static final ExceptionTracer DEFAULT_INSTANCE = new ExceptionTracer ();
}
