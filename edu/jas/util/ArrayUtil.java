/*
 * $Id: ArrayUtil.java 3213 2010-07-05 14:17:57Z kredel $
 */

package edu.jas.util;

import edu.jas.structure.Complex;
import edu.jas.structure.RingElem;

/**
 * Array utilities.
 * 
 * @author Heinz Kredel
 */

public class ArrayUtil {	
	
	public static <C extends RingElem<C>> Complex<C>[] copyOfComplex(Complex<C>[] original, int newLength) {
		@SuppressWarnings("unchecked")
		Complex<C>[] copy = new Complex[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

}
