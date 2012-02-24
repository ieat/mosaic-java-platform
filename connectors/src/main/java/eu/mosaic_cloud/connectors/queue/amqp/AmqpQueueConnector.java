
package eu.mosaic_cloud.connectors.queue.amqp;


import com.google.common.base.Preconditions;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


public class AmqpQueueConnector<P extends AmqpQueueConnectorProxy<?>>
		implements
			IAmqpQueueConnector
{
	protected AmqpQueueConnector (final P proxy)
	{
		super ();
		Preconditions.checkNotNull (proxy);
		this.proxy = proxy;
	}
	
	@Override
	public CallbackCompletion<Void> destroy ()
	{
		return this.proxy.destroy ();
	}
	
	@Override
	public CallbackCompletion<Void> initialize ()
	{
		return CallbackCompletion.createOutcome ();
	}
	
	protected final P proxy;
}
