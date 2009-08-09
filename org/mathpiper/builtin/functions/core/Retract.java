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
public class Retract extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get operator
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1);
        String orig = (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1);
        String oper = Utility.getSymbolName(aEnvironment, orig);

        ConsPointer arityPointer = new ConsPointer();
        arityPointer.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, arityPointer.car() instanceof String, 2);
        String arityString = (String) arityPointer.car();
        if(arityString.equalsIgnoreCase("*"))
        {
            aEnvironment.retractFunction(oper, -1);
        }
        else
        {
            int arity = Integer.parseInt(arityString, 10);
            aEnvironment.retractFunction(oper, arity);
        }
  
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="Retract",categories="User Functions;Built In"
*CMD Retract --- erase rules for a function
*CORE
*CALL
	Retract("function",arity)

*PARMS
{"function"} -- string, name of function

{arity} -- positive integer

*DESC

Remove a rulebase for the function named {"function"} with the specific {arity}, if it exists at all. This will make
MathPiper forget all rules defined for a given function. Rules for functions with
the same name but different arities are not affected.

Assignment {:=} of a function does this to the function being (re)defined.

*SEE RuleBaseArgList, RuleBase, :=
%/mathpiper_docs
*/