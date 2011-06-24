/*
 * $Id: ArrayUtil.java 3213 2010-07-05 14:17:57Z kredel $
 */

package edu.jas.util;

import edu.jas.structure.Complex;

/**
 * Array utilities.
 * 
 * @author Heinz Kredel
 */

public class ArrayUtil {

	/**
	 * * Copy the specified array.
	 * 
	 * @param original
	 *            array.
	 * @param newLength
	 *            new array length.
	 * @return copy of this.
	 */
	public static Complex[] copyOfComplex(Complex[] original, int newLength) {
		Complex[] copy = new Complex[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

}
