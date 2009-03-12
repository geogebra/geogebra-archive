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

/**
 *
 *  
 */
public class Divide extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber x = org.mathpiper.lisp.UtilityFunctions.getNumber(aEnvironment, aStackTop, 1);
        BigNumber y = org.mathpiper.lisp.UtilityFunctions.getNumber(aEnvironment, aStackTop, 2);
        BigNumber z = new BigNumber(aEnvironment.getPrecision());
        // if both arguments are integers, then BigNumber::Divide would perform an integer divide, but we want a float divide here.
        if (x.isInt() && y.isInt())
        {
            // why can't we just say BigNumber temp; ?
            BigNumber tempx = new BigNumber(aEnvironment.getPrecision());
            tempx.setTo(x);
            tempx.becomeFloat(aEnvironment.getPrecision());  // coerce x to float

            BigNumber tempy = new BigNumber(aEnvironment.getPrecision());
            tempy.setTo(y);
            tempy.becomeFloat(aEnvironment.getPrecision());  // coerce x to float

            z.divide(tempx, tempy, aEnvironment.getPrecision());
        } else
        {
            z.divide(x, y, aEnvironment.getPrecision());
        }
        getResult(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.Number(z));
        return;
    }
}
