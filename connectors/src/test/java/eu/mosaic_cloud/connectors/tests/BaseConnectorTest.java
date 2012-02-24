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

import eu.mosaic_cloud.connectors.core.IConnector;
import eu.mosaic_cloud.drivers.interop.AbstractDriverStub;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.log.MosaicLogger;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.exceptions.tools.AbortingExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;
import eu.mosaic_cloud.tools.transcript.core.Transcript;
import eu.mosaic_cloud.tools.transcript.tools.TranscriptExceptionTracer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseConnectorTest<Connector extends IConnector, Context_ extends BaseConnectorTest.Context<?>> {

    protected static class Context<DriverStub extends AbstractDriverStub> {

        IConfiguration configuration;

        ZeroMqChannel driverChannel;

        DriverStub driverStub;

        TranscriptExceptionTracer exceptions;

        QueueingExceptionTracer exceptions_;

        MosaicLogger logger;

        long poolTimeout = 1000 * 1000;

        BasicThreadingContext threading;

        Transcript transcript;
    }

    protected Connector connector;

    protected Context_ context;

    protected static <C extends Context<?>> void setupUpContext(
            final Class<? extends BaseConnectorTest<?, C>> owner, final C context,
            final String configuration) {
        BasicThreadingSecurityManager.initialize();
        context.logger = MosaicLogger.createLogger(owner);
        context.transcript = Transcript.create(owner);
        context.exceptions_ = QueueingExceptionTracer.create(NullExceptionTracer.defaultInstance);
        context.exceptions = TranscriptExceptionTracer.create(context.transcript,
                context.exceptions_);
        context.configuration = PropertyTypeConfiguration.create(owner.getClassLoader(),
                configuration);
        context.threading = BasicThreadingContext.create(MemcacheKvStoreConnectorTest.class,
                context.exceptions.catcher);
        context.threading.initialize();
        final String driverIdentity = ConfigUtils.resolveParameter(context.configuration,
                "interop.driver.identifier", String.class, "");
        final String driverEndpoint = ConfigUtils.resolveParameter(context.configuration,
                "interop.channel.address", String.class, "");
        context.driverChannel = ZeroMqChannel.create(driverIdentity, context.threading,
                AbortingExceptionTracer.defaultInstance);
        context.driverChannel.accept(driverEndpoint);
    }

    protected static void tearDownContext(final Context<?> context) {
        if (context.driverStub != null) {
            context.driverStub.destroy();
        }
        Assert.assertTrue(context.driverChannel.terminate(context.poolTimeout));
        Assert.assertTrue(context.threading.destroy(context.poolTimeout));
    }

    protected void await(final CallbackCompletion<?> completion) {
        Assert.assertTrue(completion.await(this.context.poolTimeout));
    }

    protected boolean awaitBooleanOutcome(final CallbackCompletion<Boolean> completion) {
        this.await(completion);
        return this.getBooleanOutcome(completion);
    }

    protected <O> O awaitOutcome(final CallbackCompletion<O> completion) {
        this.await(completion);
        return this.getOutcome(completion);
    }

    protected boolean awaitSuccess(final CallbackCompletion<?> completion) {
        this.await(completion);
        Assert.assertTrue(completion.isCompleted());
        Assert.assertEquals(null, completion.getException());
        return true;
    }

    protected boolean getBooleanOutcome(final CallbackCompletion<Boolean> completion) {
        final Boolean value = this.getOutcome(completion);
        Assert.assertNotNull(value);
        return value.booleanValue();
    }

    protected <O> O getOutcome(final CallbackCompletion<O> completion) {
        Assert.assertTrue(completion.isCompleted());
        Assert.assertEquals(null, completion.getException());
        return completion.getOutcome();
    }

    @Before
    public abstract void setUp();

    @After
    public void tearDown() {
        this.await(this.connector.destroy());
        this.context = null;
    }

    @Test
    public abstract void test();

    protected void testConnector() {
        Assert.assertNotNull(this.connector);
    }
}
