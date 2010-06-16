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

package org.neuroph.nnet.learning;

import java.io.Serializable;
import java.util.Vector;

import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.learning.SupervisedLearning;

/**
 * LMS learning rule for neural networks.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class LMS extends SupervisedLearning implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new LMS learning rule
	 */
	public LMS() {
		super();
	}

	/**
	 * Creates new LMS learning rule for specified neural network
	 * 
	 * @param neuralNetwork neural network to train
	 */
	public LMS(NeuralNetwork neuralNetwork) {
		super(neuralNetwork);
	}

	/**
	 * Updates total network error with specified pattern error vector
	 * 
	 * @param patternError
	 *            single pattern error vector
         */
        @Override
	protected void updateTotalNetworkError(Vector<Double> patternError) {
                double sqrErrorSum = 0;
		for(Double error : patternError) {
			sqrErrorSum += (error * error);
		}
                this.totalNetworkError += sqrErrorSum / (2*patternError.size());
	}

	/**
	 * This method implements weight update procedure for the whole network for
	 * this learning rule
	 * 
	 * @param patternError
	 *            single pattern error vector
	 */
        @Override
	protected void updateNetworkWeights(Vector<Double> patternError) {
		int i = 0;
		for(Neuron neuron : neuralNetwork.getOutputNeurons()) {
			Double outputError = patternError.elementAt(i);
			neuron.setError(outputError.doubleValue());
			this.updateNeuronWeights(neuron);
			i++;
		}
	}

	/**
	 * This method implements weights update procedure for the single neuron
	 * 
	 * @param neuron
	 *            neuron to update weights
	 */
	protected void updateNeuronWeights(Neuron neuron) {
                // get the error for specified neuron
                double neuronError = neuron.getError();
                // iterate through all neuron's input connections
		for(Connection connection : neuron.getInputConnections() ) {
                        // get the input from current connection
			double input = connection.getInput();
                        // calculate the weight change
			double deltaWeight = this.learningRate * neuronError * input;
                        // apply the weight change
			connection.getWeight().inc(deltaWeight);
		}
	}

}