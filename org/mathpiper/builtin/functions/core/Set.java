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
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Set extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.setVar(aEnvironment, aStackTop, false, false);
    }
}



/*
%mathpiper_docs,name="Set",categories="User Functions;Variables;Built In"
*CMD Set --- assignment
*CORE
*CALL
	Set(var, exp)

*PARMS

{var} -- variable which should be assigned

{exp} -- expression to assign to the variable

*DESC

The expression "exp" is evaluated and assigned it to the variable
named "var". The first argument is not evaluated. The value True
is returned.

The statement {Set(var, exp)} is equivalent to {var := exp}, but the {:=} operator
has more uses, e.g. changing individual entries in a list.

*E.G.

	In> Set(a, Sin(x)+3);
	Out> True;
	In> a;
	Out> Sin(x)+3;

*SEE Clear, :=
%/mathpiper_docs
*/