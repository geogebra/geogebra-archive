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
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class MacroSet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.setVar(aEnvironment, aStackTop, true, false);
    }
}



/*
%mathpiper_docs,name="MacroSet",categories="Programmer Functions;Programming;Built In"
*CMD MacroSet --- define rules in functions
*CORE
*DESC

This function has the same effect as its non-macro counterpart, except
that its arguments are evaluated before the required action is performed.
This is useful in macro-like procedures or in functions that need to define new
rules based on parameters.

Make sure that the arguments of {Macro}... commands evaluate to expressions that would normally be used in the non-macro version!

*SEE Set, Clear, Local, RuleBase, Rule, Backquoting, MacroClear, MacroLocal, MacroRuleBase, MacroRuleBaseListed, MacroRule
%/mathpiper_docs
*/