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
import org.mathpiper.lisp.DefFile;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.userfunctions.MultipleArityUserFunction;

/**
 *
 *  
 */
public class DefLoadFunction extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer name = new ConsPointer();
        name.setCons(argumentPointer(aEnvironment, aStackTop, 1).getCons());
        String orig = name.getCons().string();
        LispError.checkArgumentCore(aEnvironment, aStackTop, orig != null, 1);
        String oper = UtilityFunctions.internalUnstringify(orig);

        MultipleArityUserFunction multiUserFunc =
                aEnvironment.multiUserFunction((String)aEnvironment.getTokenHash().lookUp(oper));
        if (multiUserFunc != null)
        {
            if (multiUserFunc.iFileToOpen != null)
            {
                DefFile def = multiUserFunc.iFileToOpen;
                if (!def.iIsLoaded)
                {
                    multiUserFunc.iFileToOpen = null;
                    UtilityFunctions.internalUse(aEnvironment, def.iFileName);
                }
            }
        }
        UtilityFunctions.internalTrue(aEnvironment, result(aEnvironment, aStackTop));
    }
}
