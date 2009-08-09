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
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.printers.MathPiperPrinter;

/**
 *
 *  
 */
public class GetTime extends BuiltinFunction
{

     private GetTime()
    {
    }

    public GetTime(Environment aEnvironment)
    {
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "GetTime");
    }//end constructor.

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        long starttime = System.nanoTime();
        ConsPointer res = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, res, getArgumentPointer(aEnvironment, aStackTop, 1));
        long endtime = System.nanoTime();
        double timeDiff;
        timeDiff = endtime - starttime;
        timeDiff /= 1000000000.0;
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, "" + timeDiff));
    }
}



/*
%mathpiper_docs,name="GetTime",categories="Programmer Functions;Built In"
*CMD GetTime --- measure the time taken by an evaluation
*CORE
*CALL
	GetTime() expr
*PARMS
{expr} -- any expression
*DESC
The function {GetTime() expr} evaluates the expression {expr} and returns the time needed for the evaluation.
The result is returned as a floating-point number of seconds.
The value of the expression {expr} is lost.

The result is the "user time" as reported by the OS, not the real ("wall clock") time.
Therefore, any CPU-intensive processes running alongside Yacas will not significantly affect the result of {GetTime}.

*EG
	In> GetTime() Simplify((a*b)/(b*a))
	Out> 0.09;

*SEE Time

%/mathpiper_docs
*/