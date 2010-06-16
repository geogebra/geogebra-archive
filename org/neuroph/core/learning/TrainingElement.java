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
import java.util.Vector;

import org.neuroph.util.VectorParser;

/**
 * Represents single training element for neural network learning.
 * This class contains only network input and it is used for unsupervised learning algorithms.
 * It is also the base class for SupervisedTrainingElement.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class TrainingElement implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Input vector for this training element
	 */
	protected Vector<Double> input;

        /**
         * Label for this training element
         */
        protected String label;

	/**
	 * Creates new training element with specified input vector
	 */
	public TrainingElement() {
		this.input = new Vector<Double>();
	}

	/**
	 * Creates new training element with specified input vector
	 * 
	 * @param input
	 *            input vector
	 */
	public TrainingElement(Vector<Double> input) {
		this.input = input;
	}

	/**
	 * Creates new training element with specified input vector
	 * 
	 * @param input
	 */
	public TrainingElement(String input) {
		this.input = VectorParser.parseDouble(input);
	}

	/**
	 * Creates new training element with input array
	 *
	 * @param input
	 *            input array
	 */
	public TrainingElement(double ... input) {
        this.input = new Vector<Double>();
        for(int i=0; i<input.length; i++)
            this.input.add(new Double(input[i]));
	}

	/**
	 * Returns input vector
	 * 
	 * @return input vector
	 */
	public Vector<Double> getInput() {
		return this.input;
	}

	/**
	 * Sets input vector
	 * 
	 * @param input
	 *            input vector
	 */
	public void setInput(Vector<Double> input) {
		this.input = input;
	}

        /**
         * Get training element label
         * @return training element label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Set training element label
         * @param label label for this training element
         */
        public void setLabel(String label) {
            this.label = label;
        }



}