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

package org.mathpiper.builtin.functions;

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 *  
 */
public class FromBase extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get the base to convert to:
        // Evaluate first argument, and store getResult in oper
        ConsPointer oper = new ConsPointer();
        oper.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        // check that getResult is a number, and that it is in fact an integer
        BigNumber num = oper.getCons().getNumber(aEnvironment.getPrecision());
        LispError.checkArgumentCore(aEnvironment, aStackTop, num != null, 1);
        // check that the base is an integer between 2 and 32
        LispError.checkArgumentCore(aEnvironment, aStackTop, num.isInt(), 1);

        // Get a short platform integer from the first argument
        int base = (int) (num.toDouble());

        // Get the number to convert
        ConsPointer fromNum = new ConsPointer();
        fromNum.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        String str2;
        str2 = fromNum.getCons().string();
        LispError.checkArgumentCore(aEnvironment, aStackTop, str2 != null, 2);

        // Added, unquote a string
        LispError.checkArgumentCore(aEnvironment, aStackTop, UtilityFunctions.internalIsString(str2), 2);
        str2 = aEnvironment.getTokenHash().lookUpUnStringify(str2);

        // convert using correct base
        BigNumber z = new BigNumber(str2, aEnvironment.getPrecision(), base);
        getResult(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.Number(z));
    }
}
