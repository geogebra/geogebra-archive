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

import java.lang.reflect.Constructor;

import java.lang.reflect.InvocationTargetException;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.input.SummingFunction;
import org.neuroph.core.input.WeightsFunction;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.comp.InputOutputNeuron;
import org.neuroph.nnet.comp.ThresholdNeuron;

/**
 * Provides methods to create customized instances of Neurons.
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class NeuronFactory {

	/**
	 * Creates and returns neuron instance according to the given specification in neuronProperties.
	 * 
	 * @param neuronProperties
	 *            specification of neuron properties
	 * @return returns instance of neuron with specified properties
	 */
	public static Neuron createNeuron(NeuronProperties neuronProperties) {
		WeightsFunction weightsFunction = createWeightsFunction(neuronProperties.getWeightsFunction());
		SummingFunction summingFunction = createSummingFunction(neuronProperties.getSummingFunction());
		InputFunction inputFunction = new InputFunction(weightsFunction, summingFunction);             
		TransferFunction transferFunction = createTransferFunction(neuronProperties.getTransferFunctionProperties());

		Neuron neuron = null;
		Class neuronClass = neuronProperties.getNeuronType();

                // use two param constructor to create neuron
                    try {
                        Class[] paramTypes = {InputFunction.class, TransferFunction.class};
                        Constructor con = neuronClass.getConstructor(paramTypes);

                        Object paramList[] = new Object[2];
                        paramList[0] = inputFunction;
                        paramList[1] = transferFunction;
                        
                        neuron = (Neuron) con.newInstance(paramList);

                    }  catch (NoSuchMethodException e) {
                        System.err.println("getConstructor() couldn't find the constructor while creating Neuron!");
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        System.err.println("InstantiationException while creating Neuron!");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.err.println("No permission to invoke method while creating Neuron!");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Method threw an: " + e.getTargetException() + " while creating Neuron!");
                        e.printStackTrace();
                    }

                    if (neuron == null) {
                        // use constructor without params to create neuron
                        try {
                            neuron = (Neuron) neuronClass.newInstance();
                            System.out.println("Using no arg constructor ");
                        } catch(IllegalAccessException e) {
                            System.err.println("InstantiationException while creating Neuron!");
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            System.err.println("InstantiationException while creating Neuron!");
                            e.printStackTrace();
                        }
                    }
                
                    if  (neuronProperties.hasProperty("thresh")) {
                        ((ThresholdNeuron)neuron).setThresh((Double)neuronProperties.getProperty("thresh"));
                    } else if  (neuronProperties.hasProperty("bias")) {
                        ((InputOutputNeuron)neuron).setBias((Double)neuronProperties.getProperty("bias"));
                    }

                     return neuron;                    

//		if (neuronclass.equals("InputOutputNeuron")) {
//			neuron = new InputOutputNeuron(inputFunction, transferFunction);
//			double bias = Double.parseDouble(neuronProperties.get("bias")
//					.toString());
//			((InputOutputNeuron) neuron).setBias(bias);
//		} else if (neuronclass.equals("ThresholdNeuron")) {
//			neuron = new ThresholdNeuron(inputFunction, transferFunction);
//			double thresh = Double.parseDouble(neuronProperties.get("thresh")
//					.toString());
//			((ThresholdNeuron) neuron).setThresh(thresh);
//		} else if (neuronclass.equals("DelayedNeuron")) {
//			neuron = new DelayedNeuron(inputFunction, transferFunction);
//		} else if (neuronclass.equals("CompetitiveNeuron")) {
//			neuron = new CompetitiveNeuron(inputFunction, transferFunction);
//		} else if (neuronclass.equals("BiasNeuron")) {
//			neuron = new BiasNeuron();
//		} else {
//			neuron = new Neuron(inputFunction, transferFunction);
//		}

//		return neuron;
	}

	/**
	 * Creates and returns instance of transfer function
	 * 
	 * @param tfProperties
	 *            transfer function properties
	 * @return returns transfer function
	 */
	private static TransferFunction createTransferFunction(Properties tfProperties) {
		TransferFunction transferFunction = null;

		Class  tfClass = (Class)tfProperties.getProperty("transferFunction");

                    try {
                        Class[] paramTypes = null;

                        Constructor[] cons = tfClass.getConstructors();
                        for (int i=0; i<cons.length; i++) {
                             paramTypes = cons[i].getParameterTypes();

                            // use constructor with one parameter of Properties type
                            if ((paramTypes.length == 1) && (paramTypes[0] == Properties.class)) {
                                Class argTypes[] = new Class[1];
                                argTypes[0] = Properties.class;
                                Constructor ct = tfClass.getConstructor(argTypes);

                                Object argList[] = new Object[1];
                                argList[0] = tfProperties;
                                transferFunction = (TransferFunction) ct.newInstance(argList);
                                break;
                            } else if(paramTypes.length == 0) { // use constructor without params
                                transferFunction = (TransferFunction) tfClass.newInstance();
                                break;
                            }
                        }

                        return transferFunction;
                        
                    } catch (NoSuchMethodException e) {
                        System.err.println("getConstructor() couldn't find the constructor while creating TransferFunction!");
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        System.err.println("InstantiationException while creating TransferFunction!");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.err.println("No permission to invoke method while creating TransferFunction!");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Method threw an: " + e.getTargetException() + " while creating TransferFunction!");
                        e.printStackTrace();
                    }

                    return transferFunction;

//		switch (transferFunctionType) {
//		case LINEAR:
//			transferFunction = new Linear(tfProperties);
//			break;
//		case STEP:
//			transferFunction = new Step(tfProperties);
//			break;
//		case RAMP:
//			transferFunction = new Ramp(tfProperties);
//			break;
//		case SIGMOID:
//			transferFunction = new Sigmoid(tfProperties);
//			break;
//		case TANH:
//			transferFunction = new Tanh(tfProperties);
//			break;
//		case TRAPEZOID:
//			transferFunction = new Trapezoid(tfProperties);
//			break;
//		case GAUSSIAN:
//			transferFunction = new Gaussian(tfProperties);
//			break;
//		case SGN:
//			transferFunction = new Sgn(); // isti problem kao i sa BiasNeuronom - ima samo konstruktor bez parametara
//			break;
//		default:
//			throw new RuntimeException("Unknown tranfer function type!");
//		} // switch
	}

	/**
	 * Creates and returns instance of specified weights function.
	 * 
	 * @param weightsFunctionClass
	 *            weights function class
         * 
	 * @return returns instance of weights function.
	 */
        private static WeightsFunction createWeightsFunction(Class weightsFunctionClass) {
            WeightsFunction weightsFunction = null;

            try {
                weightsFunction = (WeightsFunction) weightsFunctionClass.newInstance();
            } catch (InstantiationException e) {
                System.err.println("InstantiationException while creating WeightsFunction!");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("No permission to invoke method");
                e.printStackTrace();
            }

            return weightsFunction;
        }

	/**
	 * Creates and returns instance of specified summing function.
	 * 
	 * @param summingFunctionClass
         *              summing function class
         *
	 * @return returns instance of summing function
	 */
	private static SummingFunction createSummingFunction(Class summingFunctionClass) {
		SummingFunction summingFunction = null;

                try {
                    summingFunction = (SummingFunction) summingFunctionClass.newInstance();
                } catch (InstantiationException e) {
                    System.err.println("InstantiationException while creating SummingFunction!");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.err.println("No permission to invoke method");
                    e.printStackTrace();
                }
                
		return summingFunction;
	}

}