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
import org.mathpiper.lisp.TokenHash;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 *  
 */
abstract public class LexCompare2
{

    abstract boolean lexfunc(String f1, String f2, TokenHash aHashTable, int aPrecision);

    abstract boolean numfunc(BigNumber n1, BigNumber n2);

    void Compare(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer result1 = new ConsPointer();
        ConsPointer result2 = new ConsPointer();
        result1.setCons(BuiltinFunctionInitialize.getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        result2.setCons(BuiltinFunctionInitialize.getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        boolean cmp;
        BigNumber n1 = result1.getCons().getNumber(aEnvironment.getPrecision());
        BigNumber n2 = result2.getCons().getNumber(aEnvironment.getPrecision());
        if (n1 != null && n2 != null)
        {
            cmp = numfunc(n1, n2);
        } else
        {
            String str1;
            String str2;
            str1 = result1.getCons().string();
            str2 = result2.getCons().string();
            LispError.checkArgumentCore(aEnvironment, aStackTop, str1 != null, 1);
            LispError.checkArgumentCore(aEnvironment, aStackTop, str2 != null, 2);
            // the getPrecision argument is ignored in "lex" functions
            cmp = lexfunc(str1, str2,
                    aEnvironment.getTokenHash(),
                    aEnvironment.getPrecision());
        }

        UtilityFunctions.internalBoolean(aEnvironment, BuiltinFunctionInitialize.getResult(aEnvironment, aStackTop), cmp);
    }
}
