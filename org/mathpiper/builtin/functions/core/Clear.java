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

import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Clear extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        if (getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer) {

            ConsPointer subList = (ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car();
            
            ConsTraverser consTraverser = new ConsTraverser(subList);
            consTraverser.goNext();
            int nr = 1;
            while (consTraverser.getCons() != null)
            {
                String variableName;
                variableName =  (String) consTraverser.car();
                LispError.checkArgument(aEnvironment, aStackTop, variableName != null, nr);
                aEnvironment.unbindVariable(variableName);
                consTraverser.goNext();
                nr++;
            }
        }
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="Clear",categories="User Functions;Variables;Built In"
*CMD Clear --- undo an assignment
*CORE
*CALL
	Clear(var, ...)

*PARMS

{var} -- name of variable to be cleared

*DESC

All assignments made to the variables listed as arguments are
undone. From now on, all these variables remain unevaluated (until a
subsequent assignment is made). Also clears any metadata that may have
been set in an unbound variable.  The result of the expression is
True.

*E.G.

	In> a := 5;
	Out> 5;
	In> a^2;
	Out> 25;

	In> Clear(a);
	Out> True;
	In> a^2;
	Out> a^2;

*SEE Set, :=
%/mathpiper_docs
*/



/*
%mathpiper_docs,name="MacroClear",categories="Programmer Functions;Programming;Built In"
*CMD MacroClear --- define rules in functions
*CORE
*DESC

This function has the same effect as its non-macro counterpart, except
that its arguments are evaluated before the required action is performed.
This is useful in macro-like procedures or in functions that need to define new
rules based on parameters.

Make sure that the arguments of {Macro}... commands evaluate to expressions that would normally be used in the non-macro version!

*SEE Set, Clear, Local, RuleBase, Rule, Backquoting, MacroSet, MacroLocal, MacroRuleBase, MacroRuleBaseListed, MacroRule
%/mathpiper_docs
*/