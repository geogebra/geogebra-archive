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
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class RightAssociative extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1);
        String orig = (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1);
        aEnvironment.iInfixOperators.setRightAssociative(Utility.getSymbolName(aEnvironment, orig));
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="RightAssociative",categories="Programmer Functions;Programming;Built In"
*CMD RightAssociative --- declare associativity
*CORE
*CALL
	RightAssociative("op")

*PARMS

{"op"} -- string, the name of a function

*DESC
This makes the operator right-associative. For example:
	RightAssociative("*")
would make multiplication right-associative. Take care not to abuse this
function, because the reverse, making an infix operator left-associative, is
not implemented. (All infix operators are by default left-associative until
they are declared to be right-associative.)

*SEE OpPrecedence, OpLeftPrecedence, OpRightPrecedence, LeftPrecedence, RightPrecedence
%/mathpiper_docs
*/