/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;

/**
 *
 *
 */
public class RoundToN extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        BigNumber decimalToBeRounded = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);

        BigNumber requestedPrecision = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 2);

        if(decimalToBeRounded.getPrecision() != requestedPrecision.toInt())
        {
            decimalToBeRounded.setPrecision(requestedPrecision.toInt());
        }

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(decimalToBeRounded));
    }


}//end class.



/*
%mathpiper_docs,name="RoundToN",categories="User Functions;Numeric;Built In"
*CMD RoundToN --- rounds a decimal number to a given precision
*CORE
*CALL
	RoundToN(decimalNumber, precision)

*PARMS
{decimalNumber} -- a decimal number to be rounded
{precision} -- precision to round the number to

*DESC

This command rounds a decimal number to a given precision.

*E.G.
In> RoundToN(7.57809824,2)
Result> 7.6

%/mathpiper_docs
*/
