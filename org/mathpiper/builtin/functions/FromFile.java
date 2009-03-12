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
import org.mathpiper.io.InputStatus;
import org.mathpiper.lisp.Environment;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.UtilityFunctions;

/**
 *
 * 
 */
public class FromFile extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkCore(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.KLispErrSecurityBreach);
        ConsPointer evaluated = new ConsPointer();
        aEnvironment.iEvaluator.evaluate(aEnvironment, evaluated, getArgumentPointer(aEnvironment, aStackTop, 1));

        // Get file name
        LispError.checkArgumentCore(aEnvironment, aStackTop, evaluated.getCons() != null, 1);
        String orig = evaluated.getCons().string();
        LispError.checkArgumentCore(aEnvironment, aStackTop, orig != null, 1);

        String hashedname = aEnvironment.getTokenHash().lookUpUnStringify(orig);

        InputStatus oldstatus = aEnvironment.iInputStatus;
        MathPiperInputStream previous = aEnvironment.iCurrentInput;
        try
        {
            aEnvironment.iInputStatus.setTo(hashedname);
            MathPiperInputStream input = // new StdFileInput(hashedname, aEnvironment.iInputStatus);
                    UtilityFunctions.openInputFile(aEnvironment, aEnvironment.iInputDirectories, hashedname, aEnvironment.iInputStatus);
            aEnvironment.iCurrentInput = input;
            // Open file
            LispError.checkCore(aEnvironment, aStackTop, input != null, LispError.KLispErrFileNotFound);

            // Evaluate the body
            aEnvironment.iEvaluator.evaluate(aEnvironment, getResult(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iCurrentInput = previous;
            aEnvironment.iInputStatus.restoreFrom(oldstatus);
        }
    //Return the getResult
    }
}
