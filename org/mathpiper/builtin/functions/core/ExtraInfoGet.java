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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class ExtraInfoGet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer object = new ConsPointer();
        object.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        ConsPointer result = object.getCons().getExtraInfoPointer();
        if (result == null)
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        } else if (result.getCons() == null)
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        } else
        {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(result.getCons());
        }
    }
}
