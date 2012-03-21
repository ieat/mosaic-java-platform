/*
 * #%L
 * mosaic-components-httpg-jetty-container
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

package eu.mosaic_cloud.components.httpg.jetty.container;


import eu.mosaic_cloud.components.implementations.basic.MosBasicComponentLauncher;

import com.google.common.base.Preconditions;


public final class JettyComponentLauncher
{
	private JettyComponentLauncher ()
	{
		super ();
		throw (new UnsupportedOperationException ());
	}
	
	public static void main (final String[] arguments)
			throws Throwable
	{
		Preconditions.checkArgument ((arguments != null) && (arguments.length >= 1), "invalid arguments: expected `<application-war> ...`");
		final String[] finalArguments = new String[arguments.length];
		System.arraycopy (arguments, 1, finalArguments, 0, arguments.length - 1);
		finalArguments[finalArguments.length - 1] = String.format ("{\"%s\":\"%s\"}", "war", arguments[0]);
		MosBasicComponentLauncher.main (JettyComponentLauncher.class.getName ().replace ("Launcher", "Callbacks"), finalArguments, JettyComponentLauncher.class.getClassLoader ());
	}
}
