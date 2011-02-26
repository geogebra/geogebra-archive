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
public class RulePattern extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        org.mathpiper.lisp.Utility.newRule(aEnvironment, aStackTop, true);
    }
}




/*
%mathpiper_docs,name="RulePattern",categories="Programmer Functions;Programming;Built In"
*CMD RulePattern --- defines a rule which uses a pattern as its predicate
 
*CALL
	RulePattern("operator", arity, precedence, pattern) body
*PARMS

{"operator"} -- string, name of function

{arity}, {precedence} -- integers

{pattern} -- a pattern object

{body} -- expression, body of rule

*DESC
This function defines a rule which uses a pattern as its predicate.

*SEE MacroRulePattern
%/mathpiper_docs
*/