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
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 *  
 */
public class Tail extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer first = new ConsPointer();
        UtilityFunctions.internalTail(first, getArgumentPointer(aEnvironment, aStackTop, 1));
        UtilityFunctions.internalTail(getResult(aEnvironment, aStackTop), first);
        ConsPointer head = new ConsPointer();
        head.setCons(aEnvironment.iListAtom.copy(false));
        head.getCons().rest().setCons(getResult(aEnvironment, aStackTop).getCons().getSubList().getCons());
        getResult(aEnvironment, aStackTop).getCons().getSubList().setCons(head.getCons());
    }
}
