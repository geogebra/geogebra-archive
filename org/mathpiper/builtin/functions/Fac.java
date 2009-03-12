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

/**
 *
 *  
 */
public class Fac extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgumentCore(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons().getNumber(0) != null, 1);
        ConsPointer arg = getArgumentPointer(aEnvironment, aStackTop, 1);

        //TODO fixme I am sure this can be optimized still
        int nr = (int) arg.getCons().getNumber(0).toLong();
        LispError.check(nr >= 0, LispError.KLispErrInvalidArg);
        BigNumber fac = new BigNumber("1", 10, 10);
        int i;
        for (i = 2; i <= nr; i++)
        {
            BigNumber m = new BigNumber("" + i, 10, 10);
            m.multiply(fac, m, 0);
            fac = m;
        }
        getResult(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.Number(fac));
    }
}
