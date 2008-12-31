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
import org.mathpiper.lisp.UtilityFunctions;

;

import org.mathpiper.lisp.Environment;

import org.mathpiper.lisp.UtilityFunctions;

/**
 *Corresponds to the MathPiper function AddN.
 *If called with one argument (unary plus), this argument is
 *converted to BigNumber. If called with two arguments (binary plus),
 *both argument are converted to a BigNumber, and these are added
 *together at the current getPrecision. The sum is returned.
 * See: getNumber(), BigNumber::Add().
 *  
 */
public class Add extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        int length = UtilityFunctions.internalListLength(argumentPointer(aEnvironment, aStackTop, 0));
        if (length == 2)
        {
            BigNumber x;
            x = UtilityFunctions.getNumber(aEnvironment, aStackTop, 1);
            result(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.Number(x));
            return;
        } else
        {
            BigNumber x = UtilityFunctions.getNumber(aEnvironment, aStackTop, 1);
            BigNumber y = UtilityFunctions.getNumber(aEnvironment, aStackTop, 2);
            int bin = aEnvironment.getPrecision();
            BigNumber z = new BigNumber(bin);
            z.Add(x, y, aEnvironment.getPrecision());
            result(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.Number(z));
            return;
        }
    }
}
