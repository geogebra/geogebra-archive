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
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.behaviours.LocalSymbol;

/**
 *
 *  
 */
public class LocalSymbols extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        int nrArguments = UtilityFunctions.internalListLength(argumentPointer(aEnvironment, aStackTop, 0));
        int nrSymbols = nrArguments - 2;

        String names[] = new String[nrSymbols];
        String localnames[] = new String[nrSymbols];

        int uniquenumber = aEnvironment.getUniqueId();
        int i;
        for (i = 0; i < nrSymbols; i++)
        {
            String atomname = argumentPointer(argumentPointer(aEnvironment, aStackTop, 0), i + 1).getCons().string();
            LispError.checkArgumentCore(aEnvironment, aStackTop, atomname != null, i + 1);
            names[i] = atomname;
            int len = atomname.length();
            String newname = "$" + atomname + uniquenumber;
            String variable = (String) aEnvironment.getTokenHash().lookUp(newname);
            localnames[i] = variable;
        }
        LocalSymbol behaviour = new LocalSymbol(aEnvironment, names, localnames, nrSymbols);
        ConsPointer result = new ConsPointer();
        UtilityFunctions.internalSubstitute(result, argumentPointer(argumentPointer(aEnvironment, aStackTop, 0), nrArguments - 1), behaviour);
        aEnvironment.iEvaluator.evaluate(aEnvironment, result(aEnvironment, aStackTop), result);
    }
}
