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
import java.util.Iterator;
import java.util.Vector;

import org.neuroph.core.NeuralNetwork;

/**
 * Base class for all unsupervised learning algorithms.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
abstract public class UnsupervisedLearning extends IterativeLearning implements
		Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new unsupervised learning rule
	 */
	public UnsupervisedLearning() {
		super();
	}

	/**
	 * Creates new unsupervised learning rule and sets the neural network to train
	 * @param neuralNetwork
	 *            neural network to train
	 */
	public UnsupervisedLearning(NeuralNetwork neuralNetwork) {
		super(neuralNetwork);
	}

	/**
	 * This method does one learning epoch for the unsupervised learning rules.
	 * It iterates through the training set and trains network weights for each
	 * element
	 * 
	 * @param trainingSet
	 *            training set for training network
	 */
	public void doLearningEpoch(TrainingSet trainingSet) {
		Iterator<TrainingElement> iterator = trainingSet.iterator();
		while (iterator.hasNext() && !isStopped()) {
			TrainingElement trainingElement = iterator.next();
			learnPattern(trainingElement);
		}
	}	
	
	/**
	 * Trains network with the pattern from the specified training element
	 * 
	 * @param trainingElement
	 *            unsupervised training element which contains network input
	 */
	protected void learnPattern(TrainingElement trainingElement) {
		Vector<Double> input = trainingElement.getInput();
		this.neuralNetwork.setInput(input);
		this.neuralNetwork.calculate();
		this.adjustWeights();
	}



	/**
	 * This method implements the weight adjustment
	 */
	abstract protected void adjustWeights();

}