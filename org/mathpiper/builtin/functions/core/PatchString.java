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
import org.mathpiper.io.InputStatus;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.io.StringInputStream;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.parsers.MathPiperParser;
import org.mathpiper.lisp.parsers.Parser;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;

/**
 *
 *  
 */
public class PatchString extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {
        String unpatchedString;
        unpatchedString = (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, unpatchedString != null, 2);

        String resultString;
        StringBuilder resultStringBuilder = new StringBuilder();
        String[] tags = unpatchedString.split("\\?\\>");
        if (tags.length > 1) {
            for (int x = 0; x < tags.length; x++) {
                String[] tag = tags[x].split("\\<\\?");
                if (tag.length > 1) {
                    resultStringBuilder.append(tag[0]);
                    String scriptCode = tag[1];
                    if (scriptCode.endsWith(";")) {
                        scriptCode = scriptCode.substring(0, scriptCode.length() - 1);
                    }
                    resultString = lispEvaluate(aEnvironment, "Eval(" + scriptCode + ");");
                    resultStringBuilder.append(resultString);
                }
            }//end for.
            resultStringBuilder.append(tags[tags.length - 1]);
        } else {
            resultStringBuilder.append(unpatchedString);
        }

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, resultStringBuilder.toString()));
    }

    private String lispEvaluate(Environment aEnvironment, String inputExpression) throws Exception {
        ConsPointer result = new ConsPointer();
        StringBuffer oper = new StringBuffer();
        StringOutputStream newOutput = new StringOutputStream(oper);
        MathPiperOutputStream previous = aEnvironment.iCurrentOutput;
        aEnvironment.iCurrentOutput = newOutput;

        MathPiperTokenizer tokenizer = new MathPiperTokenizer();
        InputStatus someStatus = new InputStatus();
        ConsPointer inputExpressionPointer = new ConsPointer();
        try {
            StringBuffer inp = new StringBuffer();
            inp.append(inputExpression);
            inp.append(";");
            StringInputStream inputExpressionBuffer = new StringInputStream(inp, someStatus);

            Parser infixParser = new MathPiperParser(tokenizer, inputExpressionBuffer, aEnvironment, aEnvironment.iPrefixOperators, aEnvironment.iInfixOperators, aEnvironment.iPostfixOperators, aEnvironment.iBodiedOperators);
            infixParser.parse(inputExpressionPointer);

            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, result, inputExpressionPointer);

            String resultString = Utility.printExpression(result, aEnvironment, 0);

        } catch (Exception e) {
            throw e;
        } finally {
            aEnvironment.iCurrentOutput = previous;
        }
        return oper.toString();
    }
}//end class.



/*
%mathpiper_docs,name="PatchString",categories="User Functions;String Manipulation;Built In"
*CMD PatchString --- execute commands between {<?} and {?>} in strings
*CORE
*CALL
	PatchString(string)

*PARMS

{string} -- a string to patch

*DESC

This function does the same as PatchLoad, but it works on a string
in stead of on the contents of a text file. See PatchLoad for more
details.

*E.G.

	In> PatchString("Two plus three \
	  is <? Write(2+3); ?> ");
	Out> "Two plus three is 5 ";

*SEE PatchLoad
%/mathpiper_docs
*/