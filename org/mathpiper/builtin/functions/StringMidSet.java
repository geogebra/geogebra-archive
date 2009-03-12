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

import org.mathpiper.builtin.*;
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;

/**
 *
 *  
 */
public class StringMidSet extends BuiltinFunctionInitialize
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

        ConsPointer ev2 = new ConsPointer();
        ev2.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkIsStringCore(aEnvironment, aStackTop, ev2, 2);
        String replace = ev2.getCons().string();

        LispError.checkCore(aEnvironment, aStackTop, from + replace.length() - 2 < orig.length(), LispError.KLispErrInvalidArg);
        String str;
        str = orig.substring(0, from);
        str = str + replace.substring(1, replace.length() - 1);
        //System.out.println("from="+from+replace.length()-2);
        str = str + orig.substring(from + replace.length() - 2, orig.length());
        getResult(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, str));
    }
}
