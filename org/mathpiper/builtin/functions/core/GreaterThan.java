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
public class GreaterThan extends BuiltinFunction
{

    LexGreaterThan compare = new LexGreaterThan();

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        compare.Compare(aEnvironment, aStackTop);
    }
}//end class.



/*
%mathpiper_docs,name="GreaterThan"
*CMD GreaterThan --- comparison predicate
*CORE
*CALL
	GreaterThan(a,b)

*PARMS
{a}, {b} -- numbers or strings
*DESC
Comparing numbers or strings (lexicographically).

**E.G.
	In> GreaterThan(1,1)
	Out> False;
	In> GreaterThan("b","a")
	Out> True;

*SEE LessThan, Equals
%/mathpiper_docs
*/
