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

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class DumpNumber extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);

        x.dumpNumber(aEnvironment.iCurrentOutput);

        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));

    }//end method.

}//end class.




/*
%mathpiper_docs,name="DumpNumber",categories="Programmer Functions;Numerical (Arbitrary Precision);Built In"
*CMD DumpNumber --- prints the implementation details of a number
*CORE
*CALL
	DumpNumber(x)

*PARAMS
 * 
{x} -- an integer or decimal number.


*DESC

This function prints the implementation details of an integer or decimal number.

*E.G.
In> DumpNumber(42)
Result> True
Side Effects>
BigInteger: 42

In> DumpNumber(42.2343)
Result> True
Side Effects>
BigDecimal: 42.2343   Precision: 6   Unscaled Value: 422343   Scale: 4

%/mathpiper_docs
*/
