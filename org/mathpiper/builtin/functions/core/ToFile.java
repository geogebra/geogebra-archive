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

import java.io.FileOutputStream;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.io.StandardFileOutputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class ToFile extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);

        ConsPointer evaluated = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, evaluated, getArgumentPointer(aEnvironment, aStackTop, 1));

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1);
        String orig = (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1);
        String oper = Utility.unstringify(orig);

        // Open file for writing
        FileOutputStream localFP = new FileOutputStream(oper);
        LispError.check(aEnvironment, aStackTop, localFP != null, LispError.FILE_NOT_FOUND);
        StandardFileOutputStream newOutput = new StandardFileOutputStream(localFP);

        MathPiperOutputStream previous = aEnvironment.iCurrentOutput;
        aEnvironment.iCurrentOutput = newOutput;

        try
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iCurrentOutput = previous;
        }
    }
}



/*
%mathpiper_docs,name="ToFile",categories="User Functions;Input/Output;Built In"
*CMD ToFile --- connect current output to a file
*CORE
*CALL
	ToFile(name) body

*PARMS

{name} -- string, the name of the file to write the result to

{body} -- expression to be evaluated

*DESC

The current output is connected to the file "name". Then the expression
"body" is evaluated. Everything that the commands in "body" print
to the current output, ends up in the file "name". Finally, the
file is closed and the result of evaluating "body" is returned.

If the file is opened again, the old contents will be overwritten.
This is a limitation of {ToFile}: one cannot append to a file that has already been created.

*E.G. notest

Here is how one can create a file with C code to evaluate an expression:

	In> ToFile("expr1.c") WriteString(
	  CForm(Sqrt(x-y)*Sin(x)) );
	Out> True;
The file {expr1.c} was created in the current working directory and it
contains the line
	sqrt(x-y)*sin(x)

As another example, take a look at the following command:

	In> [ Echo("Result:");  \
	  PrettyForm(Taylor(x,0,9) Sin(x)); ];
	Result:

	     3    5      7       9
	    x    x      x       x
	x - -- + --- - ---- + ------
	    6    120   5040   362880

	Out> True;

Now suppose one wants to send the output of this command to a
file. This can be achieved as follows:

	In> ToFile("out") [ Echo("Result:");  \
	  PrettyForm(Taylor(x,0,9) Sin(x)); ];
	Out> True;

After this command the file {out} contains:


	Result:

	     3    5      7       9
	    x    x      x       x
	x - -- + --- - ---- + ------
	    6    120   5040   362880


*SEE FromFile, ToString, Echo, Write, WriteString, PrettyForm, Taylor
%/mathpiper_docs
*/