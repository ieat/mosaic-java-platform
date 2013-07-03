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

package eu.mosaic_cloud.cloudlets.v1.connectors.httpg;


import eu.mosaic_cloud.cloudlets.v1.connectors.queue.IQueueConnectorCallback;
import eu.mosaic_cloud.cloudlets.v1.core.GenericCallbackCompletionArguments;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


public interface IHttpgQueueConnectorCallback<TContext, TRequestBody, TResponseBody, TExtra>
			extends
				IQueueConnectorCallback<TContext>
{
	CallbackCompletion<Void> requested (TContext context, HttpgQueueRequestedCallbackArguments<TRequestBody> arguments);
	
	CallbackCompletion<Void> respondFailed (TContext context, GenericCallbackCompletionArguments<TExtra> arguments);
	
	CallbackCompletion<Void> respondSucceeded (TContext context, GenericCallbackCompletionArguments<TExtra> arguments);
}
