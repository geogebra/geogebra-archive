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

package org.neuroph.core.transfer;

import java.io.Serializable;

/**
 * Abstract base class for all neuron tranfer functions.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 * @see org.neuroph.core.Neuron
 */
abstract public class TransferFunction implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */		
	private static final long serialVersionUID = 1L;	

	/**
	 * Returns the ouput of this function.
	 * 
	 * @param net
	 *            net input
	 */
	abstract public double getOutput(double net);

	/**
	 * Returns the first derivative of this function.
	 * 
	 * @param net
	 *            net input
	 */
	public double getDerivative(double net) {
		return 1d;
	}

	/**
	 * Returns the class name
	 * @return class name
	 */
	@Override
	public String toString() {
		return getClass().getName();
	}
}
