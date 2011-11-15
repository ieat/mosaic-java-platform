/*
 * #%L
 * mosaic-cloudlet
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
package mosaic.cloudlet.core;

import mosaic.core.configuration.IConfiguration;

/**
 * Interface defining the contract of the internal representation of a cloudlet.
 * 
 * @author Georgiana Macariu
 * 
 */
public interface ICloudlet {

	/**
	 * Initializes the cloudlet.
	 * 
	 * @param configData
	 *            configuration data of the cloudlet
	 * @return <code>true</code> if cloudlet was successfully initialized
	 */
	boolean initialize(IConfiguration configData);

	/**
	 * Destroys the cloudlet.
	 * 
	 * @return <code>true</code> if cloudlet was successfully destroyed
	 */
	boolean destroy();

	/**
	 * Indicates if the cloudlet is alive and can receive messages or not.
	 * 
	 * @return <code>true</code> if cloudlet is alive
	 */
	boolean isActive();

}