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

/**
 *
 *  
 */
public class GetCoreError extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aEnvironment.getTokenHash().lookUpStringify(aEnvironment.iError)));
    }
}



/*
%mathpiper_docs,name="GetCoreError",categories="Programmer Functions;Built In"
*CMD GetCoreError --- get "hard" error string
*CORE
*CALL
	GetCoreError()

*DESC

GetCoreError returns a string describing the core error.
TrapError and GetCoreError can be used in combination to write
a custom error handler error reporting facility that does not stop the execution is provided by the function {Assert}.

*EG

	In>

*SEE Assert, Check, TrapError

%/mathpiper_docs
*/