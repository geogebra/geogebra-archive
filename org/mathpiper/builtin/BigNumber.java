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
package org.mathpiper.builtin;

import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.LispError;
import java.math.*;

/**
 * 
 *
 */
public class BigNumber {

    BigInteger javaBigInteger = null;
    BigDecimal javaBigDecimal = null;
    int iPrecision;
    int iTensExp;
    private static BigDecimal zero = new BigDecimal("0");
    private static BigDecimal one = new BigDecimal("1");
    private static BigDecimal two = new BigDecimal("2");
    private static BigDecimal ten = new BigDecimal("10");

    public static boolean numericSupportForMantissa() {
        return true;
    }


    //constructors
    /**
     * Create a BigNumber with the specified precision and base.
     *
     * @param aString
     * @param aBasePrecision
     * @param aBase
     */
    public BigNumber(String aString, int aBasePrecision, int aBase/*=10*/) {
        setTo(aString, aBasePrecision, aBase);
    }

    /**
     * Create a copy of a BigNumber.
     *
     * @param aOther
     */
    public BigNumber(BigNumber aOther) {
        setTo(aOther);
    }


    // no constructors from int or double to avoid automatic conversions. TODO:tk:What does this mean?
    /**
     * Create a BigNumber with the given precision and initialize it to 0.
     * 
     * @param aPrecision
     */
    public BigNumber(int aPrecision/* = 20*/) {
        iPrecision = aPrecision;
        iTensExp = 0;
        javaBigInteger = new BigInteger("0");
    }

    /**
     * Set this BigNumber to the same value as another BigNumber.
     *
     * @param aOther
     */
    public void setTo(BigNumber aOther) {
        iPrecision = aOther.getPrecision();
        iTensExp = aOther.iTensExp;
        javaBigInteger = aOther.javaBigInteger;
        javaBigDecimal = aOther.javaBigDecimal;
    }

    /**
     * Set this BigNumber to a value specified in the given string using the given precision.
     *
     * @param aString
     * @param aPrecision
     * @param aBase
     */
    public void setTo(String aString, int aPrecision, int aBase/*=10*/) {
        javaBigInteger = null;
        javaBigDecimal = null;
        boolean isFloat = isFloat(aString, aBase);

        iPrecision = aPrecision;
        iTensExp = 0;
        if (isFloat) {
            int decimalPos;
            decimalPos = aString.indexOf("e");
            if (decimalPos < 0) {
                decimalPos = aString.indexOf("E");
            }
            if (decimalPos > 0) // will never be zero
            {
                iTensExp = Integer.parseInt(aString.substring(decimalPos + 1, aString.length()));
                aString = aString.substring(0, decimalPos);
            }

            javaBigDecimal = new BigDecimal(aString); //TODO FIXME does not listen to aBase!!!
            if (javaBigDecimal.scale() > iPrecision) {
                iPrecision = javaBigDecimal.scale();
            }
        } else {
            javaBigInteger = new BigInteger(aString, aBase);
        }
    }

    /**
     * Set this BigNumber to the value of the specified Java long.
     *
     * @param javaLong
     */
    public void setTo(long javaLong) {
        setTo("" + javaLong, iPrecision, 10);
    }

    /**
     * Set this BigNumber to the value of the specified Java int.
     *
     * @param javaInt
     */
    public void setTo(int javaInt) {
        setTo((long) javaInt);
    }

    /**
     * Set this BigNumber to the value of the specified Java double.
     * 
     * @param javaDouble
     */
    public void setTo(double javaDouble) {
        setTo("" + javaDouble, iPrecision, 10);
    }

    /**
     * Is the specified string representing a floating point number?
     * 
     * @param aString
     * @param aBase
     * @return
     */
    boolean isFloat(String aString, int aBase) {
        if (aString.indexOf('.') >= 0) {
            return true;
        }
        if (aBase > 10) {
            return false;
        }
        if (aString.indexOf('e') >= 0) {
            return true;
        }
        if (aString.indexOf('E') >= 0) {
            return true;
        }
        return false;
    }

    // Convert back to other types.
    /**
     * Return a string representation of this BigNumber which has the specified precision and base.
     * @param aPrecision
     * @param aBase
     * @return
     */
    public String numToString(int aPrecision, int aBase/*=10*/) {
        if (javaBigInteger != null) {
            return javaBigInteger.toString(aBase);
        } else {
            String result = javaBigDecimal.toString();
            //System.out.println("BigNumResult: " + result);

            
            int extraExp = 0;
            // Parse out the exponent
            {
                int pos = result.indexOf("E");
                if (pos < 0) {
                    pos = result.indexOf("e");
                }
                if (pos > 0) {
                    extraExp = Integer.parseInt(result.substring(pos + 1));
                    result = result.substring(0, pos);
                }
            }


            int dotPos = result.indexOf('.');
            if (dotPos >= 0) {
                int endpos = result.length();
                while (endpos > dotPos && result.charAt(endpos - 1) == '0') {
                    endpos--;
                }
                if (endpos > 1) {
                    if (result.charAt(endpos - 1) == '.' && result.charAt(endpos - 2) >= '0' && result.charAt(endpos - 2) <= '9') {
                        endpos--;
                    }
                }
                result = result.substring(0, endpos);
                }//end if.
            
                if ((iTensExp + extraExp) != 0) {
                    result = result + "e" + (iTensExp + extraExp);
                }
            
            return result;
        }
    }//end method.

    /**
     * Return an approximate representation of this BigNumber as a Java double.
     *
     * @return
     */
    public double toDouble() {
        if (javaBigInteger != null) {
            return javaBigInteger.doubleValue();
        } else {
            return javaBigDecimal.doubleValue();
        }
    }

    /**
     * Return a representation of this BigNumber as a Java long.
     * @return
     */
    public long toLong() {
        if (javaBigInteger != null) {
            return javaBigInteger.longValue();
        } else {
            return javaBigDecimal.longValue();
        }
    }

    /**
     * Determines if the specified BigNumber is equal in value to this one.
     *
     * @param aOther
     * @return
     */
    public boolean equals(BigNumber aOther) {
        if (javaBigInteger != null) {
            if (aOther.javaBigInteger == null) {
                //hier
                BigDecimal x = getDecimal(this);
                if (x.compareTo(aOther.javaBigDecimal) == 0) {
                    return true;
                }
                return false;
            }
            return (javaBigInteger.compareTo(aOther.javaBigInteger) == 0);
        }
        if (javaBigDecimal != null) {
            BigDecimal thisd = javaBigDecimal;
            BigDecimal otherd = aOther.javaBigDecimal;
            if (otherd == null) {
                otherd = getDecimal(aOther);
            }
            if (iTensExp > aOther.iTensExp) {
                thisd = thisd.movePointRight(iTensExp - aOther.iTensExp);
            } else if (iTensExp < aOther.iTensExp) {
                otherd = otherd.movePointRight(iTensExp - aOther.iTensExp);
            }
            return (thisd.compareTo(otherd) == 0);
        }
        return true;
    }//end method.

    /**
     * Determines if this BigNumber is an integer.
     *
     * @return
     */
    public boolean isInt() {
        return (javaBigInteger != null && javaBigDecimal == null);
    }

    /**
     * Determines if this BigNumber is less than 65535.  (Floating point not implemented yet).
     *
     * @return
     */
    public boolean isSmall() {
        if (isInt()) {
            BigInteger i = javaBigInteger.abs();
            return (i.compareTo(new BigInteger("65535")) < 0); //TODO: Should this be 65536?
        } else // a function to test smallness of a float is not present in ANumber, need to code a workaround to determine whether a number fits into double.
        {
            //TODO fixme
            return true;
        /*
        LispInt tensExp = iNumber->iTensExp;
        if (tensExp<0)tensExp = -tensExp;
        return
        (
        iNumber->iPrecision <= 53  // standard float is 53 bits
        && tensExp<1021 // 306  // 1021 bits is about 306 decimals
        );
        // standard range of double precision is about 53 bits of mantissa and binary exponent of about 1021
         */
        }
    }

    /**
     * Convert this BigNumber to an integer.
     */
    public void becomeInteger() {
        if (javaBigDecimal != null) {
            javaBigInteger = javaBigDecimal.toBigInteger();
            javaBigDecimal = null;
        }
    }

    /**
     * Convert this BigNumber to a float which has the specified precision.
     * @param aPrecision
     */
    public void becomeFloat(int aPrecision/*=0*/) {
        if (javaBigInteger != null) {
            javaBigDecimal = new BigDecimal(javaBigInteger);
            iTensExp = 0;
            javaBigInteger = null;
        }
    }

    /**
     * Determine if this BigNumber is less than the specified BigNumber.
     *
     * @param aOther
     * @return
     */
    public boolean lessThan(BigNumber aOther) {
        boolean floatResult = (javaBigDecimal != null || aOther.javaBigDecimal != null);
        if (floatResult) {
            BigDecimal dX = getDecimal(this);
            BigDecimal dY = getDecimal(aOther);
            return dX.compareTo(dY) < 0;
        } else {
            return javaBigInteger.compareTo(aOther.javaBigInteger) < 0;
        }
    }


    //arithmetic.
    /**
     * Multiply the specified BigIntegers using the specified precision and place the result in this BigInteger.
     *
     * @param aX
     * @param aY
     * @param aPrecision
     */
    public void multiply(BigNumber aX, BigNumber aY, int aPrecision) {
        boolean floatResult = (aX.javaBigDecimal != null || aY.javaBigDecimal != null);
        if (floatResult) {
            BigDecimal dX = getDecimal(aX);
            BigDecimal dY = getDecimal(aY);
            javaBigInteger = null;
            javaBigDecimal = dX.multiply(dY);
            int newScale = iPrecision;
            if (newScale < javaBigDecimal.scale()) {
                javaBigDecimal = javaBigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_EVEN);
            }
            iTensExp = aX.iTensExp + aY.iTensExp;
        } else {
            javaBigDecimal = null;
            javaBigInteger = aX.javaBigInteger.multiply(aY.javaBigInteger);
        }
    }

    /**
     * Add the specified BigIntegers using the specified precision and place the result in this BigInteger.
     *
     * @param aX
     * @param aY
     * @param aPrecision
     */
    public void add(BigNumber aX, BigNumber aY, int aPrecision) {
        boolean floatResult = (aX.javaBigDecimal != null || aY.javaBigDecimal != null);
        if (floatResult) {
            BigDecimal dX = getDecimal(aX);
            BigDecimal dY = getDecimal(aY);

            javaBigInteger = null;
            if (aX.iTensExp > aY.iTensExp) {
                dY = dY.movePointLeft(aX.iTensExp - aY.iTensExp);
                iTensExp = aX.iTensExp;
            } else if (aX.iTensExp < aY.iTensExp) {
                dX = dX.movePointLeft(aY.iTensExp - aX.iTensExp);
                iTensExp = aY.iTensExp;
            }
            javaBigDecimal = dX.add(dY);
        } else {
            javaBigDecimal = null;
            javaBigInteger = aX.javaBigInteger.add(aY.javaBigInteger);
        }
    }

    /**
     * Negate the specified BigInteger and place the result in this BigInteger.
     * 
     * @param aX
     */
    public void negate(BigNumber aX) {
        if (aX.javaBigInteger != null) {
            javaBigDecimal = null;
            javaBigInteger = aX.javaBigInteger.negate();
        }
        if (aX.javaBigDecimal != null) {
            javaBigInteger = null;
            javaBigDecimal = aX.javaBigDecimal.negate();
            iTensExp = aX.iTensExp;
        }
    }

    /**
     * Divide the specified BigIntegers using the specified precision and place the result in this BigInteger.
     *
     * @param aX
     * @param aY
     * @param aPrecision
     */
    public void divide(BigNumber aX, BigNumber aY, int aPrecision) {
        //Note: if the two arguments are integers, this method should return an integer result!
        boolean floatResult = (aX.javaBigDecimal != null || aY.javaBigDecimal != null);
        if (floatResult) {
            BigDecimal dX = getDecimal(aX);
            BigDecimal dY = getDecimal(aY);
            javaBigInteger = null;
            int newScale = aPrecision + aY.getPrecision();
            if (newScale > dX.scale()) {
                dX = dX.setScale(newScale);
            }
            javaBigDecimal = dX.divide(dY, BigDecimal.ROUND_HALF_EVEN);
            iPrecision = javaBigDecimal.scale();
            iTensExp = aX.iTensExp - aY.iTensExp;
        } else {
            javaBigDecimal = null;
            javaBigInteger = aX.javaBigInteger.divide(aY.javaBigInteger);
        }
    }

    /**
     * Perform y mod z on the two specified integers.  The result is placed into this BigInteger.
     * 
     * @param aY
     * @param aZ
     * @throws java.lang.Exception
     */
    public void mod(BigNumber aY, BigNumber aZ) throws Exception {
        LispError.check(aY.javaBigInteger != null, LispError.KLispErrNotInteger);
        LispError.check(aZ.javaBigInteger != null, LispError.KLispErrNotInteger);
        //TODO fixme    LispError.check(!IsZero(aZ),LispError.KLispErrInvalidArg);
        javaBigInteger = aY.javaBigInteger.mod(aZ.javaBigInteger);
        javaBigDecimal = null;
    }

    /**
     * Print the internal state of this number.  Used for debugging purposes.
     *
     * @param aOutput
     * @throws java.lang.Exception
     */
    public void dumpDebugInfo(MathPiperOutputStream aOutput) throws Exception {
        if (javaBigInteger != null) {
            aOutput.write("integer: " + javaBigInteger.toString() + "\n");
        } else {
            aOutput.write("decimal: " + javaBigDecimal.unscaledValue() + " scale " + javaBigDecimal.scale() + " x 10^(" + iTensExp + ")\n");
        }
    }



    public String toString() {
        if (javaBigInteger != null) {
            return ("Integer: " + javaBigInteger.toString() + "   \n");
        } else {

            return ("BigDecimal: " + javaBigDecimal.toString() + "  Decimal: " + javaBigDecimal.unscaledValue() + "  Scale: " + javaBigDecimal.scale() + " x 10^" + iTensExp+"   \n");
        }
    }




    /**
     * Perform a floor operation on this BigInteger, if possible.
     * @param aX
     */
    public void floor(BigNumber aX) {
        if (aX.javaBigDecimal != null) {
            BigDecimal d = aX.javaBigDecimal;
            if (aX.iTensExp != 0) {
                d = d.movePointRight(aX.iTensExp);
            }
            BigInteger rounded = d.toBigInteger();
            if (aX.javaBigDecimal.signum() < 0) {
                BigDecimal back = new BigDecimal(rounded);
                BigDecimal difference = aX.javaBigDecimal.subtract(back);
                if (difference.signum() != 0) {
                    rounded = rounded.add(new BigInteger("-1"));
                }
            }
            javaBigInteger = rounded;
        } else {
            javaBigInteger = aX.javaBigInteger;
        }
        javaBigDecimal = null;
    }

    /**
     * Set the precision of this BigInteger (in bits).
     *
     * @param aPrecision
     */
    public void precision(int aPrecision) {
        iPrecision = aPrecision;
        if (javaBigDecimal != null) {
            if (javaBigDecimal.scale() > aPrecision) {
                javaBigDecimal = javaBigDecimal.setScale(aPrecision, BigDecimal.ROUND_HALF_EVEN);
            }
        }
    }

    /// Bitwise operations.
    /**
     * Shift the specified BigNumber to the left the specified number of bits and place the result in this BigNumber.
     *
     * @param aX
     * @param aNrToShift
     * @throws java.lang.Exception
     */
    public void shiftLeft(BigNumber aX, int aNrToShift) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        javaBigDecimal = null;
        javaBigInteger = aX.javaBigInteger.shiftLeft(aNrToShift);
    }

    /**
     * Shift the specified BigNumber to the right the specified number of bits and place the result in this BigNumber.
     * @param aX
     * @param aNrToShift
     * @throws java.lang.Exception
     */
    public void shiftRight(BigNumber aX, int aNrToShift) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        javaBigDecimal = null;
        javaBigInteger = aX.javaBigInteger.shiftRight(aNrToShift);
    }

    /**
     * Perform a GCD operation on the specified BigNumbers and place the result in this BigNumber.
     *
     * @param aX
     * @param aY
     * @throws java.lang.Exception
     */
    public void gcd(BigNumber aX, BigNumber aY) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        LispError.lispAssert(aY.javaBigInteger != null);
        javaBigInteger = aX.javaBigInteger.gcd(aY.javaBigInteger);
        javaBigDecimal = null;
    }

    /**
     * Perform a bitwise AND operation on the specified BigNumbers and place the result in this BigNumber.
     *
     * @param aX
     * @param aY
     * @throws java.lang.Exception
     */
    public void bitAnd(BigNumber aX, BigNumber aY) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        LispError.lispAssert(aY.javaBigInteger != null);
        javaBigInteger = aX.javaBigInteger.and(aY.javaBigInteger);
        javaBigDecimal = null;
    }

    /**
     * Perform a bitwise OR operation on the specified BigNumbers and place the result in this BigNumber.
     * @param aX
     * @param aY
     * @throws java.lang.Exception
     */
    public void bitOr(BigNumber aX, BigNumber aY) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        LispError.lispAssert(aY.javaBigInteger != null);
        javaBigInteger = aX.javaBigInteger.or(aY.javaBigInteger);
        javaBigDecimal = null;
    }

    /**
     * Perform a bitwise XOR operation on the specified BigNumbers and place the result in this BigNumber.
     *
     * @param aX
     * @param aY
     * @throws java.lang.Exception
     */
    public void bitXor(BigNumber aX, BigNumber aY) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        LispError.lispAssert(aY.javaBigInteger != null);
        javaBigInteger = aX.javaBigInteger.xor(aY.javaBigInteger);
        javaBigDecimal = null;
    }

    /**
     * Perform a bitwise NOT operation on the specified BigNumber and place the result in this BigNumber.
     *
     * @param aX
     * @throws java.lang.Exception
     */
    void bitNot(BigNumber aX) throws Exception {
        LispError.lispAssert(aX.javaBigInteger != null);
        javaBigInteger = aX.javaBigInteger.not();
        javaBigDecimal = null;
    }

    /**
     * If this BigNumber is an integer, its number of significant bits is returned and if it is a decimal, its binary exponent is returned.  
     * The binary exponent is a shortcut for a binary logarithm.
     * 
     * @return
     */
    public long bitCount() {
        //TODO fixme check that it works as needed
        if (javaBigInteger != null) {
            return javaBigInteger.abs().bitLength();
        }
        {
            BigDecimal d = javaBigDecimal.abs();
            if (iTensExp != 0) {
                d = d.movePointRight(iTensExp);
            }
            if (d.compareTo(one) > 0) {
                return d.toBigInteger().bitLength();
            }
            BigDecimal integerPart = new BigDecimal(d.toBigInteger());
            integerPart = integerPart.negate();
            d = d.add(integerPart);
            if (d.compareTo(zero) == 0) {
                return 0;
            }
            int bitCount = 0;

            //TODO OPTIMIZE
            d = d.multiply(two);
            while (d.compareTo(one) < 0) {
                d = d.multiply(two);
                bitCount--;
            }
            return bitCount;
        }
    }

    /**
     * Returns the sign of this BigNumber.
     * 
     * @return -1, 0, or 1
     */
    public int sign() {
        if (javaBigInteger != null) {
            return javaBigInteger.signum();
        }
        if (javaBigDecimal != null) {
            return javaBigDecimal.signum();
        }

        return 0;
    }

    /**
     * Returns the precision of this BigNumber.
     *
     * @return
     */
    public int getPrecision() {
        return iPrecision;
    }

    /**
     * Return a decimal representation of this BigNumber.
     *
     * @param aNumber
     * @return
     */
    BigDecimal getDecimal(BigNumber aNumber) {
        if (aNumber.javaBigDecimal != null) {
            return aNumber.javaBigDecimal;
        }
        return new BigDecimal(aNumber.javaBigInteger);
    }
}
