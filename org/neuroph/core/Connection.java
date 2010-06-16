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

package org.neuroph.core;

import java.io.Serializable;

/**
 * Weighted connection to another neuron.
 * 
 * @see Weight
 * @see Neuron
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Connection implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Connected neuron
	 */
	protected Neuron connectedNeuron;

	/**
	 * Weight for this connection
	 */
	protected Weight weight;

	/**
	 * Creates a new connection to specified neuron with random weight
	 * 
	 * @param connectTo
	 *            neuron to connect to
	 */
	public Connection(Neuron connectTo) {
		this.connectedNeuron = connectTo;
		this.weight = new Weight();
	}

	/**
	 * Creates a new connection to specified neuron with specified weight object
	 * 
	 * @param connectTo
	 *            neuron to connect to
	 * @param weight
	 *            weight for this connection
	 */
	public Connection(Neuron connectTo, Weight weight) {
		this.connectedNeuron = connectTo;
		this.weight = weight;
	}

	/**
	 * Creates a new connection to specified neuron with specified weight value
	 * 
	 * @param connectTo
	 *            neuron to connect to
	 * @param weightVal
	 *            weight value for this connection
	 */
	public Connection(Neuron connectTo, double weightVal) {
		this.connectedNeuron = connectTo;
		this.weight = new Weight(weightVal);
	}

	/**
	 * Creates a new connection between specified neurons with random weight value
	 * 
	 * @param from
	 *            neron to connect
	 * @param connectTo
	 *            neuron to connect to
	 */
	public Connection(Neuron from, Neuron connectTo) {
		this.connectedNeuron = connectTo;
		this.weight = new Weight();
		from.addInputConnection(this);
	}

	/**
	 * Returns weight for this connection
	 * 
	 * @return weight for this connection
	 */
	public Weight getWeight() {
		return this.weight;
	}

	/**
	 * Returns the connected neuron of this connection
	 * 
	 * @return connected neuron of this connection
	 */
	public Neuron getConnectedNeuron() {
		return this.connectedNeuron;
	}

	/**
	 * Returns input received through this connection - the activation that
	 * comes from the output of the cell on the other end of connection
	 * 
	 * @return input received through this connection
	 */
	public double getInput() {
		return this.connectedNeuron.getOutput();
	}

	/**
	 * Returns the weighted input received through this connection
	 * 
	 * @return weighted input received through this connection
	 */
	public double getWeightedInput() {
		return getInput() * weight.getValue();
	}

}
