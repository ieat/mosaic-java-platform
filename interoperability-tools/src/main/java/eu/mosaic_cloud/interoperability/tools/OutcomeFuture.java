
package eu.mosaic_cloud.interoperability.tools;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public final class OutcomeFuture<_Outcome_ extends Object>
		extends FutureTask<_Outcome_>
{
	private OutcomeFuture (final OutcomeFuture.OutcomeCallable<_Outcome_> callable)
	{
		super (callable);
		this.trigger = new OutcomeTrigger<_Outcome_> (this);
		this.callable = callable;
	}
	
	public final OutcomeFuture.OutcomeTrigger<_Outcome_> trigger;
	private final OutcomeFuture.OutcomeCallable<_Outcome_> callable;
	
	public static final <_Outcome_ extends Object> OutcomeFuture<_Outcome_> create ()
	{
		return (new OutcomeFuture<_Outcome_> (new OutcomeFuture.OutcomeCallable<_Outcome_> ()));
	}
	
	public static final class OutcomeTrigger<_Outcome_ extends Object>
			extends Object
	{
		private OutcomeTrigger (final OutcomeFuture<_Outcome_> future)
		{
			super ();
			this.future = future;
		}
		
		public final void succeeded (final _Outcome_ outcome)
		{
			this.future.callable.outcome = outcome;
			this.future.run ();
		}
		
		private final OutcomeFuture<_Outcome_> future;
	}
	
	private static final class OutcomeCallable<_Outcome_ extends Object>
			extends Object
			implements
				Callable<_Outcome_>
	{
		private OutcomeCallable ()
		{
			super ();
			this.outcome = null;
		}
		
		@Override
		public final _Outcome_ call ()
		{
			return (this.outcome);
		}
		
		_Outcome_ outcome;
	}
}
