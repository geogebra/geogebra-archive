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

import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.builtin.PatternContainer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 *  
 */
public class GenPatternMatches extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer pattern = new ConsPointer();
        pattern.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        BuiltinContainer gen = pattern.getCons().getGeneric();
        LispError.checkArgumentCore(aEnvironment, aStackTop, gen != null, 1);
        LispError.checkArgumentCore(aEnvironment, aStackTop, gen.typeName().equals("\"Pattern\""), 1);

        ConsPointer list = new ConsPointer();
        list.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        PatternContainer patclass = (PatternContainer) gen;

        ConsTraverser iter = new ConsTraverser(list);
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons() != null, 2);
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons().getSubList() != null, 2);
        iter.goSub();
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons() != null, 2);
        iter.goNext();

        ConsPointer ptr = iter.ptr();
        LispError.checkArgumentCore(aEnvironment, aStackTop, ptr != null, 2);
        boolean matches = patclass.matches(aEnvironment, ptr);
        UtilityFunctions.internalBoolean(aEnvironment, getResult(aEnvironment, aStackTop), matches);
    }
}
