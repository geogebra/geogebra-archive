/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.neuroph.util;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Provides methods to parse strings as Integer or Double vectors.
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class VectorParser {

	/**
	 * This method parses input String and returns Integer vector
	 * @param str input String
	 * @return Integer vector
	 */
	static public Vector<Integer> parseInteger(String str) {
		StringTokenizer tok = new StringTokenizer(str);
		Vector<Integer> ret = new Vector<Integer>();
		while (tok.hasMoreTokens()) {
			Integer d = new Integer(tok.nextToken());
			ret.add(d);
		}
		return ret;
	}

	/**
	 * This method parses input String and returns Double vector
	 * @param str input String
	 * @return Double vector
	 */	
	static public Vector<Double> parseDouble(String str) {
		StringTokenizer tok = new StringTokenizer(str);
		Vector<Double> ret = new Vector<Double>();
		while (tok.hasMoreTokens()) {
			Double d = new Double(tok.nextToken());
			ret.add(d);
		}
		return ret;
	}

	public static Vector<Double> convertToVector(double[] array) {
		Vector<Double> vector = new Vector<Double>(array.length);
		
		for(double val : array) {
			vector.add(val);
		}
		
		return vector;
	}
	
	public static double[] convertToArray(Vector<Double> vector) {
		double[] array = new double[vector.size()];
		
		int i = 0;
		for(double d : vector) {
			array[i++] = d;
		}
		
		return array;
	}
}