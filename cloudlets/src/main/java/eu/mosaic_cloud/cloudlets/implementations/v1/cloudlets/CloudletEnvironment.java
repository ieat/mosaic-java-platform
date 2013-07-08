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

package eu.mosaic_cloud.cloudlets.implementations.v1.cloudlets;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import eu.mosaic_cloud.connectors.implementations.v1.core.ConnectorEnvironment;
import eu.mosaic_cloud.connectors.v1.components.ZZZ_comp_ComponentConnector;
import eu.mosaic_cloud.connectors.v1.core.ZZZ_core_ConnectorsFactory;
import eu.mosaic_cloud.platform.v1.core.configuration.Configuration;
import eu.mosaic_cloud.tools.callbacks.core.CallbackReactor;
import eu.mosaic_cloud.tools.exceptions.core.ExceptionResolution;
import eu.mosaic_cloud.tools.exceptions.core.ExceptionTracer;
import eu.mosaic_cloud.tools.miscellaneous.SupplementaryEnvironment;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;

import com.google.common.base.Preconditions;


public final class CloudletEnvironment
{
	private CloudletEnvironment (final Configuration configuration, final Class<?> cloudletCallbackClass, final Class<?> cloudletContextClass, final ClassLoader classLoader, final ZZZ_core_ConnectorsFactory connectors, final ConnectorEnvironment connectorEnvironment, final ZZZ_comp_ComponentConnector componentConnector, final CallbackReactor reactor, final ThreadingContext threading, final ExceptionTracer exceptions, final Map<String, Object> supplementary) {
		super ();
		Preconditions.checkNotNull (configuration);
		Preconditions.checkNotNull (cloudletCallbackClass);
		Preconditions.checkNotNull (cloudletContextClass);
		Preconditions.checkNotNull (classLoader);
		Preconditions.checkNotNull (connectors);
		Preconditions.checkNotNull (connectorEnvironment);
		Preconditions.checkNotNull (componentConnector);
		Preconditions.checkNotNull (reactor);
		Preconditions.checkNotNull (threading);
		Preconditions.checkNotNull (exceptions);
		Preconditions.checkNotNull (supplementary);
		this.configuration = configuration;
		this.cloudletCallbackClass = cloudletCallbackClass;
		this.cloudletContextClass = cloudletContextClass;
		this.classLoader = classLoader;
		this.connectors = connectors;
		this.connectorEnvironment = connectorEnvironment;
		this.componentConnector = componentConnector;
		this.reactor = reactor;
		this.threading = threading;
		this.exceptions = exceptions;
		this.supplementary = SupplementaryEnvironment.create (supplementary, new UncaughtExceptionHandler () {
			@SuppressWarnings ("synthetic-access")
			@Override
			public void uncaughtException (final Thread thread, final Throwable exception) {
				CloudletEnvironment.this.exceptions.trace (ExceptionResolution.Ignored, exception);
			}
		});
	}
	
	public ClassLoader getClassLoader () {
		return this.classLoader;
	}
	
	public Class<?> getCloudletCallbackClass () {
		return this.cloudletCallbackClass;
	}
	
	public Class<?> getCloudletContextClass () {
		return this.cloudletContextClass;
	}
	
	public ZZZ_comp_ComponentConnector getComponentConnector () {
		return this.componentConnector;
	}
	
	public Configuration getConfiguration () {
		return this.configuration;
	}
	
	public ConnectorEnvironment getConnectorEnvironment () {
		return this.connectorEnvironment;
	}
	
	public ZZZ_core_ConnectorsFactory getConnectors () {
		return this.connectors;
	}
	
	public ExceptionTracer getExceptions () {
		return this.exceptions;
	}
	
	public CallbackReactor getReactor () {
		return this.reactor;
	}
	
	public SupplementaryEnvironment getSupplementary () {
		return this.supplementary;
	}
	
	public ThreadingContext getThreading () {
		return this.threading;
	}
	
	private final ClassLoader classLoader;
	private final Class<?> cloudletCallbackClass;
	private final Class<?> cloudletContextClass;
	private final ZZZ_comp_ComponentConnector componentConnector;
	private final Configuration configuration;
	private final ConnectorEnvironment connectorEnvironment;
	private final ZZZ_core_ConnectorsFactory connectors;
	private final ExceptionTracer exceptions;
	private final CallbackReactor reactor;
	private final SupplementaryEnvironment supplementary;
	private final ThreadingContext threading;
	
	public static final CloudletEnvironment create (final Configuration configuration, final Class<?> cloudletCallbackClass, final Class<?> cloudletContextClass, final ClassLoader classLoader, final ZZZ_core_ConnectorsFactory connectors, final ConnectorEnvironment connectorEnvironment, final ZZZ_comp_ComponentConnector componentConnector, final CallbackReactor reactor, final ThreadingContext threading, final ExceptionTracer exceptions) {
		return new CloudletEnvironment (configuration, cloudletCallbackClass, cloudletContextClass, classLoader, connectors, connectorEnvironment, componentConnector, reactor, threading, exceptions, new HashMap<String, Object> ());
	}
	
	public static final CloudletEnvironment create (final Configuration configuration, final Class<?> cloudletCallbackClass, final Class<?> cloudletContextClass, final ClassLoader classLoader, final ZZZ_core_ConnectorsFactory connectors, final ConnectorEnvironment connectorEnvironment, final ZZZ_comp_ComponentConnector componentConnector, final CallbackReactor reactor, final ThreadingContext threading, final ExceptionTracer exceptions, final Map<String, Object> supplementary) {
		return new CloudletEnvironment (configuration, cloudletCallbackClass, cloudletContextClass, classLoader, connectors, connectorEnvironment, componentConnector, reactor, threading, exceptions, supplementary);
	}
}
