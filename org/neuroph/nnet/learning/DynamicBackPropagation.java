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

package org.neuroph.nnet.learning;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.TrainingSet;


/**
 * Backpropagation learning rule with dynamic learning rate and momentum
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class DynamicBackPropagation extends MomentumBackpropagation{

    private double maxLearningRate = 0.9;
    private double minLearningRate = 0.1;
    private double learningRateChange = 0.99926;
    private boolean useDynamicLearningRate = true;
   
    private double maxMomentum = 0.9;
    private double minMomentum = 0.1;
    private double momentumChange = 0.99926;
    private boolean useDynamicMomentum = true;

   // private double previousNetworkError;


        public DynamicBackPropagation() {
            super();
        }

	public DynamicBackPropagation(NeuralNetwork neuralNetwork) {
		super(neuralNetwork);
	}

        // Adjusting learning rate dynamically
        /* If network error of current epoch is higher than the network error of the previous
         * epoch the learning rate is adjusted by minus 1 per cent of current learning rate.
         * Otherwise the learning rate is adjusted by plus 1 per cent of current learning
         * rate. So, learning rate increases faster than decreasing does. But if learning rate
         * reaches 0.9 it switches back to 0.5 to avoid endless training. The lowest learning
         * rate is 0.5 also to avoid endless training.
         */
        protected void adjustLearningRate() {
            // 1. First approach - probably the best
            // bigger error -> smaller learning rate; minimize the error growth
            // smaller error -> bigger learning rate; converege faster
            // the amount of earning rate change is proportional to error change - by using errorChange

            double errorChange = this.previousEpochError - this.totalNetworkError;
            this.learningRate = this.learningRate + (errorChange*learningRateChange);

            if (this.learningRate > this.maxLearningRate)
               this.learningRate = this.maxLearningRate;

            if (this.learningRate < this.minLearningRate)
               this.learningRate = this.minLearningRate;

//            System.out.println("Learning rate: "+this.learningRate);

            // 2. Second approach
            // doing this lineary for each epoch considering network error behaviour
            // probbaly the worst one
/*
            if (this.totalNetworkError >= this.totalNetworkErrorInPreviousEpoch) {
                this.learningRate = this.learningRate * this.learningRateChange;

                if (this.learningRate < this.minLearningRate)
                    this.learningRate = this.minLearningRate;

            } else {
                this.learningRate = this.learningRate * (1 + (1 - this.learningRateChange)); // *1.01

                if (this.learningRate > this.maxLearningRate)
                    this.learningRate = this.maxLearningRate;

            }
*/
// third approach used by sharky nn
// By default It starts with ni = 0,9, and after each epoch ni is changed by: 0,99977 ^ N
//    where N is number of points, and ^ is power.
// ni = ni * 0,99977 ^ N
// this one drops the learning rate too fast
//           this.learningRate = this.learningRate * Math.pow(learningRateChange, this.getTrainingSet().size());
//            if (this.learningRate > this.maxLearningRate)
//               this.learningRate = this.maxLearningRate;
//
//            if (this.learningRate < this.minLearningRate)
//               this.learningRate = this.minLearningRate;

  //          System.out.println("Iteration: "+currentIteration + " Learning rate: "+ this.learningRate);
        }

        protected void adjustMomentum() {
            double errorChange = this.previousEpochError - this.totalNetworkError;
            this.momentum = this.momentum + (errorChange*momentumChange);

            if (this.momentum > this.maxMomentum)
               this.momentum = this.maxMomentum;

            if (this.momentum < this.minMomentum)
               this.momentum = this.minMomentum;

//            System.out.println("Iteration: "+currentIteration + " Momentum: "+ this.momentum);
        }

        @Override
	public void doLearningEpoch(TrainingSet trainingSet) {
           super.doLearningEpoch(trainingSet);

           if (currentIteration > 0) {
                    if (useDynamicLearningRate) adjustLearningRate();
                    if (useDynamicMomentum) adjustMomentum();
           }


        }

    public double getLearningRateChange() {
        return learningRateChange;
    }

    public void setLearningRateChange(double learningRateChange) {
        this.learningRateChange = learningRateChange;
    }

    public double getMaxLearningRate() {
        return maxLearningRate;
    }

    public void setMaxLearningRate(double maxLearningRate) {
        this.maxLearningRate = maxLearningRate;
    }

    public double getMaxMomentum() {
        return maxMomentum;
    }

    public void setMaxMomentum(double maxMomentum) {
        this.maxMomentum = maxMomentum;
    }

    public double getMinLearningRate() {
        return minLearningRate;
    }

    public void setMinLearningRate(double minLearningRate) {
        this.minLearningRate = minLearningRate;
    }

    public double getMinMomentum() {
        return minMomentum;
    }

    public void setMinMomentum(double minMomentum) {
        this.minMomentum = minMomentum;
    }

    public double getMomentumChange() {
        return momentumChange;
    }

    public void setMomentumChange(double momentumChange) {
        this.momentumChange = momentumChange;
    }

    public boolean getUseDynamicLearningRate() {
        return useDynamicLearningRate;
    }

    public void setUseDynamicLearningRate(boolean useDynamicLearningRate) {
        this.useDynamicLearningRate = useDynamicLearningRate;
    }

    public boolean getUseDynamicMomentum() {
        return useDynamicMomentum;
    }

    public void setUseDynamicMomentum(boolean useDynamicMomentum) {
        this.useDynamicMomentum = useDynamicMomentum;
    }



    

}
