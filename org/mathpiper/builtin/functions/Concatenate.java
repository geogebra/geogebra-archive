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
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.SubList;

/**
 *
 *  
 */
public class Concatenate extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer all = new ConsPointer();
        all.setCons(aEnvironment.iListAtom.copy(false));
        ConsTraverser tail = new ConsTraverser(all);
        tail.goNext();
        int arg = 1;

        ConsTraverser iter = new ConsTraverser(argumentPointer(aEnvironment, aStackTop, 1).getCons().subList());
        iter.goNext();
        while (iter.getCons() != null)
        {
            LispError.checkIsListCore(aEnvironment, aStackTop, iter.ptr(), arg);
            UtilityFunctions.internalFlatCopy(tail.ptr(), iter.ptr().getCons().subList().getCons().rest());
            while (tail.getCons() != null)
            {
                tail.goNext();
            }
            iter.goNext();
            arg++;
        }
        result(aEnvironment, aStackTop).setCons(SubList.getInstance(all.getCons()));
    }
}
