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


import java.util.Iterator;
import java.util.Vector;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.comp.BiasNeuron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.LayerFactory;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;

/**
 *  Multi Layer Perceptron neural network with Back propagation learning algorithm.
 *
 *  @see org.neuroph.nnet.learning.BackPropagation
 *  @see org.neuroph.nnet.learning.MomentumBackpropagation
 *  @author Zoran Sevarac <sevarac@gmail.com>
 */
public class MultiLayerPerceptron extends NeuralNetwork {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 2L;

	/**
	 * Creates new MultiLayerPerceptron with specified number of neurons in layers
	 * 
	 * @param neuronsInLayers
	 *            collection of neuron number in layers
	 */
	public MultiLayerPerceptron(Vector<Integer> neuronsInLayers) {
		// init neuron settings
		NeuronProperties neuronProperties = new NeuronProperties();
                neuronProperties.setProperty("useBias", true);
		neuronProperties.setProperty("transferFunction", TransferFunctionType.SIGMOID);

		this.createNetwork(neuronsInLayers, neuronProperties);
	}
	
	public MultiLayerPerceptron(int ... neuronsInLayers) {
		// init neuron settings
		NeuronProperties neuronProperties = new NeuronProperties();
                neuronProperties.setProperty("useBias", true);
		neuronProperties.setProperty("transferFunction",
				TransferFunctionType.SIGMOID);

		Vector<Integer> neuronsInLayersVector = new Vector<Integer>();
		for(int i=0; i<neuronsInLayers.length; i++)
			neuronsInLayersVector.add(new Integer(neuronsInLayers[i]));
		
		this.createNetwork(neuronsInLayersVector, neuronProperties);
	}

	public MultiLayerPerceptron(TransferFunctionType transferFunctionType, int ... neuronsInLayers) {
		// init neuron settings
		NeuronProperties neuronProperties = new NeuronProperties();
                neuronProperties.setProperty("useBias", true);
		neuronProperties.setProperty("transferFunction", transferFunctionType);

		Vector<Integer> neuronsInLayersVector = new Vector<Integer>();
		for(int i=0; i<neuronsInLayers.length; i++)
			neuronsInLayersVector.add(new Integer(neuronsInLayers[i]));

		this.createNetwork(neuronsInLayersVector, neuronProperties);
	}

	public MultiLayerPerceptron(Vector<Integer> neuronsInLayers, TransferFunctionType transferFunctionType) {
		// init neuron settings
		NeuronProperties neuronProperties = new NeuronProperties();
                neuronProperties.setProperty("useBias", true);
		neuronProperties.setProperty("transferFunction", transferFunctionType);

		this.createNetwork(neuronsInLayers, neuronProperties);
	}

	/**
	 * Creates new MultiLayerPerceptron net with specified number neurons in
	 * getLayersIterator
	 * 
	 * @param neuronsInLayers
	 *            collection of neuron numbers in layers
	 * @param neuronProperties
	 *            neuron propreties
	 */
	public MultiLayerPerceptron(Vector<Integer> neuronsInLayers,NeuronProperties neuronProperties) {
		this.createNetwork(neuronsInLayers, neuronProperties);
	}

	/**
	 * Creates MultiLayerPerceptron Network architecture - fully connected
	 * feedforward with specified number of neurons in each layer
	 * 
	 * @param neuronsInLayers
	 *            collection of neuron numbers in getLayersIterator
	 * @param neuronProperties
	 *            neuron propreties
	 */
	private void createNetwork(Vector<Integer> neuronsInLayers, NeuronProperties neuronProperties) {

		// set network type
		this.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

                // create input layer
                NeuronProperties inputNeuronProperties = new NeuronProperties(TransferFunctionType.LINEAR);
                Layer layer = LayerFactory.createLayer(neuronsInLayers.get(0), inputNeuronProperties);

                boolean useBias = true; // use bias neurons by default
                if (neuronProperties.hasProperty("useBias")) {
                    useBias = (Boolean)neuronProperties.getProperty("useBias");
                }

                if (useBias) {
                    layer.addNeuron(new BiasNeuron());
                }

                this.addLayer(layer);

		// create layers
		Layer prevLayer = layer;

		//for(Integer neuronsNum : neuronsInLayers)
                for(int layerIdx = 1; layerIdx < neuronsInLayers.size(); layerIdx++){
                        Integer neuronsNum = neuronsInLayers.get(layerIdx);
			// createLayer layer
			layer = LayerFactory.createLayer(neuronsNum, neuronProperties);

                        if ( useBias && (layerIdx< (neuronsInLayers.size()-1)) ) {
                            layer.addNeuron(new BiasNeuron());
                        }

			// add created layer to network
			this.addLayer(layer);
			// createLayer full connectivity between previous and this layer
			if (prevLayer != null)
				ConnectionFactory.fullConnect(prevLayer, layer);

			prevLayer = layer;
		}

		// set input and output cells for network
                  NeuralNetworkFactory.setDefaultIO(this);

                  // set learnng rule
		//this.setLearningRule(new BackPropagation(this));
		this.setLearningRule(new MomentumBackpropagation());
               // this.setLearningRule(new DynamicBackPropagation());
		
	}

        public void connectInputsToOutputs() {
            ConnectionFactory.fullConnect( getLayers().firstElement(), getLayers().lastElement() , false);
        }

}