/*
 * #%L
 * mosaic-tools-callbacks
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

package eu.mosaic_cloud.tools.callbacks.implementations.basic.tests;


import eu.mosaic_cloud.tools.callbacks.tools.StateMachine;
import eu.mosaic_cloud.tools.callbacks.tools.StateMachine.AccessorOperation;
import eu.mosaic_cloud.tools.callbacks.tools.StateMachine.TransactionOperation;
import org.junit.Test;


public final class StateMachineTest
{
	@Test
	public final void test ()
	{
		final TestStateMachine machine = new TestStateMachine ();
		{
			machine.new Accessor ().begin ().release ();
		}
		{
			machine.new Accessor ().execute (new AccessorOperation<TestStateMachine.Accessor> () {
				@Override
				public final void execute (final TestStateMachine.Accessor accessor)
				{}
			});
		}
		{
			machine.new Transaction (TestStateMachine.Transition.Executing).begin ().commit (TestStateMachine.State.Ready);
		}
		{
			machine.new Transaction (TestStateMachine.Transition.Executing).execute (new TransactionOperation<TestStateMachine.State, TestStateMachine.Transaction> () {
				@Override
				public final TestStateMachine.State execute (final TestStateMachine.Transaction transaction)
				{
					return (TestStateMachine.State.Ready);
				}
			});
		}
		{
			machine.new Transaction (TestStateMachine.Transition.Executing).begin ().rollback ();
		}
		{
			machine.new Transaction (TestStateMachine.Transition.Executing).execute (new TransactionOperation<TestStateMachine.State, TestStateMachine.Transaction> () {
				@Override
				public final TestStateMachine.State execute (final TestStateMachine.Transaction transaction)
				{
					return (null);
				}
			});
		}
		/*{
			try (final TestStateMachine.Transaction transaction = machine.new Transaction ()) {
				transaction.commit (TestStateMachine.State.Created);
			}
		}*/
	}
	
	public static final class TestStateMachine
			extends StateMachine<TestStateMachine.State, TestStateMachine.Transition>
	{
		TestStateMachine ()
		{
			super (State.class, Transition.class);
			this.defineStates (TestStateMachine.State.class);
			this.defineTransition (Transition.Executing, State.Ready, State.Ready);
			this.initialize (State.Ready);
		}
		
		public enum State
				implements
					StateMachine.State
		{
			Ready ();
		}
		
		public enum Transition
				implements
					StateMachine.Transition
		{
			Executing ();
		}
	}
}
