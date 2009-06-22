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
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;

/**
 *
 *  
 */
public class ConcatenateStrings extends BuiltinFunctionInitialize
{

    void ConcatenateStrings(StringBuffer aStringBuffer, Environment aEnvironment, int aStackTop) throws Exception
    {
        aStringBuffer.append('\"');
        int arg = 1;

        ConsTraverser iter = new ConsTraverser(getArgumentPointer(aEnvironment, aStackTop, 1).getCons().getSubList());
        iter.goNext();
        while (iter.getCons() != null)
        {
            LispError.checkIsStringCore(aEnvironment, aStackTop, iter.ptr(), arg);
            String thisString = iter.getCons().string();
            String toAppend = thisString.substring(1, thisString.length() - 1);
            aStringBuffer.append(toAppend);
            iter.goNext();
            arg++;
        }
        aStringBuffer.append('\"');
    }

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        StringBuffer strBuffer = new StringBuffer("");
        ConcatenateStrings(strBuffer, aEnvironment, aStackTop);
        getResult(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, strBuffer.toString()));
    }
}
