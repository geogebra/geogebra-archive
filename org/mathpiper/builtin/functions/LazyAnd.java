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
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.SubList;

/**
 *
 *  
 */
public class LazyAnd extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer nogos = new ConsPointer();
        int nrnogos = 0;
        ConsPointer evaluated = new ConsPointer();

        ConsTraverser iter = new ConsTraverser(getArgumentPointer(aEnvironment, aStackTop, 1).getCons().getSubList());
        iter.goNext();
        while (iter.getCons() != null)
        {
            aEnvironment.iEvaluator.evaluate(aEnvironment, evaluated, iter.ptr());
            if (UtilityFunctions.isFalse(aEnvironment, evaluated))
            {
                UtilityFunctions.internalFalse(aEnvironment, getResult(aEnvironment, aStackTop));
                return;
            } else if (!UtilityFunctions.isTrue(aEnvironment, evaluated))
            {
                ConsPointer ptr = new ConsPointer();
                nrnogos++;
                ptr.setCons(evaluated.getCons().copy(false));
                ptr.getCons().rest().setCons(nogos.getCons());
                nogos.setCons(ptr.getCons());
            }

            iter.goNext();
        }

        if (nogos.getCons() != null)
        {
            if (nrnogos == 1)
            {
                getResult(aEnvironment, aStackTop).setCons(nogos.getCons());
            } else
            {
                ConsPointer ptr = new ConsPointer();

                UtilityFunctions.internalReverseList(ptr, nogos);
                nogos.setCons(ptr.getCons());

                ptr.setCons(getArgumentPointer(aEnvironment, aStackTop, 0).getCons().copy(false));
                ptr.getCons().rest().setCons(nogos.getCons());
                nogos.setCons(ptr.getCons());
                getResult(aEnvironment, aStackTop).setCons(SubList.getInstance(nogos.getCons()));

            //aEnvironment.CurrentPrinter().Print(getResult(aEnvironment, aStackTop), *aEnvironment.CurrentOutput());
            }
        } else
        {
            UtilityFunctions.internalTrue(aEnvironment, getResult(aEnvironment, aStackTop));
        }
    }
}
