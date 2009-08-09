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
package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Check extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer pred = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, pred, getArgumentPointer(aEnvironment, aStackTop, 1));
        if (!Utility.isTrue(aEnvironment, pred))
        {
            ConsPointer evaluated = new ConsPointer();
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, evaluated, getArgumentPointer(aEnvironment, aStackTop, 2));
            LispError.checkIsString(aEnvironment, aStackTop, evaluated, 2);
            throw new Exception( (String) evaluated.car());
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(pred.getCons());
    }
}



/*
%mathpiper_docs,name="Check",categories="Programmer Functions;Error Reporting;Built In"
*CMD Check --- report "hard" errors
*CORE
*CALL
	Check(predicate,"error text")

*PARMS

{predicate} -- expression returning {True} or {False}

{"error text"} -- string to print on error

*DESC
If {predicate} does not evaluate to {True},
the current operation will be stopped, the string {"error text"} will be printed, and control will be returned immediately to the command line. This facility can be used to assure that some condition
is satisfied during evaluation of expressions (guarding
against critical internal errors).

A "soft" error reporting facility that does not stop the execution is provided by the function {Assert}.

*EG

	In> [Check(1=0,"bad value"); Echo(OK);]
	In function "Check" :
	CommandLine(1) : "bad value"

Note that {OK} is not printed.

*SEE Assert, TrapError, GetCoreError

%/mathpiper_docs
*/