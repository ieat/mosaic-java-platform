/*
 * #%L
 * mosaic-platform-core
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

package eu.mosaic_cloud.platform.v2.serialization;


/**
 * Interface for defining data specific encoders (serializers) and decode (deserializers).
 * 
 * @param <TData>
 *            the type of data to encode and decode
 * @author Georgiana Macariu
 */
public interface DataEncoder<TData extends Object>
{
	/**
	 * Decodes (deserializes) the data.
	 * 
	 * @param data
	 *            data bytes
	 * @return the decoded object
	 */
	TData decode (byte[] data, EncodingMetadata metadata)
				throws EncodingException;
	
	/**
	 * Encodes (serializes) an object as a stream of bytes.
	 * 
	 * @param data
	 *            the data to serialize
	 * @param metadata
	 *            the desired metadata, but if null use the default
	 * @return the bytes and the actual metadata which must match the input one if set
	 */
	EncodeOutcome encode (TData data, EncodingMetadata metadata)
				throws EncodingException;
	
	/**
	 * Returns encoding metadata, such as message content type.
	 * 
	 * @return encoding metadata
	 */
	EncodingMetadata getExpectedEncodingMetadata ();
	
	public static final class EncodeOutcome
	{
		public EncodeOutcome (final byte[] data, final EncodingMetadata metadata) {
			this.data = data;
			this.metadata = metadata;
		}
		
		public final byte[] data;
		public final EncodingMetadata metadata;
	}
}
