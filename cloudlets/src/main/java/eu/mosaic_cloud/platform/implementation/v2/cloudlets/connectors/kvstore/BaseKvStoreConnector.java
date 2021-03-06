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

package eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.kvstore;


import eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.core.BaseConnector;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnector;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.DeleteFailedArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.DeleteSucceededArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.GetFailedArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.GetSucceededArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.SetFailedArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.kvstore.KvStoreConnectorCallback.SetSucceededArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.core.CloudletController;
import eu.mosaic_cloud.platform.v2.configuration.Configuration;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletionObserver;


public abstract class BaseKvStoreConnector<TConnector extends eu.mosaic_cloud.platform.v2.connectors.kvstore.KvStoreConnector<TValue>, TCallback extends KvStoreConnectorCallback<TContext, TValue, TExtra>, TContext, TValue, TExtra>
			extends BaseConnector<TConnector, TCallback, TContext>
			implements
				KvStoreConnector<TValue, TExtra>
{
	protected BaseKvStoreConnector (final CloudletController<?> cloudlet, final TConnector connector, final Configuration config, final TCallback callback, final TContext context) {
		super (cloudlet, connector, config, callback, context);
	}
	
	@Override
	public CallbackCompletion<Void> delete (final String key) {
		return this.delete (key, null);
	}
	
	@Override
	public CallbackCompletion<Void> delete (final String key, final TExtra extra) {
		this.transcript.traceDebugging ("deleting the record with key `%s` and extra `%{object}`...", key, extra);
		final CallbackCompletion<Void> completion = this.connector.delete (key);
		if (this.callback != null) {
			completion.observe (new CallbackCompletionObserver () {
				@SuppressWarnings ("synthetic-access")
				@Override
				public CallbackCompletion<Void> completed (final CallbackCompletion<?> completion_) {
					assert (completion_ == completion);
					if (completion.getException () != null) {
						BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for delete failure for key `%s` and extra `%{object}`...", key, extra);
						return BaseKvStoreConnector.this.callback.deleteFailed (BaseKvStoreConnector.this.context, new DeleteFailedArguments<TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, completion.getException (), extra));
					}
					BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for delete success for key `%s` and extra `%{object}`...", key, extra);
					return BaseKvStoreConnector.this.callback.deleteSucceeded (BaseKvStoreConnector.this.context, new DeleteSucceededArguments<TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, extra));
				}
			});
		}
		return completion;
	}
	
	@Override
	public CallbackCompletion<TValue> get (final String key) {
		return this.get (key, null);
	}
	
	@Override
	public CallbackCompletion<TValue> get (final String key, final TExtra extra) {
		this.transcript.traceDebugging ("getting the record with key `%s` and extra `%{object}`...", key, extra);
		final CallbackCompletion<TValue> completion = this.connector.get (key);
		if (this.callback != null) {
			completion.observe (new CallbackCompletionObserver () {
				@SuppressWarnings ("synthetic-access")
				@Override
				public CallbackCompletion<Void> completed (final CallbackCompletion<?> completion_) {
					assert (completion_ == completion);
					if (completion.getException () != null) {
						BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for get failure for key `%s` and extra `%{object}`...", key, extra);
						return BaseKvStoreConnector.this.callback.getFailed (BaseKvStoreConnector.this.context, new GetFailedArguments<TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, completion.getException (), extra));
					}
					BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for get success for key `%s` and extra `%{object}`...", key, extra);
					return BaseKvStoreConnector.this.callback.getSucceeded (BaseKvStoreConnector.this.context, new GetSucceededArguments<TValue, TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, completion.getOutcome (), extra));
				}
			});
		}
		return completion;
	}
	
	@Override
	public CallbackCompletion<Void> set (final String key, final TValue value) {
		return this.set (key, value, null);
	}
	
	@Override
	public CallbackCompletion<Void> set (final String key, final TValue value, final TExtra extra) {
		this.transcript.traceDebugging ("setting the record with key `%s` and extra `%{object}`...", key, extra);
		final CallbackCompletion<Void> completion = this.connector.set (key, value);
		if (this.callback != null) {
			completion.observe (new CallbackCompletionObserver () {
				@SuppressWarnings ("synthetic-access")
				@Override
				public CallbackCompletion<Void> completed (final CallbackCompletion<?> completion_) {
					assert (completion_ == completion);
					if (completion.getException () != null) {
						BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for set failure for key `%s` and extra `%{object}`...", key, extra);
						return BaseKvStoreConnector.this.callback.setFailed (BaseKvStoreConnector.this.context, new SetFailedArguments<TValue, TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, value, completion.getException (), extra));
					}
					BaseKvStoreConnector.this.transcript.traceDebugging ("triggering the callback for set success for key `%s` and extra `%{object}`...", key, extra);
					return BaseKvStoreConnector.this.callback.setSucceeded (BaseKvStoreConnector.this.context, new SetSucceededArguments<TValue, TExtra> (BaseKvStoreConnector.this.cloudlet, BaseKvStoreConnector.this, key, value, extra));
				}
			});
		}
		return completion;
	}
}
