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
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class IsString extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer result = new ConsPointer();
        result.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        boolean resultBoolean ;
         if( result.car() instanceof String  )
         {
             resultBoolean = Utility.isString(   (String) result.car() );

         }
        else{
            resultBoolean = false;
        }
        Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), resultBoolean);
                
    }
}



/*
%mathpiper_docs,name="IsString",categories="User Functions;Predicates;Built In"
*CMD IsString --- test for an string
*CORE
*CALL
	IsString(expr)

*PARMS

{expr} -- expression to test

*DESC

This function tests whether "expr" is a string. A string is a text
within quotes, e.g. {"duh"}.

*E.G.

	In> IsString("duh");
	Out> True;
	In> IsString(duh);
	Out> False;

*SEE IsAtom, IsNumber
%/mathpiper_docs
*/