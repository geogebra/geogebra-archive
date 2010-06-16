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
 * Represents training element for supervised learning algorithms.
 * Each supervised training element contains network input and desired network output.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class SupervisedTrainingElement extends TrainingElement implements
		Serializable {

	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Desired output for this training element
	 */
	private Vector<Double> desiredOutput;

	/**
	 * Creates new training element with specified input and desired output
	 * vectors
	 * 
	 * @param input
	 *            input vector
	 * @param desiredOutput
	 *            desired output vector
	 */
	public SupervisedTrainingElement(Vector<Double> input, Vector<Double> desiredOutput) {
		super(input);
		this.desiredOutput = desiredOutput;
	}

	/**
	 * Creates new training element with specified input and desired output
	 * vectors specifed as strings
	 * 
	 * @param input
	 *            input vector as space separated string
	 * @param desiredOutput
	 *            desired output vector as space separated string
	 */
	public SupervisedTrainingElement(String input, String desiredOutput) {
		super(input);
		this.desiredOutput = VectorParser.parseDouble(desiredOutput);
	}

	/**
	 * Creates new training element with specified input and desired output
	 * vectors
	 *
	 * @param input
	 *            input array
	 * @param desiredOutput
	 *            desired output array
	 */
	public SupervisedTrainingElement(double[] input, double[] desiredOutput) {
		super(input);

        this.desiredOutput = new Vector<Double>();
        for(int i=0; i<desiredOutput.length; i++)
            this.desiredOutput.add(new Double(desiredOutput[i]));
	}

	/**
	 * Returns desired output for this training element
	 * 
	 * @return desired output vector
	 */
	public Vector<Double> getDesiredOutput() {
		return this.desiredOutput;
	}

	/**
	 * Sets desired output vector for this training element
	 * 
	 * @param desiredOutput
	 *            desired output vector
	 */
	public void setDesiredOutput(Vector<Double> desiredOutput) {
		this.desiredOutput = desiredOutput;
	}
}