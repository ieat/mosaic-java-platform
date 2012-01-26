/*
 * #%L
 * mosaic-tools-callbacks
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

package eu.mosaic_cloud.tools.callbacks.core.v2;


import eu.mosaic_cloud.tools.threading.core.Joinable;


public interface CallbackReactor
		extends
			Joinable
{
	public abstract <_Callbacks_ extends Callbacks> CallbackReference assignHandler (final _Callbacks_ proxy, final CallbackIsolate isolate, final CallbackHandler<_Callbacks_> handler);
	
	public abstract CallbackIsolate createIsolate ();
	
	public abstract <_Callbacks_ extends Callbacks> _Callbacks_ createProxy (final Class<_Callbacks_> specification);
	
	public abstract CallbackReference destroyIsolate (final CallbackIsolate isolate);
	
	public abstract <_Callbacks_ extends Callbacks> CallbackReference destroyProxy (final _Callbacks_ proxy);
}