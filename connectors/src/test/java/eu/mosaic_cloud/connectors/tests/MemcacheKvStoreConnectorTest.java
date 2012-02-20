/*
 * #%L
 * mosaic-connectors
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

package eu.mosaic_cloud.connectors.tests;


import java.util.Arrays;
import java.util.Map;

import eu.mosaic_cloud.connectors.kvstore.memcache.MemcacheKvStoreConnector;
import eu.mosaic_cloud.drivers.interop.kvstore.memcached.MemcachedStub;
import eu.mosaic_cloud.platform.core.utils.PojoDataEncoder;
import eu.mosaic_cloud.platform.interop.kvstore.KeyValueSession;
import eu.mosaic_cloud.platform.interop.kvstore.MemcachedSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class MemcacheKvStoreConnectorTest
		extends BaseKvStoreConnectorTest<MemcacheKvStoreConnector<String>>
{
	@Override
	public void setUp ()
	{
		this.context = MemcacheKvStoreConnectorTest.context_;
		this.connector = MemcacheKvStoreConnector.create (this.context.configuration, new PojoDataEncoder<String> (String.class), this.context.threading);
	}
	
	@Override
	@Test
	public void test ()
	{
		this.testConnector ();
		this.testSet ();
		this.testGet ();
		this.testGetBulk ();
		this.testAdd ();
		this.testReplace ();
		this.testCas ();
		this.testList ();
		this.testDelete ();
	}
	
	protected void testAdd ()
	{
		final String k1 = this.context.keyPrefix + "_key_fantastic";
		final String k2 = this.context.keyPrefix + "_key_fabulous";
		Assert.assertFalse (this.awaitBooleanOutcome (this.connector.add (k1, 30, "wrong")));
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.add (k2, 30, "fabulous")));
	}
	
	protected void testAppend ()
	{
		final String k1 = this.context.keyPrefix + "_key_fabulous";
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.append (k1, " and miraculous")));
		Assert.assertEquals ("fantabulous and miraculous", this.awaitOutcome (this.connector.get (k1)));
	}
	
	protected void testCas ()
	{
		final String k1 = this.context.keyPrefix + "_key_fabulous";
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.cas (k1, "replaced by dummy")));
		Assert.assertEquals ("replaced by dummy", this.awaitOutcome (this.connector.get (k1)));
	}
	
	protected void testGetBulk ()
	{
		final String k1 = this.context.keyPrefix + "_key_fantastic";
		final String k2 = this.context.keyPrefix + "_key_famous";
		final Map<String, String> values = this.awaitOutcome (this.connector.getBulk (Arrays.asList (k1, k2)));
		Assert.assertNotNull (values);
		Assert.assertEquals ("fantastic", values.get (k1));
		Assert.assertEquals ("famous", values.get (k2));
	}
	
	@Override
	protected void testList ()
	{
		Assert.assertNull (this.awaitOutcome (this.connector.list ()));
	}
	
	protected void testPrepend ()
	{
		final String k1 = this.context.keyPrefix + "_key_fabulous";
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.prepend (k1, "it is ")));
		Assert.assertEquals ("it is fantabulous and miraculous", this.awaitOutcome (this.connector.get (k1)));
	}
	
	protected void testReplace ()
	{
		final String k1 = this.context.keyPrefix + "_key_fabulous";
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.replace (k1, 30, "fantabulous")));
		Assert.assertEquals ("fantabulous", this.awaitOutcome (this.connector.get (k1)));
	}
	
	@Override
	protected void testSet ()
	{
		final String k1 = this.context.keyPrefix + "_key_fantastic";
		final String k2 = this.context.keyPrefix + "_key_famous";
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.set (k1, 30, "fantastic")));
		Assert.assertTrue (this.awaitBooleanOutcome (this.connector.set (k2, 30, "famous")));
	}
	
	@BeforeClass
	public static void setUpBeforeClass ()
	{
		final Context context = new Context ();
		BaseConnectorTest.setupUpContext (MemcacheKvStoreConnectorTest.class, context, "memcache-kv-store-connector-test.prop");
		context.driverChannel.register (KeyValueSession.DRIVER);
		context.driverChannel.register (MemcachedSession.DRIVER);
		context.driverStub = MemcachedStub.create (context.configuration, context.driverChannel, context.threading);
		MemcacheKvStoreConnectorTest.context_ = context;
	}
	
	@AfterClass
	public static void tearDownAfterClass ()
	{
		BaseConnectorTest.tearDownContext (MemcacheKvStoreConnectorTest.context_);
	}
	
	private static Context context_;
}