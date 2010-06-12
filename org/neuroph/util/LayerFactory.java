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

import java.util.Vector;

import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;

/**
 * Provides methods to create instance of a Layer with specifed number of neurons and neuron's properties.
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class LayerFactory {

	public static Layer createLayer(int neuronsCount, NeuronProperties neuronProperties) {
		Layer layer = new Layer(neuronsCount, neuronProperties);
        	return layer;
	}

	public static Layer createLayer(int neuronsCount, TransferFunctionType transferFunctionType) {
		NeuronProperties neuronProperties = new NeuronProperties();
		neuronProperties.setProperty("transferFunction", transferFunctionType);
		Layer layer = createLayer(neuronsCount, neuronProperties);
		return layer;
	}

	public static Layer createLayer(Vector<NeuronProperties> neuronPropertiesVector) {
		Layer layer = new Layer();
		
		for(NeuronProperties neuronProperties : neuronPropertiesVector) {
			Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
			layer.addNeuron(neuron);
		}
		
		return layer;
	}

}