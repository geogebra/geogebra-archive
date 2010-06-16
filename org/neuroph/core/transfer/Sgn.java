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
import java.util.Properties;

import org.neuroph.util.TransferFunctionType;

/**
 * Sgn neuron transfer function.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Sgn extends TransferFunction implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 *  y = 1, x > 0  
	 *  y = -1, x <= 0
	 */

	public double getOutput(double net) {
		if (net > 0)
			return 1;
		else
			return -1;
	}

	/**
	 * Returns the properties of this function
	 * @return properties of this function
	 */	
	public Properties getProperties() {
		Properties properties = new java.util.Properties();
		properties.setProperty("transferFunction", TransferFunctionType.SGN.toString());
		return properties;
	}

}
