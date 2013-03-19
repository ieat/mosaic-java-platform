/*
 * #%L
 * mosaic-components-core
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

package eu.mosaic_cloud.components.core;


import com.google.common.base.Preconditions;


public final class ComponentAcquireRequest
		extends ComponentMessage
{
	private ComponentAcquireRequest (final ComponentResourceSpecification specification, final ComponentCallReference reference)
	{
		super ();
		Preconditions.checkNotNull (specification);
		Preconditions.checkNotNull (reference);
		this.specification = specification;
		this.reference = reference;
	}
	
	public static final ComponentAcquireRequest create (final ComponentResourceSpecification specification, final ComponentCallReference reference)
	{
		return (new ComponentAcquireRequest (specification, reference));
	}
	
	public final ComponentCallReference reference;
	public final ComponentResourceSpecification specification;
}
