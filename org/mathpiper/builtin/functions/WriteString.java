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

import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 * 
 */
public class WriteString extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgumentCore(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1);
        String str = getArgumentPointer(aEnvironment, aStackTop, 1).getCons().string();
        LispError.checkArgumentCore(aEnvironment, aStackTop, str != null, 1);
        LispError.checkArgumentCore(aEnvironment, aStackTop, str.charAt(0) == '\"', 1);
        LispError.checkArgumentCore(aEnvironment, aStackTop, str.charAt(str.length() - 1) == '\"', 1);

        int i = 1;
        int nr = str.length() - 1;
        //((*str)[i] != '\"')
        for (i = 1; i < nr; i++)
        {
            aEnvironment.iCurrentOutput.putChar(str.charAt(i));
        }
        // pass last printed character to the current printer
        aEnvironment.iCurrentPrinter.rememberLastChar(str.charAt(nr - 1));  // hacky hacky

        UtilityFunctions.internalTrue(aEnvironment, getResult(aEnvironment, aStackTop));
    }
}
