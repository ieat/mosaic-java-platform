/*
 * #%L
 * mosaic-tools-miscellaneous
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
// $codepro.audit.disable overridingSynchronizedMethod

package eu.mosaic_cloud.tools.miscellaneous;


import java.io.InputStream;


public final class BrokenInputStream
			extends InputStream
{
	private BrokenInputStream () {
		super ();
	}
	
	@Override
	public final int available () {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final void close () {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final void mark (final int readlimit) {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final boolean markSupported () {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final int read () {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final int read (final byte[] b) {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final int read (final byte[] b, final int off, final int len) {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final void reset () {
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final long skip (final long n) {
		throw (new UnsupportedOperationException ());
	}
	
	public static final BrokenInputStream create () {
		return (new BrokenInputStream ());
	}
	
	public static final BrokenInputStream defaultInstance = BrokenInputStream.create ();
}
