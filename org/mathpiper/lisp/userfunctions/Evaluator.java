/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.lisp.userfunctions;

// new-style evaluator, passing arguments onto the stack in Environment

import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.*;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.SubList;


public class Evaluator extends EvalFuncBase
{
	// FunctionFlags can be ORed when passed to the constructor of this function

	public static int Function=0;    // Function: evaluate arguments
	public static int Macro=1;       // Function: don't evaluate arguments
	public static int Fixed = 0;     // fixed number of arguments
	public static int Variable = 2;  // variable number of arguments
	
	BuiltinFunctionInitialize iCalledFunction;
	int iNumberOfArguments;
	int iFlags;

	public Evaluator(BuiltinFunctionInitialize aCalledFunction,int aNumberOfArguments, int aFlags)
	{
		iCalledFunction = aCalledFunction;
		iNumberOfArguments = aNumberOfArguments;
		iFlags = aFlags;
	}
	
	public void evaluate(ConsPointer aResult,Environment aEnvironment, ConsPointer aArguments) throws Exception
	{
		if ((iFlags & Variable) == 0)
		{
			LispError.checkNumberOfArguments(iNumberOfArguments+1,aArguments,aEnvironment);
		}

		int stacktop = aEnvironment.iArgumentStack.getStackTopIndex();

		// Push a place holder for the getResult: push full expression so it is available for error reporting
		aEnvironment.iArgumentStack.pushArgumentOnStack(aArguments.getCons());

		ConsTraverser iter = new ConsTraverser(aArguments);
		iter.goNext();

		int i;
		int numberOfArguments = iNumberOfArguments;

		if ((iFlags & Variable) != 0) numberOfArguments--;

		// Walk over all arguments, evaluating them as necessary
		if ((iFlags & Macro) != 0)
		{
			for (i=0;i<numberOfArguments;i++)
			{
				LispError.check(iter.getCons() != null, LispError.KLispErrWrongNumberOfArgs);
				aEnvironment.iArgumentStack.pushArgumentOnStack(iter.getCons().copy(false));
				iter.goNext();
			}
			if ((iFlags & Variable) != 0)
			{
				ConsPointer head = new ConsPointer();
				head.setCons(aEnvironment.iListAtom.copy(false));
				head.getCons().rest().setCons(iter.getCons());
				aEnvironment.iArgumentStack.pushArgumentOnStack(SubList.getInstance(head.getCons()));
			}
		}
		else
		{
			ConsPointer argument = new ConsPointer();
			for (i=0;i<numberOfArguments;i++)
			{
				LispError.check(iter.getCons() != null, LispError.KLispErrWrongNumberOfArgs);
				LispError.check(iter.ptr() != null, LispError.KLispErrWrongNumberOfArgs);
				aEnvironment.iEvaluator.evaluate(aEnvironment, argument, iter.ptr());
				aEnvironment.iArgumentStack.pushArgumentOnStack(argument.getCons());
				iter.goNext();
			}
			if ((iFlags & Variable) != 0)
			{

				//LispString res;

				//printf("Enter\n");
				ConsPointer head = new ConsPointer();
				head.setCons(aEnvironment.iListAtom.copy(false));
				head.getCons().rest().setCons(iter.getCons());
				ConsPointer list = new ConsPointer();
				list.setCons(SubList.getInstance(head.getCons()));


				/*
				PrintExpression(res, list,aEnvironment,100);
				printf("before %s\n",res.String());
				*/

				aEnvironment.iEvaluator.evaluate(aEnvironment, argument, list);

				/*
				PrintExpression(res, arg,aEnvironment,100);
				printf("after %s\n",res.String());
				*/

				aEnvironment.iArgumentStack.pushArgumentOnStack(argument.getCons());
				//printf("Leave\n");
			}
		}

		iCalledFunction.eval(aEnvironment,stacktop);
		aResult.setCons(aEnvironment.iArgumentStack.getElement(stacktop).getCons());
		aEnvironment.iArgumentStack.popTo(stacktop);
	}

}


