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

package org.neuroph.core.input;

import java.io.Serializable;
import java.util.Vector;

/**
 * Performs max function on input vector
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Max extends SummingFunction implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;
	
	public double getOutput(Vector<Double> inputVector) {
		double max = 0;
		for(Double input : inputVector) {
			if (input.doubleValue() > max)
				max = input.doubleValue();
		}
		return max;
	}

}
