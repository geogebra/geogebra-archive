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
package org.mathpiper.lisp;

import org.mathpiper.io.OutputStream;
import org.mathpiper.*;

/**
 * Abstract evaluator for Lisp expressions.
 * evaluate() is an abstract method, to be provided by the derived class.
 * The other functions are stubs.
 */

public abstract class ExpressionEvaluator
{

    UserStackInformation iBasicInfo = new UserStackInformation();

    public abstract void evaluate(Environment aEnvironment, ConsPointer aResult, ConsPointer aExpression) throws Exception;

    public void resetStack()
    {
    }

    public UserStackInformation stackInformation()
    {
        return iBasicInfo;
    }

    public void showStack(Environment aEnvironment, OutputStream aOutput)
    {
    }
};
