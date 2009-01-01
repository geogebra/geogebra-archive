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
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Cons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 *  
 */
public class BuiltinAssoc extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        // key to find
        ConsPointer key = new ConsPointer();
        key.setCons(argumentPointer(aEnvironment, aStackTop, 1).getCons());

        // assoc-list to find it in
        ConsPointer list = new ConsPointer();
        list.setCons(argumentPointer(aEnvironment, aStackTop, 2).getCons());

        Cons t;

        //check that it is a compound object
        LispError.checkArgumentCore(aEnvironment, aStackTop, list.getCons().subList() != null, 2);
        t = list.getCons().subList().getCons();
        LispError.checkArgumentCore(aEnvironment, aStackTop, t != null, 2);
        t = t.rest().getCons();

        while (t != null)
        {
            if (t.subList() != null)
            {
                Cons sub = t.subList().getCons();
                if (sub != null)
                {
                    sub = sub.rest().getCons();
                    ConsPointer temp = new ConsPointer();
                    temp.setCons(sub);
                    if (UtilityFunctions.internalEquals(aEnvironment, key, temp))
                    {
                        result(aEnvironment, aStackTop).setCons(t);
                        return;
                    }
                }
            }
            t = t.rest().getCons();
        }
        result(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, "Empty"));
    }
}
