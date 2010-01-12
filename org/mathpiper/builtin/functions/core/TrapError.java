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

/**
 *
 *  
 */
public class TrapError extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        try
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
        } catch (Exception e)
        {
            //e.printStackTrace();
            aEnvironment.iError ="Caught in TrapError function: " + e.toString();
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
            aEnvironment.iError = null;
        }
    }
}



/*
%mathpiper_docs,name="TrapError",categories="Programmer Functions;Error Reporting;Built In"
*CMD TrapError --- trap "hard" errors
*CORE
*CALL
	TrapError(expression,errorHandler)

*PARMS

{expression} -- expression to evaluate (causing potential error)

{errorHandler} -- expression to be called to handle error

*DESC
TrapError evaluates its argument {expression}, returning the
result of evaluating {expression}. If an error occurs,
{errorHandler} is evaluated, returning its return value in stead.

**E.G.

	In>


*SEE Assert, Check, GetCoreError

%/mathpiper_docs
*/