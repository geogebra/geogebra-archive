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

package org.neuroph.core.learning;

import java.io.Serializable;
import java.util.Observable;

import org.neuroph.core.NeuralNetwork;

/**
 * Base class for all neural network learning algorithms.
 * It provides the general principles for training neural network.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
abstract public class LearningRule extends Observable implements Runnable,
		Serializable {

	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Neural network to train
	 */
	protected NeuralNetwork neuralNetwork;

	/**
	 * Collection of training elements
	 */
	private transient TrainingSet trainingSet;
	
	/**
	 * Flag to stop learning
	 */
	private transient boolean stopLearning=false;		

	/**
	 * Creates new instance of learning rule
	 */
	public LearningRule() {	}

	/**
	 * Sets neural network for this learning rule
	 * 
	 * @param network
	 *            neural network to train
	 */
	public LearningRule(NeuralNetwork network) {
		this.neuralNetwork = network;
	}

	/**
	 * Sets training set for this learning rule
	 * 
	 * @param trainingSet
	 *            training set for this learning rule
	 */
	public void setTrainingSet(TrainingSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	/**
	 * Gets training set
	 * 
	 * @return training set
	 */
	public TrainingSet getTrainingSet() {
		return trainingSet;
	}

        /**
         * Gets neural network
         * @return neural network
         */
        public NeuralNetwork getNeuralNetwork() {
            return neuralNetwork;
        }

        /**
         * Sets neural network for this learning rule
         * @param neuralNetwork
         *              neural network for this learning rule
         */
        public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
            this.neuralNetwork = neuralNetwork;
        }

	/**
	 * Method from Runnable interface for running learning procedure in separate
	 * thread.
	 */
        // Yves Kreis -->
        //@Override
        // <-- Yves Kreis
	public void run() {
		this.stopLearning = false;
		this.learn(this.trainingSet);
	}
	
	/**
	 * Stops learning
	 */
	public void stopLearning() {
		this.stopLearning = true;
	}	
	
	/**
	 * Returns true if learning has stopped, false otherwise
	 * @return true if learning has stopped, false otherwise
	 */
	public boolean isStopped() {
		return this.stopLearning;
	}

	/**
	 * Notify observers about change
	 */
	protected void notifyChange() {
		setChanged();
		notifyObservers();
		clearChanged();
	}

	/**
	 * Override this method to implement specific learning procedures
	 * 
	 * @param trainingSet
	 *            training set
	 */
	abstract public void learn(TrainingSet trainingSet);

}