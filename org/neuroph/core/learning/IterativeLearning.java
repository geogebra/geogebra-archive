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
import org.neuroph.core.NeuralNetwork;

/**
 * Base class for all iterative learning algorithms.
 * It provides the iterative learning procedure for all of its subclasses.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
abstract public class IterativeLearning extends LearningRule implements
		Serializable {

	/**
	 * The class fingerprint that is set to indicate serialization 
	 * compatibility with a previous version of the class
	 */		
	private static final long serialVersionUID = 1L;

	/**
	 * Learning rate parametar
	 */
	protected double learningRate = 0.1;

	/**
	 * Current iteration counter
	 */
	protected int currentIteration = 0;
	
	/**
	 * Max training iterations (when to stopLearning training)
         * TODO: this field should be private, to force use of setMaxIterations from derived classes, so
         * iterationsLimited flag is also set at the sam etime.Wil that break backward compatibility with serialized networks?
	 */
	protected int maxIterations = Integer.MAX_VALUE;

	/**
	 * Flag for indicating if the training iteration number is limited
	 */
	protected boolean iterationsLimited = false;

        /**
         * Flag for indicating if learning thread is paused
         */
        private transient boolean pausedLearning = false;

	/**
	 * Creates new instannce of IterativeLearning learning algorithm
	 */
	public IterativeLearning() {
		super();
	}

	/**
	 * Creates new instannce of IterativeLearning learning algorithm for the
     * specified neural network.
	 * 
	 * @param network
	 *            neural network to train
	 */
	public IterativeLearning(NeuralNetwork network) {
		super(network);
	}

	/**
	 * Returns learning rate for this algorithm
	 * 
	 * @return learning rate for this algorithm
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * Sets learning rate for this algorithm
	 * 
	 * @param learningRate
	 *            learning rate for this algorithm
	 */
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	
	/**
	 * Sets iteration limit for this learning algorithm
	 * 
	 * @param maxIterations
	 *            iteration limit for this learning algorithm
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
                this.iterationsLimited = true;
	}	

	/**
	 * Returns current iteration of this learning algorithm
	 * 
	 * @return current iteration of this learning algorithm
	 */
	public Integer getCurrentIteration() {
		return new Integer(this.currentIteration);
	}

        /**
         * Returns true if learning thread is paused, false otherwise
         * @return true if learning thread is paused, false otherwise
         */
        public boolean isPausedLearning() {
            return pausedLearning;
        }

        /**
         * Pause the learning
         */
        public void pause() {
             this.pausedLearning = true;
        }

        /**
         * Resumes the paused learning
         */
        public void resume() {
            this.pausedLearning = false;
            synchronized(this) {
                 this.notify();
            }
        }

        /**
         * Reset the iteration counter
         */
        protected void reset() {
            this.currentIteration = 0;
        }

        @Override
        public void learn(TrainingSet trainingSet) {				
                this.reset();

		while(!isStopped()) {
			doLearningEpoch(trainingSet);
			this.currentIteration++;
			if (iterationsLimited && (currentIteration == maxIterations)) {
				stopLearning();
			} else if (!iterationsLimited && (currentIteration == Integer.MAX_VALUE)){
                           // restart iteration counter since it has reached max value and iteration numer is not limited
                           this.currentIteration = 1; 
                        }

			this.notifyChange(); // notify observers

                        // Thread safe pause
                        if (this.pausedLearning == true)
                            synchronized (this) {
                                while (this.pausedLearning) {
                                    try {
                                        this.wait();
                                    }
                                    catch (Exception e) { }
                                }
                        }

		}
	}

        /**
         * Trains network for the specified training set and number of iterations
         * @param trainingSet training set to learn
         * @param maxIterations maximum numberof iterations to learn
         *
         */
        public void learn(TrainingSet trainingSet, int maxIterations) {
            this.setMaxIterations(maxIterations);
            this.learn(trainingSet);
        }

        /**
         * Runs one learning iteration for the specified training set and notfies observers.
         * This method does the the doLearningEpoch() and in addtion notifes observrs when iteration is done.
         * @param trainingSet training set to learn
         */
        public void doOneLearningIteration(TrainingSet trainingSet) {
            this.doLearningEpoch(trainingSet);
            this.notifyChange(); // notify observers
        }
	
	/**
	 * Override this method to implement specific learning epoch - one learning iteration, one pass through whole training set
	 * 
	 * @param trainingSet
	 *            training set
	 */
	abstract public void doLearningEpoch(TrainingSet trainingSet);	

}