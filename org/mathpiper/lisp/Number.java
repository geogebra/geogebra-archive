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

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.*;

/**
 * Holds a single number.
 *  
 */
public class Number extends Cons {
    /* Note: Since Number is a LispAtom, shouldn't it extend LispAtom instead of Cons? tk
     */

    /// number object; NULL if not yet converted from string
    BigNumber iBigNumber;
    /// string representation in decimal; NULL if not yet converted from BigNumber
    String iStringNumber;

    /**
     * Construct a number from either a BigNumber or a String.
     *
     * @param aNumber
     * @param aString
     */
    public Number(BigNumber aNumber, String aString) {
        iStringNumber = aString;
        iBigNumber = aNumber;
    }

    /**
     * Construct a number from a BigNumber.
     * @param aNumber
     */
    public Number(BigNumber aNumber) {
        iStringNumber = null;
        iBigNumber = aNumber;
    }

    /**
     * Construct a number from a decimal string representation and the specified number of decimal digits.
     *
     * @param aString a number in decimal format
     * @param aBasePrecision the number of decimal digits for the number
     */
    public Number(String aString, int aBasePrecision) {
        //(also create a number object).
        iStringNumber = aString;
        iBigNumber = null;  // purge whatever it was.

    // create a new BigNumber object out of iString, set its precision in digits
    //TODO FIXME enable this in the end    Number(aBasePrecision);
    }

    public Cons copy(boolean aRecursed) {
        return new Number(iBigNumber, iStringNumber);
    }

    public Object first() {
        return iBigNumber;
    }


    /**
     * Return a string representation of the number in decimal format with the maximum decimal precision allowed by the inherent accuracy of the number.
     *
     * @return string representation of the number
     * @throws java.lang.Exception
     */
    public String string() throws Exception {
        if (iStringNumber == null) {
            LispError.lispAssert(iBigNumber != null);  // either the string is null or the number but not both.

            iStringNumber = iBigNumber.numToString(0/*TODO FIXME*/, 10);
        // export the current number to string and store it as Number::iString
        }
        return iStringNumber;
    }

    public String toString() {
        String stringRepresentation = null;
        try {
            stringRepresentation = string();

        } catch (Exception e) {
            e.printStackTrace();  //Todo:fixme.
        }
        return stringRepresentation;

    }

    
    /**
     * Returns a BigNumber which has at least the specified precision.
     *
     * @param aPrecision
     * @return
     * @throws java.lang.Exception
     */
    public BigNumber getNumber(int aPrecision) throws Exception {
        /// If necessary, will create a BigNumber object out of the stored string, at given precision (in decimal?)
        if (iBigNumber == null) {  // create and store a BigNumber out of the string representation.
            LispError.lispAssert(iStringNumber != null);
            String str;
            str = iStringNumber;
            // aBasePrecision is in digits, not in bits, ok
            iBigNumber = new BigNumber(str, aPrecision, 10/*TODO FIXME BASE10*/);
        } // check if the BigNumber object has enough precision, if not, extend it
        // (applies only to floats). Note that iNumber->GetPrecision() might be < 0
        else if (!iBigNumber.isInt() && iBigNumber.getPrecision() < aPrecision) {
            if (iStringNumber != null) {// have string representation, can extend precision
                iBigNumber.setTo(iStringNumber, aPrecision, 10);
            } else {
                // do not have string representation, cannot extend precision!
            }
        }

        return iBigNumber;
    }

    /**
        Used to annotate data (not implemented yet).
    */
    public Cons setExtraInfo(ConsPointer aData) {
        /*TODO FIXME
        Cons* result = NEW LispAnnotatedObject<Number>(this);
        result->SetExtraInfo(aData);
        return result;
         */
        return null;
    }
}
