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

package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.SublistCons;
import org.mathpiper.lisp.userfunctions.SingleArityBranchingUserFunction;

/**
 *
 *  
 */
public class RulebaseArgList extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer name = new ConsPointer();
        name.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        String orig = (String) name.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1);
        String oper = Utility.unstringify(orig);

        ConsPointer sizearg = new ConsPointer();
        sizearg.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, sizearg.getCons() != null, 2);
        LispError.checkArgument(aEnvironment, aStackTop, sizearg.car() instanceof String, 2);

        int arity = Integer.parseInt( (String) sizearg.car(), 10);

        SingleArityBranchingUserFunction userFunc = aEnvironment.getUserFunction((String)aEnvironment.getTokenHash().lookUp(oper), arity);
        LispError.check(aEnvironment, aStackTop, userFunc != null, LispError.INVALID_ARGUMENT);

        ConsPointer list = userFunc.argList();
        ConsPointer head = new ConsPointer();
        head.setCons(aEnvironment.iListAtom.copy( aEnvironment, false));
        head.cdr().setCons(list.getCons());
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment,head.getCons()));
    }
}



/*
%mathpiper_docs,name="RuleBaseArgList",categories="User Functions;Built In;Built In"
*CMD RuleBaseArgList --- obtain list of arguments
*CORE
*CALL
	RuleBaseArgList("operator", arity)

*PARMS
{"operator"} -- string, name of function

{arity} -- integer

*DESC

Returns a list of atoms, symbolic parameters specified in the {RuleBase} call
for the function named {"operator"} with the specific {arity}.

*SEE RuleBase, HoldArgNr, HoldArg
%/mathpiper_docs
*/