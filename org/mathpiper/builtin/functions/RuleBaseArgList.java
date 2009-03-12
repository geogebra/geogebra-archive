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
import org.mathpiper.lisp.SubList;
import org.mathpiper.lisp.userfunctions.UserFunction;

/**
 *
 *  
 */
public class RuleBaseArgList extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer name = new ConsPointer();
        name.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        String orig = name.getCons().string();
        LispError.checkArgumentCore(aEnvironment, aStackTop, orig != null, 1);
        String oper = UtilityFunctions.internalUnstringify(orig);

        ConsPointer sizearg = new ConsPointer();
        sizearg.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkArgumentCore(aEnvironment, aStackTop, sizearg.getCons() != null, 2);
        LispError.checkArgumentCore(aEnvironment, aStackTop, sizearg.getCons().string() != null, 2);

        int arity = Integer.parseInt(sizearg.getCons().string(), 10);

        UserFunction userFunc = aEnvironment.userFunction((String)aEnvironment.getTokenHash().lookUp(oper), arity);
        LispError.checkCore(aEnvironment, aStackTop, userFunc != null, LispError.KLispErrInvalidArg);

        ConsPointer list = userFunc.argList();
        ConsPointer head = new ConsPointer();
        head.setCons(aEnvironment.iListAtom.copy(false));
        head.getCons().rest().setCons(list.getCons());
        getResult(aEnvironment, aStackTop).setCons(SubList.getInstance(head.getCons()));
    }
}
