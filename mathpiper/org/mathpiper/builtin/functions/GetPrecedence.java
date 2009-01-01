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
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.InfixOperator;
import org.mathpiper.lisp.LispError;

/**
 *
 *  
 */
public class GetPrecedence extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        InfixOperator op = UtilityFunctions.operatorInfo(aEnvironment, aStackTop, aEnvironment.iInfixOperators);
        if (op == null)
        {  // also need to check for a postfix or prefix operator

            op = UtilityFunctions.operatorInfo(aEnvironment, aStackTop, aEnvironment.iPrefixOperators);
            if (op == null)
            {
                op = UtilityFunctions.operatorInfo(aEnvironment, aStackTop, aEnvironment.iPostfixOperators);
                if (op == null)
                {  // or maybe it's a bodied function

                    op = UtilityFunctions.operatorInfo(aEnvironment, aStackTop, aEnvironment.iBodiedOperators);
                    LispError.checkCore(aEnvironment, aStackTop, op != null, LispError.KLispErrIsNotInFix);
                }
            }
        }
        result(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, "" + op.iPrecedence));
    }
}
