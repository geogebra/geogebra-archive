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
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;

/**
 *
 *  
 */
public class StringMidGet extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 3).getCons());
        LispError.checkIsStringCore(aEnvironment, aStackTop, evaluated, 3);
        String orig = evaluated.getCons().string();

        ConsPointer index = new ConsPointer();
        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispError.checkArgumentCore(aEnvironment, aStackTop, index.getCons() != null, 1);
        LispError.checkArgumentCore(aEnvironment, aStackTop, index.getCons().string() != null, 1);
        int from = Integer.parseInt(index.getCons().string(), 10);
        LispError.checkArgumentCore(aEnvironment, aStackTop, from > 0, 1);

        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkArgumentCore(aEnvironment, aStackTop, index.getCons() != null, 2);
        LispError.checkArgumentCore(aEnvironment, aStackTop, index.getCons().string() != null, 2);
        int count = Integer.parseInt(index.getCons().string(), 10);


        String str = "\"" + orig.substring(from, from + count) + "\"";
        getResult(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, str));
    }
}
