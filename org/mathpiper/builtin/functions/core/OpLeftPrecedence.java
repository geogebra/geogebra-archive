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
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.InfixOperator;
import org.mathpiper.lisp.LispError;

/**
 *
 *  
 */
public class OpLeftPrecedence extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        InfixOperator op = Utility.operatorInfo(aEnvironment, aStackTop, aEnvironment.iInfixOperators);
        if (op == null)
        {  // infix and postfix operators have left precedence

            op = Utility.operatorInfo(aEnvironment, aStackTop, aEnvironment.iPostfixOperators);
            LispError.check(aEnvironment, aStackTop, op != null, LispError.IS_NOT_INFIX);
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, "" + op.iLeftPrecedence));
    }
}



/*
%mathpiper_docs,name="OpLeftPrecedence",categories="Programmer Functions;Programming;Built In"
*CMD OpLeftPrecedence --- get operator precedence
*CORE
*CALL
	OpLeftPrecedence("op")

*PARMS

{"op"} -- string, the name of a function

*DESC

Returns the precedence of the function named "op" which should have been declared as a bodied function or an infix, postfix, or prefix operator. Generates an error message if the string str does not represent a type of function that can have precedence.

For infix operators, right precedence can differ from left precedence. Bodied functions and prefix operators cannot have left precedence, while postfix operators cannot have right precedence; for these operators, there is only one value of precedence.

*E.G.
	In> OpLeftPrecedence("!")
	Out> 0;

*SEE OpPrecedence,OpRightPrecedence
%/mathpiper_docs
*/