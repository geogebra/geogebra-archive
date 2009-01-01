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
import org.mathpiper.builtin.PatternContainer;
import org.mathpiper.lisp.BuiltinObject;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;

/**
 *
 *  
 */
public class GenPatternCreate extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer pattern = new ConsPointer();
        pattern.setCons(argumentPointer(aEnvironment, aStackTop, 1).getCons());
        ConsPointer postpredicate = new ConsPointer();
        postpredicate.setCons(argumentPointer(aEnvironment, aStackTop, 2).getCons());

        ConsTraverser iter = new ConsTraverser(pattern);
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons() != null, 1);
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons().subList() != null, 1);
        iter.goSub();
        LispError.checkArgumentCore(aEnvironment, aStackTop, iter.getCons() != null, 1);
        iter.goNext();

        ConsPointer ptr = iter.ptr();


        org.mathpiper.parametermatchers.Pattern matcher = new org.mathpiper.parametermatchers.Pattern(aEnvironment, ptr, postpredicate);
        PatternContainer p = new PatternContainer(matcher);
        result(aEnvironment, aStackTop).setCons(BuiltinObject.getInstance(p));
    }
}
