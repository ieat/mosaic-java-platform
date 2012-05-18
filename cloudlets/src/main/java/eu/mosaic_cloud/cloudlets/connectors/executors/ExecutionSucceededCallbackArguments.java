/*
 * #%L
 * mosaic-cloudlets
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

package eu.mosaic_cloud.cloudlets.connectors.executors;


import eu.mosaic_cloud.cloudlets.core.CallbackArguments;
import eu.mosaic_cloud.cloudlets.core.ICloudletController;


public class ExecutionSucceededCallbackArguments<TOutcome, TExtra>
		extends CallbackArguments
{
	public ExecutionSucceededCallbackArguments (final ICloudletController<?> cloudlet, final TOutcome outcome, final TExtra extra)
	{
		super (cloudlet);
		this.outcome = outcome;
		this.extra = extra;
	}
	
	public TExtra getExtra ()
	{
		return (this.extra);
	}
	
	public TOutcome getOutcome ()
	{
		return (this.outcome);
	}
	
	private final TExtra extra;
	private final TOutcome outcome;
}