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

package org.neuroph.core.transfer;

import java.io.Serializable;
import org.neuroph.util.Properties;

/**
 * <pre>
 * Sigmoid neuron transfer function.
 * 
 * output = 1/(1+ e^(-slope*input))
 * </pre>
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Sigmoid extends TransferFunction implements Serializable {
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */		
	private static final long serialVersionUID = 2L;
	
	/**
	 * The slope parametetar of the sigmoid function
	 */
	private double slope = 1;

	/**
	 * Creates an instance of Sigmoid neuron transfer function with default
	 * slope=1.
	 */	
	public Sigmoid() {
	}

	/**
	 * Creates an instance of Sigmoid neuron transfer function with specified
	 * value for slope parametar.
	 * @param slope the slope parametar for the sigmoid function
	 */
	public Sigmoid(double slope) {
		this.slope = slope;
	}

	/**
	 * Creates an instance of Sigmoid neuron transfer function with the
	 * specified properties.
	 * @param properties properties of the sigmoid function
	 */	
	public Sigmoid(Properties properties) {
		try {
			this.slope = (Double)properties.getProperty("transferFunction.slope");
		} catch (NullPointerException e) {
			// if properties are not set just leave default values
		} catch (NumberFormatException e) {
			System.err.println("Invalid transfer function properties! Using default values.");
		}
	}
	
	/**
	 * Returns the slope parametar of this function
	 * @return  slope parametar of this function 
	 */
	public double getSlope() {
		return this.slope;
	}

	/**
	 * Sets the slope parametar for this function
	 * @param slope value for the slope parametar
	 */
	public void setSlope(Double slope) {
		this.slope = slope.doubleValue();
	}

	@Override
	public double getOutput(double net) {
		double den = (double) (1 + Math.exp(-this.slope * net));
		return (1 / den);
	}

	@Override
	public double getDerivative(double net) {
		double out = getOutput(net);
		double derivative = this.slope * out * (1 - out);
		return derivative;
	}

}
