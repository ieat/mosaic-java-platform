/*
 * #%L
 * mosaic-drivers-stubs-riak
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

package eu.mosaic_cloud.drivers.kvstore.tests;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import eu.mosaic_cloud.drivers.kvstore.RiakPBDriver;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.platform.core.ops.IResult;
import eu.mosaic_cloud.platform.core.tests.TestLoggingHandler;
import eu.mosaic_cloud.platform.core.utils.SerDesUtils;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import eu.mosaic_cloud.tools.threading.tools.Threading;
import eu.mosaic_cloud.tools.transcript.core.Transcript;
import eu.mosaic_cloud.tools.transcript.tools.TranscriptExceptionTracer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RiakPBDriverTest {

    private static final String MOSAIC_RIAK_HOST = "mosaic.tests.resources.riak.host";
    private static final String MOSAIC_RIAK_HOST_DEFAULT = "127.0.0.1";
    private static final String MOSAIC_RIAK_PORT = "mosaic.tests.resources.riakpb.port";
    private static final String MOSAIC_RIAK_PORT_DEFAULT = "22652";
    private RiakPBDriver wrapper;
    private BasicThreadingContext threadingContext;
    private static String keyPrefix;

    @BeforeClass
    public static void setUpBeforeClass() {
        RiakPBDriverTest.keyPrefix = UUID.randomUUID().toString();
    }

    @Before
    public void setUp() throws Exception {
        final Transcript transcript = Transcript.create(this);
        final QueueingExceptionTracer exceptionsQueue = QueueingExceptionTracer
                .create(NullExceptionTracer.defaultInstance);
        final TranscriptExceptionTracer exceptions = TranscriptExceptionTracer.create(transcript,
                exceptionsQueue);
        BasicThreadingSecurityManager.initialize();
        this.threadingContext = BasicThreadingContext.create(this, exceptions, exceptions.catcher);
        this.threadingContext.initialize();

        final String host = System.getProperty(RiakPBDriverTest.MOSAIC_RIAK_HOST,
                RiakPBDriverTest.MOSAIC_RIAK_HOST_DEFAULT);
        final Integer port = Integer.valueOf(System.getProperty(RiakPBDriverTest.MOSAIC_RIAK_PORT,
                RiakPBDriverTest.MOSAIC_RIAK_PORT_DEFAULT));

        final IConfiguration configuration = PropertyTypeConfiguration.create();
        configuration.addParameter("kvstore.host", host);
        configuration.addParameter("kvstore.port", port);
        configuration.addParameter("kvstore.driver_name", "RIAKPB");
        configuration.addParameter("kvstore.driver_threads", 1);
        configuration.addParameter("kvstore.bucket", "tests");

        this.wrapper = RiakPBDriver.create(configuration, this.threadingContext);
        this.wrapper.registerClient(RiakPBDriverTest.keyPrefix, "test");
    }

    @After
    public void tearDown() {
        this.wrapper.unregisterClient(RiakPBDriverTest.keyPrefix);
        this.wrapper.destroy();
        this.threadingContext.destroy();
    }

    public void testConnection() {
        Assert.assertNotNull(this.wrapper);
    }

    public void testDelete() {
        final String k1 = RiakPBDriverTest.keyPrefix + "_key_fantastic";
        final IOperationCompletionHandler<Boolean> handler1 = new TestLoggingHandler<Boolean>(
                "delete 1");
        final IResult<Boolean> r1 = this.wrapper.invokeDeleteOperation(RiakPBDriverTest.keyPrefix,
                k1, handler1);
        try {
            Assert.assertTrue(r1.getResult());
        } catch (final InterruptedException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        } catch (final ExecutionException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        }
        Threading.sleep(1000);
        final IOperationCompletionHandler<byte[]> handler3 = new TestLoggingHandler<byte[]>(
                "check deleted");
        final IResult<byte[]> r3 = this.wrapper.invokeGetOperation(RiakPBDriverTest.keyPrefix, k1,
                handler3);
        try {
            Assert.assertNull(r3.getResult());
        } catch (final InterruptedException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        } catch (final ExecutionException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        }
    }

    @Test
    public void testDriver() throws IOException, ClassNotFoundException {
        testConnection();
        testSet();
        testGet();
        testList();
        testDelete();
    }

    public void testGet() throws IOException, ClassNotFoundException {
        final String k1 = RiakPBDriverTest.keyPrefix + "_key_famous";
        final IOperationCompletionHandler<byte[]> handler = new TestLoggingHandler<byte[]>("get");
        final IResult<byte[]> r1 = this.wrapper.invokeGetOperation(RiakPBDriverTest.keyPrefix, k1,
                handler);
        try {
            Assert.assertEquals("famous", SerDesUtils.toObject(r1.getResult()).toString());
        } catch (final InterruptedException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        } catch (final ExecutionException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        }
    }

    public void testList() {
        final String k1 = RiakPBDriverTest.keyPrefix + "_key_fantastic";
        final String k2 = RiakPBDriverTest.keyPrefix + "_key_famous";
        final IOperationCompletionHandler<List<String>> handler = new TestLoggingHandler<List<String>>(
                "list");
        final IResult<List<String>> r1 = this.wrapper.invokeListOperation(
                RiakPBDriverTest.keyPrefix, handler);
        try {
            final List<String> lresult = r1.getResult();
            Assert.assertNotNull(lresult);
            Assert.assertTrue(lresult.contains(k1));
            Assert.assertTrue(lresult.contains(k2));
        } catch (final InterruptedException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        } catch (final ExecutionException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        }
    }

    public void testSet() throws IOException {
        final String k1 = RiakPBDriverTest.keyPrefix + "_key_fantastic";
        final byte[] b1 = SerDesUtils.pojoToBytes("fantastic");
        final IOperationCompletionHandler<Boolean> handler1 = new TestLoggingHandler<Boolean>(
                "set 1");
        final IResult<Boolean> r1 = this.wrapper.invokeSetOperation(RiakPBDriverTest.keyPrefix, k1,
                b1, handler1);
        Assert.assertNotNull(r1);
        final String k2 = RiakPBDriverTest.keyPrefix + "_key_famous";
        final byte[] b2 = SerDesUtils.pojoToBytes("famous");
        final IOperationCompletionHandler<Boolean> handler2 = new TestLoggingHandler<Boolean>(
                "set 2");
        final IResult<Boolean> r2 = this.wrapper.invokeSetOperation(RiakPBDriverTest.keyPrefix, k2,
                b2, handler2);
        Assert.assertNotNull(r2);
        try {
            Assert.assertTrue(r1.getResult());
            Assert.assertTrue(r2.getResult());
        } catch (final InterruptedException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        } catch (final ExecutionException e) {
            ExceptionTracer.traceIgnored(e);
            Assert.fail();
        }
    }
}