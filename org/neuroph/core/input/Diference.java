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

import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;

/**
 * Performs the vector difference operation on input and
 * weight vector.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Diference extends WeightsFunction implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */		
	private static final long serialVersionUID =21L;
	

	public Vector<Double> getOutput(Vector<Connection> inputConnections) {
		Vector<Double> inputVector = new Vector<Double>();

		for(Connection connection : inputConnections) {
			Neuron neuron = connection.getConnectedNeuron();
			
			Weight weight = connection.getWeight();
			double input = neuron.getOutput() - weight.getValue();
			
			inputVector.addElement(input);
		}

		return inputVector;
	}

}
