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

package org.neuroph.nnet;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.LayerFactory;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.NeuronProperties;

/**
 * Adaline neural network architecture with LMS learning rule.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Adaline extends NeuralNetwork {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new Adaline network with specified number of neurons in input
	 * layer
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 */
	public Adaline(int inputNeuronsCount) {
		this.createNetwork(inputNeuronsCount);
	}

	/**
	 * Creates adaline network architecture with specified number of input neurons
	 * 
	 * @param inputNeuronsCount
         *              number of neurons in input layer
	 */
	private void createNetwork(int inputNeuronsCount) {

		// createLayer neuron settings for this network
		NeuronProperties neuronProperties = new NeuronProperties();

		// set network type code
		this.setNetworkType(NeuralNetworkType.ADALINE);

		// createLayer input layer with specified number of neurons
		Layer inputLayer = LayerFactory.createLayer(inputNeuronsCount, neuronProperties);
		this.addLayer(inputLayer);

		// createLayer output layer (only one neuron)
		Layer outputLayer = LayerFactory.createLayer(1, neuronProperties);
		this.addLayer(outputLayer);

		// createLayer full conectivity between input and output layer
		ConnectionFactory.fullConnect(inputLayer, outputLayer);

		// set input and output cells for network
		NeuralNetworkFactory.setDefaultIO(this);

		// set LMS learning rule for this network
		this.setLearningRule(new LMS(this));
	}

}