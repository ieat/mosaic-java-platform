package mosaic.core.ops;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Defines a generic result handle of asynchronous operation. The asynchronous
 * operation is implemented using Java Futures in this case. It implements the
 * {@link IResult} interface.
 * <p>
 * If you would like to use a GenericResult for the sake of managing
 * asynchronous operation but not provide a usable result, you can declare types
 * of the form GenericResult<?> and return null as a result of the underlying
 * operation.
 * <p>
 * For working with {@link GenericOperation} types, you should either use this
 * class or extend it instead of implementing another one directly from
 * {@link IResult}.
 * 
 * @author Georgiana Macariu
 * 
 * @param <T>
 *            The type of the actual result of the asynchronous operation.
 */
public class GenericResult<T> implements IResult<T> {

	private final GenericOperation<T> operation;

	public GenericResult(final GenericOperation<T> operation) {
		this.operation = operation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mosaic.core.IResult#getResult()
	 */
	@Override
	public T getResult() throws InterruptedException, ExecutionException {
		return this.operation.get();
	}

	@Override
	public T getResult(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.operation.get (timeout, unit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mosaic.core.ops.IResult#isDone()
	 */
	@Override
	public final boolean isDone() {
		return this.operation.isDone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mosaic.core.ops.IResult#cancel()
	 */
	@Override
	public final boolean cancel() {
		boolean done;

		// first test if it was not already cancelled
		done = this.operation.isCancelled();

		if (!done) {
			// try to cancel the operation
			done = this.operation.cancel();

			// cancellation may have failed if the operation was
			// already finished
			if (!done) {
				done = this.operation.isDone();
			}
		}
		return done;
	}
}
