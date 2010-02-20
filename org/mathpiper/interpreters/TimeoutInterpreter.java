
package org.mathpiper.interpreters;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 *
 */
class TimeoutInterpreter extends AsynchronousInterpreter
{

	private int timeout;

	TimeoutInterpreter(SynchronousInterpreter s, int timeout) {
		super(s);
		this.timeout = timeout;
	}

	public EvaluationResponse evaluate(String expression, boolean notifyEvaluationListeners)
	{
		FutureTask<EvaluationResponse> task = new EvaluationTask(new Evaluator(expression, notifyEvaluationListeners));
		Thread thd = new Thread(task);
		thd.start();
		EvaluationResponse response = EvaluationResponse.newInstance();
		try
		{
			response = task.get(timeout, TimeUnit.MILLISECONDS); // timeout in ms
		}
		catch (ExecutionException e)
		{
			response.setExceptionMessage("MathPiper: ExecutionException");
		}
		catch (InterruptedException e)
		{
			response.setExceptionMessage("MathPiper: InterruptedException");
		}
		catch (TimeoutException e)
		{
			response.setExceptionMessage("MathPiper: TimeoutException");
		}


		return response;


	}//end method.

	public static Interpreter getInstance(int i) {
		if (singletonInstance == null)
		{
			SynchronousInterpreter interpreter = SynchronousInterpreter.getInstance();
			singletonInstance = new TimeoutInterpreter(interpreter, i);
		}
		return singletonInstance;
	}

}//end class.
